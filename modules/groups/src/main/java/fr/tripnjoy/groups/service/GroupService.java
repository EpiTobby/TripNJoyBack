package fr.tripnjoy.groups.service;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.groups.QRCodeGenerator;
import fr.tripnjoy.groups.dto.request.CreatePrivateGroupRequest;
import fr.tripnjoy.groups.dto.request.JoinGroupWithoutInviteModel;
import fr.tripnjoy.groups.dto.request.UpdatePrivateGroupRequest;
import fr.tripnjoy.groups.dto.request.UpdatePublicGroupRequest;
import fr.tripnjoy.groups.dto.response.GroupInfoModel;
import fr.tripnjoy.groups.dto.response.GroupMemberModel;
import fr.tripnjoy.groups.dto.response.GroupMemberPublicInfoModel;
import fr.tripnjoy.groups.dto.response.GroupMemoriesResponse;
import fr.tripnjoy.groups.entity.GroupEntity;
import fr.tripnjoy.groups.entity.GroupMemberEntity;
import fr.tripnjoy.groups.entity.GroupMemoryEntity;
import fr.tripnjoy.groups.entity.StateEntity;
import fr.tripnjoy.groups.exception.*;
import fr.tripnjoy.groups.model.GroupModel;
import fr.tripnjoy.groups.model.State;
import fr.tripnjoy.groups.repository.GroupMemberRepository;
import fr.tripnjoy.groups.repository.GroupMemoryRepository;
import fr.tripnjoy.groups.repository.GroupRepository;
import fr.tripnjoy.profiles.api.client.ProfileFeignClient;
import fr.tripnjoy.profiles.dto.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotConfirmedException;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import fr.tripnjoy.users.api.response.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemoryRepository groupMemoryRepository;

    private final QRCodeGenerator qrCodeGenerator;
    private final String qrCodeSecret;
    private final UserFeignClient userClient;
    private final ProfileFeignClient profileFeignClient;

    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, GroupMemoryRepository groupMemoryRepository,
                        QRCodeGenerator qrCodeGenerator, @Value("${qrcode.secret}") final String qrCodeSecret, UserFeignClient userClient,
                        final ProfileFeignClient profileFeignClient)
    {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.groupMemoryRepository = groupMemoryRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrCodeSecret = qrCodeSecret;
        this.userClient = userClient;
        this.profileFeignClient = profileFeignClient;
    }

    public boolean isInGroup(final long groupId, final long userId) {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return group.getMembers().stream().anyMatch(member -> member.getUserId() == userId);
    }

    public GroupMemberModel getMember(long groupId, long userId) {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        UserResponse user = userClient.getUserById(List.of("admin"), userId);
        return group.getMembers().stream().filter(member -> member.getUserId() == userId)
                .findAny()
                .map(member -> new GroupMemberModel(userId, user.getFirstname(), user.getLastname(), user.getProfilePicture()))
                .orElseThrow(UserNotFoundException::new);
    }

    public Collection<Long> getOpenGroups()
    {
        return groupRepository.findAvailableGroups().stream()
                              .map(GroupEntity::getId)
                              .toList();
    }

    public Collection<GroupModel> getUserGroups(long userId) {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUserId() == userId && !m.isPending())).map(GroupModel::of).toList();
    }

    public Optional<GroupModel> getGroup(long groupId)
    {
        return groupRepository.findById(groupId)
                              .map(GroupModel::of);
    }

    public Optional<GroupInfoModel> getGroupInfo(long groupId)
    {
        return groupRepository
                .findById(groupId)
                .map(GroupModel::of)
                .map(group -> {
                    var members = group.getMembers().stream()
                                       .map(member -> userClient.getUserById(List.of("admin"), member))
                                       .map(memberModel -> new GroupMemberPublicInfoModel(memberModel.getId(), memberModel.getFirstname(), memberModel.getLastname()))
                                       .toList();

                    return new GroupInfoModel(groupId, group.getName(), members, group.getMaxSize(), group.getState(), group.getPicture());
                });
    }

    public Collection<GroupModel> getUserInvites(long userId) {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUserId() == userId && m.isPending())).map(GroupModel::of).toList();
    }

    public String getOwnerEmail(long groupId) throws IllegalArgumentException {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (groupEntity.getOwner() == null)
            throw new IllegalArgumentException("The group " + groupId + " has no owner");
        try
        {
            return userClient.getUserById(List.of("admin"), groupEntity.getId()).getEmail();
        }
        catch (UnauthorizedException e)
        {
            throw new AssertionError();
        }
    }

    @Transactional
    public GroupModel createPublicGroup(long user1Entity, long profile1Entity, long user2Entity, long profile2Entity,
                                        int maxSize) {
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Date.from(Instant.now()))
                .stateEntity(maxSize > 2
                        ? StateEntity.ofModel(State.OPEN)
                        : StateEntity.ofModel(State.CLOSED))
                .members(List.of())
                .build();
        groupRepository.save(groupEntity);
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user1Entity, profile1Entity, false));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user2Entity, profile2Entity, false));
        // FIXME
