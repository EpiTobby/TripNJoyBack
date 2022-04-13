package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.GroupModel;
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
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ProfileRepository profileRepository;
    private final StateRepository stateRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, GroupMemberRepository groupMemberRepository, ProfileRepository profileRepository, StateRepository stateRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
    }

    public Collection<GroupModel> getUserGroups(long userId) {
        List<GroupEntity> groups = groupRepository.findAll();
        return groups.stream().filter(g -> g.members.stream().anyMatch(m -> m.getUser().getId() == userId)).map(GroupModel::of).toList();
    }

    public String getOwnerEmail(long groupId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        return groupEntity.getOwner().getEmail();
    }

    @Transactional
    public GroupModel createPublicGroup(UserEntity user1Entity, ProfileEntity profile1Entity, UserEntity user2Entity, ProfileEntity profile2Entity, int maxSize) {
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Date.from(Instant.now()))
                .stateEntity(maxSize > 2 ? stateRepository.findByValue("OPEN").get() : stateRepository.findByValue("CLOSED").get())
                .members(List.of())
                .build();
        groupRepository.save(groupEntity);
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, user1Entity, profile1Entity)));
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, user2Entity, profile2Entity)));
        return GroupModel.of(groupEntity);
    }


    @Transactional
    public GroupModel createPrivateGroup(long userId, int maxSize) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Date.from(Instant.now()))
                .owner(userEntity)
                .stateEntity(stateRepository.findByValue("CLOSED").get())
                .build();
        groupRepository.save(groupEntity);
        GroupMemberEntity groupMemberEntity = new GroupMemberEntity(groupEntity, userEntity, null);
        groupMemberRepository.save(groupMemberEntity);
        groupEntity.members = List.of(groupMemberEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, profileEntity)));
        if (groupEntity.getMaxSize() == groupEntity.members.size())
            groupEntity.setStateEntity(stateRepository.findByValue("CLOSED").get());
    }

    @Transactional
    public void addUserToPrivateGroup(long groupId, String email) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user with this email " + email));
        if (!userEntity.isConfirmed())
            throw new UserNotConfirmedException("The user you want to invite is not confirmed");
        groupEntity.members.add(groupMemberRepository.save(new GroupMemberEntity(groupEntity, userEntity, null)));
        if (groupEntity.getMaxSize() == groupEntity.members.size())
            groupEntity.setStateEntity(stateRepository.findByValue("CLOSED").get());
    }

    @Transactional
    public void UpdatePrivateGroup(long groupId, UpdateGroupRequest updateGroupRequest) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        if (updateGroupRequest.getName() != null) {
            groupEntity.setName(updateGroupRequest.getName());
        }
        int newMaxSize = updateGroupRequest.getMaxSize();
        if (newMaxSize != 0) {
            if (newMaxSize < groupEntity.members.size())
                throw new UpdateGroupException("The maximum size of the group must be greater or equal than the current number of members in the private group");
            groupEntity.setMaxSize(newMaxSize);
        }
        if (updateGroupRequest.getState() != null)
            groupEntity.setStateEntity(stateRepository.findByValue(updateGroupRequest.getState().toString()).get());
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
    public void removeUserOfGroup(long groupId, long userId) {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        groupEntity.members.removeIf(m -> m.getUser().getId() == userId);
    }
}
