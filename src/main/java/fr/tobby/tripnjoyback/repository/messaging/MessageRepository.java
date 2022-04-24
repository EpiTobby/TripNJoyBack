package fr.tobby.tripnjoyback.repository.messaging;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {

    List<MessageEntity> findAllByChannelIdOrderBySendDateDesc(long channelId, Pageable pageable);

    List<MessageEntity> findAllByChannelIdAndPinnedIsFalseOrderBySendDateDesc(long channelId);
}
