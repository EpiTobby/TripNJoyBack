package fr.tobby.tripnjoyback.repository.messaging;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.entity.messaging.MessageTypeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByChannelIdOrderBySendDateDesc(long channelId, Pageable pageable);

    List<MessageEntity> findAllByChannelIdAndTypeInOrderBySendDateDesc(long channelId, Collection<MessageTypeEntity> types, Pageable pageable);

    List<MessageEntity> findAllByChannelIdAndPinnedIsTrueOrderBySendDateDesc(long channelId);
}
