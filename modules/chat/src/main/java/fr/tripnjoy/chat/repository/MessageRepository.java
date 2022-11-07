package fr.tripnjoy.chat.repository;

import fr.tripnjoy.chat.entity.MessageEntity;
import fr.tripnjoy.chat.entity.MessageTypeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByChannelIdOrderBySendDateDesc(long channelId, Pageable pageable);

    List<MessageEntity> findAllByChannelIdAndTypeInOrderBySendDateDesc(long channelId, Collection<MessageTypeEntity> types, Pageable pageable);

    List<MessageEntity> findAllByChannelIdAndPinnedIsTrueOrderBySendDateDesc(long channelId);

    Optional<MessageEntity> findByTypeAndContent(MessageTypeEntity messageTypeEntity, String content);
}
