package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.request.UpdateGroupRequest;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.StateRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final StateRepository stateRepository;

    public GroupService(GroupRepository groupRepository, UserRepository userRepository, ProfileRepository profileRepository, StateRepository stateRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
    }

    public List<GroupModel> getUserGroups(long userId){
        Iterable<GroupEntity> entities =  groupRepository.findAll();
        List<GroupModel> models = new ArrayList();
        entities.forEach(e -> {
            if (e.members.stream().anyMatch(m -> m.getUser().getId() == userId))
                models.add(GroupModel.of(e));
        });
        return models;
    }

    @Transactional
    public GroupModel createPublicGroup(long user1Id, long profile1Id, long user2Id, long profile2Id, int maxSize){
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Instant.now())
                .stateEntity(maxSize > 2 ? stateRepository.findByValue("OPEN").get() : stateRepository.findByValue("CLOSED").get())
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


    @Transactional
    public GroupModel createPrivateGroup(long userId, int maxSize){
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Instant.now())
                .owner(userEntity)
                .stateEntity(stateRepository.findByValue("CLOSED").get())
                .build();
        groupEntity.members = List.of(new GroupMemberEntity(groupEntity, userEntity, null));
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
            groupEntity.setStateEntity(stateRepository.findByValue("CLOSED").get());
    }

    @Transactional
    public void addUserToPrivateGroup(long groupId, String email, Authentication authentication){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        UserEntity owner = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UserNotFoundException("No user with this email " + email));
        if (groupEntity.getOwner().getId() != owner.getId())
            throw new ForbiddenOperationException();
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("No user with this email " + email));
        if (!userEntity.isConfirmed())
            throw new UserNotConfirmedException("The user you want to invite is not confirmed");
        groupEntity.members.add(new GroupMemberEntity(groupEntity, userEntity, null));
        if (groupEntity.getMaxSize() > groupEntity.members.size())
            groupEntity.setStateEntity(stateRepository.findByValue("CLOSED").get());
    }

    @Transactional
    public void UpdatePrivateGroup(long groupId, UpdateGroupRequest updateGroupRequest){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        if (updateGroupRequest.getMaxSize() != 0)
            groupEntity.setMaxSize(updateGroupRequest.getMaxSize());
        if (updateGroupRequest.getState() != null)
            groupEntity.setStateEntity(stateRepository.findByValue(updateGroupRequest.getState().toString()).get());
        if (groupEntity.getStateEntity().getValue().equals("CLOSED")){
            //TODO set date and destination
        }
    }

    @Transactional
    public void removeUserOfGroup(long groupId, String email){
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException("No group found with id " + groupId));
        groupEntity.members.removeIf(m -> m.getUser().getEmail().equals(email));
    }
}