//        channelService.createDefaultChannel(groupEntity);
//        promStats.getGroupCount().set(groupRepository.count());
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public GroupModel createPrivateGroup(long userId, CreatePrivateGroupRequest createPrivateGroupRequest) {
        if (!userClient.exists(userId).value())
            throw new UserNotFoundException("No user with id " + userId);
        GroupEntity groupEntity = groupRepository.save(GroupEntity.builder()
                .name(createPrivateGroupRequest.getName())
                .description(createPrivateGroupRequest.getDescription())
                .maxSize(createPrivateGroupRequest.getMaxSize())
                .createdDate(Date.from(Instant.now()))
                .owner(userId)
                .stateEntity(StateEntity.ofModel(State.CLOSED))
                .members(new ArrayList<>())
                .build());
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userId, null, false);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members.add(groupMemberEntity);
        // FIXME
//        channelService.createDefaultChannel(groupEntity);
//        promStats.getGroupCount().set(groupRepository.count());
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (!userClient.exists(userId).value())
            throw new UserNotFoundException("No user with id " + userId);
        if (groupEntity.members.stream().anyMatch(m -> m.getUserId() == userId))
            throw new UserAlreadyInGroupException("User already in group");
        // FIXME check profile exists
//        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, userId, profileId, true));
    }

    @Transactional
    public void inviteUserInPrivateGroup(long groupId, String email) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        UserResponse user = userClient.getCurrentUser(email);
        if (!user.isConfirmed())
            throw new UserNotConfirmedException("The user you want to invite is not confirmed");
        if (groupEntity.members.stream().anyMatch(m -> m.getUserId() == user.getId()))
            throw new UserAlreadyInGroupException("User already in group");
        GroupMemberEntity groupMemberEntity = groupMemberRepository.save(new GroupMemberEntity(groupEntity, user.getId(), null, true));
        groupEntity.members.add(groupMemberEntity);
    }

    @Transactional
    public void updatePrivateGroup(long groupId, UpdatePrivateGroupRequest updatePrivateGroupRequest) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (updatePrivateGroupRequest.getName() != null) {
            groupEntity.setName(updatePrivateGroupRequest.getName());
        }
        if (updatePrivateGroupRequest.getDescription() != null) {
            groupEntity.setDescription(updatePrivateGroupRequest.getDescription());
        }
        if (updatePrivateGroupRequest.getMaxSize() != null && updatePrivateGroupRequest.getMaxSize() != 0) {
            int newMaxSize = updatePrivateGroupRequest.getMaxSize();
            if (newMaxSize < groupEntity.getNumberOfNonPendingUsers())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updatePrivateGroupRequest.getState() != null) {
            if (updatePrivateGroupRequest.getState() == State.CLOSED)
                groupEntity.members.stream().filter(GroupMemberEntity::isPending).forEach(groupMemberRepository::delete);
            groupEntity.setStateEntity(StateEntity.ofModel(updatePrivateGroupRequest.getState()));
        }
        if (updatePrivateGroupRequest.getPicture() != null) {
            groupEntity.setPicture(updatePrivateGroupRequest.getPicture());
        }
        if (updatePrivateGroupRequest.getDestination() != null) {
            groupEntity.setDestination(updatePrivateGroupRequest.getDestination());
        }
        if (groupEntity.getStateEntity().getValue().equals("CLOSED")) {
            if (updatePrivateGroupRequest.getStartOfTrip() != null)
                groupEntity.setStartOfTrip(updatePrivateGroupRequest.getStartOfTrip());
            if (updatePrivateGroupRequest.getEndOfTrip() != null)
                groupEntity.setEndOfTrip(updatePrivateGroupRequest.getEndOfTrip());
        }
        if (updatePrivateGroupRequest.getOwnerId() != null)
        {
            long newOwnerId = updatePrivateGroupRequest.getOwnerId();
            if (!userClient.exists(newOwnerId).value())
                throw new UserNotFoundException(updatePrivateGroupRequest.getOwnerId());
            if (groupEntity.members.stream().anyMatch(m -> m.getUserId() == newOwnerId))
                groupEntity.setOwner(updatePrivateGroupRequest.getOwnerId());
            else
                throw new UpdateGroupException("The new owner does not exist or is not in this private group");
        }
    }

    @Transactional
    public void updatePublicGroup(long groupId, UpdatePublicGroupRequest request) throws GroupNotFoundException {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (request.getName() != null)
            groupEntity.setName(request.getName());
        if (request.getDescription() != null)
            groupEntity.setDescription(request.getDescription());

        if (request.getPicture() != null)
            groupEntity.setPicture(request.getPicture());

        if (request.getDestination() != null)
            groupEntity.setDestination(request.getDestination());

        if (groupEntity.getStateEntity().equals(StateEntity.ofModel(State.CLOSED))) {
            if (request.getStartOfTrip() != null)
                groupEntity.setStartOfTrip(request.getStartOfTrip());
            if (request.getEndOfTrip() != null)
                groupEntity.setEndOfTrip(request.getEndOfTrip());
        }
    }

    @Transactional
    public void deletePrivateGroup(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        deleteGroup(groupEntity);
        // FIXME prom
//        promStats.getGroupCount().set(groupRepository.count());
    }

    @Transactional
    protected void updateGroupState(GroupEntity groupEntity){
        if (groupEntity.getMaxSize() == groupEntity.getNumberOfNonPendingUsers()) {
            groupEntity.members.stream().filter(GroupMemberEntity::isPending).forEach(groupMemberRepository::delete);
            groupEntity.members.removeIf(GroupMemberEntity::isPending);
            groupEntity.setStateEntity(StateEntity.ofModel(State.CLOSED));
        }
    }

    @Transactional
    public void joinGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity invitedUser = groupEntity.members.stream().filter(m -> m.getUserId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not invited or does not exist"));
        invitedUser.setPending(false);
        updateGroupState(groupEntity);
    }

    @Transactional
    public void joinGroupWithoutInvite(long groupId, long userId, JoinGroupWithoutInviteModel model) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (!userClient.exists(userId).value())
            throw new UserNotFoundException(userId);
        try {
            if (!model.getMessage().equals(getEncryptedStringToJoinGroup(groupId)))
                throw new ForbiddenOperationException();
        }
        catch(NoSuchAlgorithmException e){
            throw new JoinGroupFailedException("Cannot add user to group");
        }
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userId, null, false);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members.add(groupMemberEntity);
        updateGroupState(groupEntity);
    }

    @Transactional
    public void declineGroupInvite(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity invitedUser = groupEntity.members.stream().filter(m -> m.getUserId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not invited or does not exist"));
        if (!invitedUser.isPending())
            throw new UserAlreadyInGroupException("User has joined the group so there is not invite");
        groupMemberRepository.delete(invitedUser);
    }

    @Transactional
    public void removeUserFromGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        // FIXME: planning service
//        activityRepository.findAllByGroupOrderByStartDate(groupEntity)
//                .forEach(activity -> activity.getParticipants().removeIf(member -> member.getUserId() == userId));
        GroupMemberEntity groupMemberEntity = groupEntity.members.stream().filter(m -> m.getUserId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not found in this group or does not exist"));
        groupEntity.members.removeIf(m -> m.getUserId() == userId);
        groupMemberRepository.delete(groupMemberEntity);
        if (groupEntity.getNumberOfNonPendingUsers() == 0) {
            deleteGroup(groupEntity);
        }
    }

    @Transactional
    protected void deleteGroup(GroupEntity groupEntity){
        groupRepository.delete(groupEntity);
        // FIXME: prom
//        promStats.getGroupCount().set(groupRepository.count());
    }

    @Transactional
    public void setGroupPublic(long groupId, ProfileCreationRequest profileRequest) throws GroupNotFoundException, IllegalArgumentException {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        if (group.getOwner() == null)
            throw new IllegalArgumentException("This group is already public");
        ProfileModel profile = profileFeignClient.createGroupProfile(List.of("admin"), groupId, profileRequest);
        group.setOwner(null);
    }

    public GroupMemoriesResponse getAllMemories(long groupId) {
        var group = groupRepository.findById(groupId);

        if (group.isEmpty())
            throw new GroupNotFoundException(groupId);

        List<GroupMemoryEntity> groupMemoryEntities = groupMemoryRepository.findByGroupId(groupId);
        return new GroupMemoriesResponse(groupMemoryEntities.stream().map(GroupMemoryEntity::getMemoryUrl).collect(Collectors.toList()));
    }

    @Transactional
    public GroupMemoriesResponse addMemory(long groupId, String memoryUrl){
            GroupMemoryEntity groupMemoryEntity = new GroupMemoryEntity();
            groupMemoryEntity.setGroup(groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId)));
            groupMemoryEntity.setMemoryUrl(memoryUrl);
            groupMemoryRepository.save(groupMemoryEntity);
            return new GroupMemoriesResponse(groupMemoryRepository.findByGroupId(groupId).stream().map(GroupMemoryEntity::getMemoryUrl).collect(Collectors.toList()));
        }

    private String getEncryptedStringToJoinGroup(long groupId) throws NoSuchAlgorithmException {
        String stringToHash = String.format("tripnjoy-group-qr:%o;%s",groupId,qrCodeSecret);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Arrays.toString(digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8)));
    }

    public String getQRCode(long groupId){
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        if (group.getOwner() == null)
            throw new ForbiddenOperationException("Cannot generate QR Code for public group");
        try {
            String data = String.format("%o;%s",group.getId(), getEncryptedStringToJoinGroup(group.getId()));
            return qrCodeGenerator.generateQRCode(data);
        } catch (Exception e) {
            throw new QRCodeGenerationFailedException("Cannot generate QR code for group with id:" + groupId);
        }
    }
}
