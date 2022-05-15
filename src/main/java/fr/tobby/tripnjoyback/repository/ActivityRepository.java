package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.ActivityEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActivityRepository extends CrudRepository<ActivityEntity, Long> {

    List<ActivityEntity> findAllByGroupOrderByStartDate(final GroupEntity group);
}
