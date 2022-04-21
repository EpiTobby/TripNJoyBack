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
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class GroupService extends IdCheckerService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository,
                        ProfileRepository profileRepository)
    {
        super(userRepository);
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.profileRepository = profileRepository;
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
                                             .build();
        groupRepository.save(groupEntity);
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user1Entity, profile1Entity, false));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, user2Entity, profile2Entity, false));
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public GroupModel createPrivateGroup(long userId, CreatePrivateGroupRequest createPrivateGroupRequest) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        GroupEntity groupEntity = GroupEntity.builder()
                                             .name(createPrivateGroupRequest.getName())
                                             .maxSize(createPrivateGroupRequest.getMaxSize())
                                             .createdDate(Date.from(Instant.now()))
                                             .owner(userEntity)
                                             .stateEntity(State.CLOSED.getEntity())
                                             .build();
        groupRepository.save(groupEntity);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userEntity, null, false);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members = List.of(groupMemberEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, profileEntity, true)));
    }

    @Transactional
    public void inviteUserInPrivateGroup(long groupId, String email) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user with this email " + email));
        if (!userEntity.isConfirmed())
            throw new UserNotConfirmedException("The user you want to invite is not confirmed");
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, null, true)));

    }

    @Transactional
    public void updatePrivateGroup(long groupId, UpdateGroupRequest updateGroupRequest) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        if (updateGroupRequest.getName() != null) {
            groupEntity.setName(updateGroupRequest.getName());
        }
        int newMaxSize = updateGroupRequest.getMaxSize();
        if (newMaxSize != 0) {
            if (newMaxSize < groupEntity.getNumberOfNonPendingUsers())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updateGroupRequest.getState() != null) {
            if (updateGroupRequest.getState() == State.CLOSED)
                groupEntity.members.stream().filter(m -> m.isPending()).forEach(groupMemberRepository::delete);
            groupEntity.setStateEntity(updateGroupRequest.getState().getEntity());
        }
        if (groupEntity.getStateEntity().getValue().equals("CLOSED")) {
            if (updateGroupRequest.getStartOfTrip() != null)
                groupEntity.setStartOfTrip(updateGroupRequest.getStartOfTrip());
            if (updateGroupRequest.getEndOfTrip() != null)
                groupEntity.setStartOfTrip(updateGroupRequest.getEndOfTrip());
        }
        if (updateGroupRequest.getOwnerId() != null) {
            if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == updateGroupRequest.getOwnerId())) {
                groupEntity.setOwner(userRepository.findById(updateGroupRequest.getOwnerId()).get());
            } else
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
            groupEntity.setStateEntity(State.CLOSED.getEntity());
        }
    }

    @Transactional
    public void removeUserFromGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        GroupMemberEntity groupMemberEntity = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not found in this group or does not exist"));
        groupMemberRepository.delete(groupMemberEntity);
        if (groupEntity.getNumberOfNonPendingUsers() == 0) {
            groupRepository.delete(groupEntity);
        }
    }
}
