package fr.tripnjoy.chat.repository;

import fr.tripnjoy.chat.entity.MessageTypeEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MessageTypeRepository extends CrudRepository<MessageTypeEntity, Long> {

    Optional<MessageTypeEntity> findByName(String name);
}
