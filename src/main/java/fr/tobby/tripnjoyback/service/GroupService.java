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
public class GroupService extends IdCheckerService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;
    private final StateRepository stateRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository, ProfileRepository profileRepository, StateRepository stateRepository) {
        super(userRepository);
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
    }

    public Collection<GroupModel> getUserGroups(long userId) {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUser().getId() == userId && !m.isPending())).map(GroupModel::of).toList();
    }

    public Collection<GroupModel> getUserInvites(long userId) {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUser().getId() == userId && m.isPending())).map(GroupModel::of).toList();
    }

    public String getOwnerEmail(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
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
                                                          ? stateRepository.findByValue("OPEN").get()
                                                          : stateRepository.findByValue("CLOSED").get())
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
        GroupEntity groupEntity = groupRepository.save(GroupEntity.builder()
                .name(createPrivateGroupRequest.getName())
                .maxSize(createPrivateGroupRequest.getMaxSize())
                .createdDate(Date.from(Instant.now()))
                .owner(userEntity)
                .stateEntity(stateRepository.findByValue("CLOSED").get())
                .members(new ArrayList<>())
                .build());
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userEntity, null, false);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members.add(groupMemberEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == userEntity.getId()))
            throw new UserAlreadyInGroupException("User already in group");
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, profileEntity, true));
    }

    @Transactional
    public void inviteUserInPrivateGroup(long groupId, String email) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
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
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        if (updateGroupRequest.getName() != null) {
            groupEntity.setName(updateGroupRequest.getName());
        }
        int newMaxSize = updateGroupRequest.getMaxSize() == null ? 0 : updateGroupRequest.getMaxSize();
        if (newMaxSize != 0) {
            if (newMaxSize < groupEntity.getNumberOfNonPendingUsers())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updateGroupRequest.getState() != null) {
            if (updateGroupRequest.getState() == State.CLOSED)
                groupEntity.members.stream().filter(m -> m.isPending()).forEach(groupMemberRepository::delete);
            groupEntity.setStateEntity(stateRepository.findByValue(updateGroupRequest.getState().toString()).get());
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
        if (updateGroupRequest.getOwnerId() != null) {
            if (groupEntity.members.stream().anyMatch(m -> m.getUser().getId() == updateGroupRequest.getOwnerId())) {
                groupEntity.setOwner(userRepository.findById(updateGroupRequest.getOwnerId()).get());
            } else
                throw new UpdateGroupException("The new owner does not exist or is not in this private group");
        }
    }

    @Transactional
    public void deletePrivateGroup(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        groupRepository.delete(groupEntity);
    }

    @Transactional
    public void joinGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        GroupMemberEntity invitedUser = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not invited or does not exist"));
        invitedUser.setPending(false);
        if (groupEntity.getMaxSize() == groupEntity.getNumberOfNonPendingUsers()) {
            groupEntity.members.stream().filter(m -> m.isPending()).forEach(groupMemberRepository::delete);
            groupEntity.members.removeIf(m -> m.isPending());
            groupEntity.setStateEntity(stateRepository.findByValue("CLOSED").get());
        }
    }

    @Transactional
    public void removeUserFromGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        GroupMemberEntity groupMemberEntity = groupEntity.members.stream().filter(m -> m.getUser().getId() == userId).findFirst().orElseThrow(() -> new UserNotFoundException("User not found in this group or does not exist"));
        groupEntity.members.removeIf(m -> m.getUser().getId() == userId);
        groupMemberRepository.delete(groupMemberEntity);
        if (groupEntity.getNumberOfNonPendingUsers() == 0) {
            groupRepository.delete(groupEntity);
        }
    }
}
