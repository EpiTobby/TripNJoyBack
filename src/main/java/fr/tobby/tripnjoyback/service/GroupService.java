package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.StateEntity;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    private GroupModel createPublicGroup(long user1Id, long profile1Id, long user2Id, long profile2Id, int maxSize){
        GroupEntity groupEntity = GroupEntity.builder()
                .maxSize(maxSize)
                .createdDate(Instant.now())
                .stateEntity(new StateEntity(maxSize > 2 ? "OPEN" : "CLOSE"))
                .build();
        groupRepository.save(groupEntity);
        return GroupModel.of(groupEntity);
    }

    private void addUserToGroup(long groupId, long userId, long profileId){

    }
}
