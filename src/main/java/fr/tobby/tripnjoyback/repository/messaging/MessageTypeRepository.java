package fr.tobby.tripnjoyback.repository.messaging;

import fr.tobby.tripnjoyback.entity.messaging.MessageTypeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MessageTypeRepository extends CrudRepository<MessageTypeEntity, Long> {

    Optional<MessageTypeEntity> findByName(String name);
}
