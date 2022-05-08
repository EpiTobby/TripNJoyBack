package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.GroupMemberEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdateGroupRequest;
import fr.tobby.tripnjoyback.repository.GroupMemberRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class GroupService extends IdCheckerService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;
    private final ChannelService channelService;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository,
                        ProfileRepository profileRepository, ChannelService channelService)
    {
        super(userRepository);
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.profileRepository = profileRepository;
        this.channelService = channelService;
    }

    public Collection<GroupModel> getUserGroups(long userId)
    {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUser().getId() == userId && !m.isPending())).map(GroupModel::of).toList();
    }

    public Collection<GroupModel> getUserInvites(long userId)
    {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUser().getId() == userId && m.isPending())).map(GroupModel::of).toList();
    }

    public String getOwnerEmail(long groupId) throws IllegalArgumentException
    {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (groupEntity.getOwner() == null)
            throw new IllegalArgumentException("The group " + groupId + " has no owner");
        return groupEntity.getOwner().getEmail();
    }

    @Transactional
    public GroupModel createPublicGroup(UserEntity user1Entity, ProfileEntity profile1Entity, UserEntity user2Entity, ProfileEntity profile2Entity,
                                        int maxSize, ProfileEntity groupProfile)
    {
        GroupEntity groupEntity = GroupEntity.builder()
                                             .maxSize(maxSize)
                                             .createdDate(Date.from(Instant.now()))
                                             .stateEntity(maxSize > 2
                                                          ? State.OPEN.getEntity()
                                                          : State.CLOSED.getEntity())
                                             .members(List.of())
                                             .profile(groupProfile)
                                             .channels(new ArrayList<>())
                                             .build();
        groupRepository.save(groupEntity);
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user1Entity, profile1Entity, false));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user2Entity, profile2Entity, false));
        channelService.createDefaultChannel(groupEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public GroupModel createPrivateGroup(long userId, CreatePrivateGroupRequest createPrivateGroupRequest) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        GroupEntity groupEntity = groupRepository.save(GroupEntity.builder()
                .name(createPrivateGroupRequest.getName())
                .maxSize(createPrivateGroupRequest.getMaxSize())
                .createdDate(Date.from(Instant.now()))
                .owner(userEntity)
                .stateEntity(State.CLOSED.getEntity())
                .members(new ArrayList<>())
                .channels(new ArrayList<>())
                .build());
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userEntity, null, false);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members.add(groupMemberEntity);
        channelService.createDefaultChannel(groupEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == userEntity.getId()))
            throw new UserAlreadyInGroupException("User already in group");
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, profileEntity, true));
    }

    @Transactional
    public void inviteUserInPrivateGroup(long groupId, String email) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user with this email " + email));
        if (!userEntity.isConfirmed())
            throw new UserNotConfirmedException("The user you want to invite is not confirmed");
        if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == userEntity.getId()))
            throw new UserAlreadyInGroupException("User already in group");
        GroupMemberEntity groupMemberEntity = groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, null, true));
        groupEntity.members.add(groupMemberEntity);
    }

    @Transactional
    public void updatePrivateGroup(long groupId, UpdateGroupRequest updateGroupRequest) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (updateGroupRequest.getName() != null)
        {
            groupEntity.setName(updateGroupRequest.getName());
        }
        if (updateGroupRequest.getMaxSize() != null && updateGroupRequest.getMaxSize() != 0)
        {
            int newMaxSize = updateGroupRequest.getMaxSize();
            if (newMaxSize < groupEntity.getNumberOfNonPendingUsers())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updateGroupRequest.getState() != null)
        {
            if (updateGroupRequest.getState() == State.CLOSED)
                groupEntity.members.stream().filter(GroupMemberEntity::isPending).forEach(groupMemberRepository::delete);
            groupEntity.setStateEntity(updateGroupRequest.getState().getEntity());
        }
        if (updateGroupRequest.getPicture() != null){
            groupEntity.setPicture(updateGroupRequest.getPicture());
        }
        if (groupEntity.getStateEntity().getValue().equals("CLOSED")) {
            if (updateGroupRequest.getStartOfTrip() != null)
                groupEntity.setStartOfTrip(updateGroupRequest.getStartOfTrip());
            if (updateGroupRequest.getEndOfTrip() != null)
                groupEntity.setEndOfTrip(updateGroupRequest.getEndOfTrip());
        }
        if (updateGroupRequest.getOwnerId() != null)
        {
            long newOwnerId = updateGroupRequest.getOwnerId();
            if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == newOwnerId))
            {
                UserEntity newOwner = userRepository.findById(updateGroupRequest.getOwnerId())
                                                    .orElseThrow(() -> new UserNotFoundException(updateGroupRequest.getOwnerId()));
                groupEntity.setOwner(newOwner);
            }
            else
                throw new UpdateGroupException("The new owner does not exist or is not in this private group");
        }
    }

    @Transactional
    public void deletePrivateGroup(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        groupRepository.delete(groupEntity);
    }

    @Transactional
    public void joinGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity invitedUser = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not invited or does not exist"));
        invitedUser.setPending(false);
        if (groupEntity.getMaxSize() == groupEntity.getNumberOfNonPendingUsers()) {
            groupEntity.members.stream().filter(m -> m.isPending()).forEach(groupMemberRepository::delete);
            groupEntity.members.removeIf(m -> m.isPending());
            groupEntity.setStateEntity(State.CLOSED.getEntity());
        }
    }

    @Transactional
    public void declineGroupInvite(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity invitedUser = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not invited or does not exist"));
        if (!invitedUser.isPending())
            throw new UserAlreadyInGroupException("User has joined the groupe so there is not invite");
        groupMemberRepository.delete(invitedUser);
    }

    @Transactional
    public void removeUserFromGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity groupMemberEntity = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not found in this group or does not exist"));
        groupEntity.members.removeIf(m -> m.getUser().getId() == userId);
        groupMemberRepository.delete(groupMemberEntity);
        if (groupEntity.getNumberOfNonPendingUsers() == 0) {
            groupRepository.delete(groupEntity);
        }
    }
}
