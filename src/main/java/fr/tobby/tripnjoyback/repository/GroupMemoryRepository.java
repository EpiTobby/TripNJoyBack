package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.GroupMemoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemoryRepository extends CrudRepository<GroupMemoryEntity, Long> {
    List<GroupMemoryEntity> findAll();

    Optional<GroupMemoryEntity> findById(Long id);

    List<GroupMemoryEntity> findByGroupId(long groupId);
}
