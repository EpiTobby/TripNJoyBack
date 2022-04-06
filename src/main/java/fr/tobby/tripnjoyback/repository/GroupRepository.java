package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GroupRepository extends CrudRepository<GroupEntity, Long> {
    Optional<GroupEntity> findById(long  groupId);
}
