package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;

@Service
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ProfileRepository profileRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    public GroupModel createPublicGroup(long user1Id, long profile1Id, long user2Id, long profile2Id, int maxSize){
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Instant.now())
                .stateEntity(new StateEntity(maxSize > 2 ? "OPEN" : "CLOSED"))
                .build();
        UserEntity user1Entity = userRepository.findById(user1Id).orElseThrow(() -> new UserNotFoundException("No user with id " + user1Id));
        UserEntity user2Entity = userRepository.findById(user2Id).orElseThrow(() -> new UserNotFoundException("No user with id " + user2Id));
        ProfileEntity profile1Entity = profileRepository.findById(profile1Id).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profile1Id));
        ProfileEntity profile2Entity = profileRepository.findById(profile2Id).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profile2Id));
        groupEntity.members.add(new GroupMemberEntity(groupEntity, user1Entity, profile1Entity));
        groupEntity.members.add(new GroupMemberEntity(groupEntity, user2Entity, profile2Entity));
        groupRepository.save(groupEntity);
        return GroupModel.of(groupEntity);
    }


    public GroupModel createPrivateGroup(long userId, int maxSize){
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Instant.now())
                .owner(userEntity)
                .stateEntity(new StateEntity(maxSize > 2 ? "OPEN" : "CLOSED"))
                .build();
        groupEntity.members.add(new GroupMemberEntity(groupEntity, userEntity, null));
        groupRepository.save(groupEntity);
        return GroupModel.of(groupEntity);
    }

    @Transactional
    public void addUserToPublicGroup(long groupId, long userId, long profileId){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        ProfileEntity profileEntity = profileRepository.findById(profileId).orElseThrow(() -> new ProfileNotFoundException("No profile with id " + profileId));
        groupEntity.members.add(new GroupMemberEntity(groupEntity, userEntity, profileEntity));
        if (groupEntity.getMaxSize() > groupEntity.members.size())
            groupEntity.setStateEntity(new StateEntity("CLOSED"));
    }

    @Transactional
    public void addUserToPrivateGroup(long groupId, long ownerId, long userId){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        if (groupEntity.getOwner().getId() != ownerId)
            throw new ForbiddenOperationException();
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with this id " + userId));
        groupEntity.members.add(new GroupMemberEntity(groupEntity, userEntity, null));
        if (groupEntity.getMaxSize() > groupEntity.members.size())
            groupEntity.setStateEntity(new StateEntity("CLOSED"));
    }

    @Transactional
    public void removeUserOfGroup(long groupId, long userId){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        groupEntity.members.removeIf(m -> m.getUser().getId() == userId);
    }
}
