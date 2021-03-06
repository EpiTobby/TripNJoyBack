package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.GroupMemberEntity;
import fr.tobby.tripnjoyback.entity.ProfileEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePublicGroupRequest;
import fr.tobby.tripnjoyback.model.response.GroupMemberModel;
import fr.tobby.tripnjoyback.repository.*;
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
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;
    private final ChannelService channelService;
    private final ActivityRepository activityRepository;
    private final ProfileService profileService;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository,
                        ProfileRepository profileRepository, ChannelService channelService,
                        final ActivityRepository activityRepository, final ProfileService profileService)
    {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.profileRepository = profileRepository;
        this.channelService = channelService;
        this.activityRepository = activityRepository;
        this.profileService = profileService;
    }

    public boolean isInGroup(final long groupId, final long userId)
    {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return group.getMembers().stream().anyMatch(member -> member.getUser().getId().equals(userId));
    }

    public GroupMemberModel getMember(long groupId, long userId)
    {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return group.getMembers().stream().filter(member -> member.getUser().getId().equals(userId))
                    .findAny()
                    .map(member -> GroupMemberModel.of(member.getUser()))
                    .orElseThrow(UserNotFoundException::new);
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
                .description(createPrivateGroupRequest.getDescription())
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
    public void updatePrivateGroup(long groupId, UpdatePrivateGroupRequest updatePrivateGroupRequest) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (updatePrivateGroupRequest.getName() != null)
        {
            groupEntity.setName(updatePrivateGroupRequest.getName());
        }
        if (updatePrivateGroupRequest.getDescription() != null)
        {
            groupEntity.setDescription(updatePrivateGroupRequest.getDescription());
        }
        if (updatePrivateGroupRequest.getMaxSize() != null && updatePrivateGroupRequest.getMaxSize() != 0)
        {
            int newMaxSize = updatePrivateGroupRequest.getMaxSize();
            if (newMaxSize < groupEntity.getNumberOfNonPendingUsers())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updatePrivateGroupRequest.getState() != null)
        {
            if (updatePrivateGroupRequest.getState() == State.CLOSED)
                groupEntity.members.stream().filter(GroupMemberEntity::isPending).forEach(groupMemberRepository::delete);
            groupEntity.setStateEntity(updatePrivateGroupRequest.getState().getEntity());
        }
        if (updatePrivateGroupRequest.getPicture() != null){
            groupEntity.setPicture(updatePrivateGroupRequest.getPicture());
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
            if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == newOwnerId))
            {
                UserEntity newOwner = userRepository.findById(updatePrivateGroupRequest.getOwnerId())
                                                    .orElseThrow(() -> new UserNotFoundException(updatePrivateGroupRequest.getOwnerId()));
                groupEntity.setOwner(newOwner);
            }
            else
                throw new UpdateGroupException("The new owner does not exist or is not in this private group");
        }
    }

    @Transactional
    public void updatePublicGroup(long groupId, UpdatePublicGroupRequest request) throws GroupNotFoundException
    {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (request.getName() != null)
            groupEntity.setName(request.getName());
        if (request.getDescription() != null)
            groupEntity.setDescription(request.getDescription());

        if (request.getPicture() != null)
            groupEntity.setPicture(request.getPicture());

        if (groupEntity.getStateEntity().equals(State.CLOSED.getEntity()))
        {
            if (request.getStartOfTrip() != null)
                groupEntity.setStartOfTrip(request.getStartOfTrip());
            if (request.getEndOfTrip() != null)
                groupEntity.setEndOfTrip(request.getEndOfTrip());
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
        activityRepository.findAllByGroupOrderByStartDate(groupEntity)
                          .forEach(activity -> activity.getParticipants().removeIf(member -> member.getUser().getId().equals(userId)));
        GroupMemberEntity groupMemberEntity = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not found in this group or does not exist"));
        groupEntity.members.removeIf(m -> m.getUser().getId() == userId);
        groupMemberRepository.delete(groupMemberEntity);
        if (groupEntity.getNumberOfNonPendingUsers() == 0) {
            groupRepository.delete(groupEntity);
        }
    }

    @Transactional
    public void setGroupPublic(long groupId, ProfileCreationRequest profileRequest) throws GroupNotFoundException, IllegalArgumentException
    {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        if (group.getOwner() == null)
            throw new IllegalArgumentException("This group is already public");
        ProfileModel profile = profileService.createProfile(profileRequest);
        group.setProfile(profileRepository.getById(profile.getId()));
        group.setOwner(null);
    }
}
