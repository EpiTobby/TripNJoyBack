package fr.tripnjoy.planning.repository;

import fr.tripnjoy.planning.entity.ActivityEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepository extends CrudRepository<ActivityEntity, Long> {

    List<ActivityEntity> findAllByGroupIdOrderByStartDate(final long groupId);
}
