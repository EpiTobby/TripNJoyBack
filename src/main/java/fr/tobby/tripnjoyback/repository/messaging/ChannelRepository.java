package fr.tobby.tripnjoyback.repository.messaging;

import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface ChannelRepository extends CrudRepository<ChannelEntity, Long> {

    Collection<ChannelEntity> findAllByGroupId(long groupId);
}
