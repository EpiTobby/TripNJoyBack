package fr.tripnjoy.chat.repository;

import fr.tripnjoy.chat.entity.ChannelEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends CrudRepository<ChannelEntity, Long> {

    List<ChannelEntity> findAllByGroup(long groupId);

    Optional<ChannelEntity> findById(long channelId);

    int countByGroup(long groupId);
}
