package fr.tripnjoy.notifications.repository;

import fr.tripnjoy.notifications.entity.NotificationEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface NotificationRepository extends CrudRepository<NotificationEntity, Long> {

    Collection<NotificationEntity> getAllByUserId(long userId);
}
