package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.NotificationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface NotificationRepository extends CrudRepository<NotificationEntity, Long> {

    Collection<NotificationEntity> getAllByUserId(long userId);
}
