package fr.tobby.tripnjoyback.repository.messaging;

import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface ChannelRepository extends CrudRepository<ChannelEntity, Long> {

    Collection<ChannelEntity> findAllByGroupId(long groupId);

    Optional<ChannelEntity> findById(long channelId);

    int countByGroupId(long groupId);
}
