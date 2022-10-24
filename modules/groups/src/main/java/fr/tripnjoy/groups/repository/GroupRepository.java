package fr.tripnjoy.groups.repository;

import fr.tripnjoy.groups.entity.GroupEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends CrudRepository<GroupEntity, Long> {
    List<GroupEntity> findAll();

    Optional<GroupEntity> findById(long groupId);

    @Query("SELECT g FROM GroupEntity g "
            + "JOIN StateEntity state ON g.stateEntity = state"
            + " WHERE state.value = 'OPEN' AND g.owner is null")
    Collection<GroupEntity> findAvailableGroups();
}
