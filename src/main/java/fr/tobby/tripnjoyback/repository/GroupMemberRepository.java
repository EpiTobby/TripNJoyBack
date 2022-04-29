package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.GroupMemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupMemberRepository extends CrudRepository<GroupMemberEntity, Long> {
    List<GroupMemberEntity> findByGroupId(long groupId);

    void deleteByGroupId(long groupId);
}
