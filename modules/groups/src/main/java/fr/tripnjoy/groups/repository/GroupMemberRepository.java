package fr.tripnjoy.groups.repository;

import fr.tripnjoy.groups.entity.GroupMemberEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupMemberRepository extends CrudRepository<GroupMemberEntity, Long> {
    List<GroupMemberEntity> findByGroupId(long groupId);

    void deleteByGroupId(long groupId);
}
