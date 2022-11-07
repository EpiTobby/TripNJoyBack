package fr.tripnjoy.chat.service;

import fr.tripnjoy.chat.dto.request.CreateChannelRequest;
import fr.tripnjoy.chat.dto.request.UpdateChannelRequest;
import fr.tripnjoy.chat.entity.ChannelEntity;
import fr.tripnjoy.chat.exception.ChannelNotFoundException;
import fr.tripnjoy.chat.exception.DeleteChannelException;
import fr.tripnjoy.chat.model.ChannelModel;
import fr.tripnjoy.chat.repository.ChannelRepository;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.groups.dto.response.GroupInfoModel;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final GroupFeignClient groupFeignClient;

    public ChannelService(ChannelRepository channelRepository, final GroupFeignClient groupFeignClient)
    {
        this.channelRepository = channelRepository;
        this.groupFeignClient = groupFeignClient;
    }

    public void checkMember(long userId, long groupId) throws ForbiddenOperationException
    {
        if (!groupFeignClient.isUserInGroup(groupId, userId).value())
            throw new ForbiddenOperationException();
    }

    public void checkUserHasAccessToChannel(long channelId, long userId)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        checkMember(userId, channelEntity.getGroup());
    }

    public void checkUserIsOwnerOfGroup(long channelId)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));

        GroupInfoModel group = groupFeignClient.getInfo(channelEntity.getGroup());
        // FIXME: check ownership
//        if (group. != null && !owner.getEmail().equals(email))
//            throw new ForbiddenOperationException("You don't have access to this channel");
    }

    public Collection<ChannelModel> getGroupChannels(long groupId)
    {
        Collection<ChannelEntity> channelEntities = channelRepository.findAllByGroup(groupId);
        return channelEntities.stream().sorted(Comparator.comparing(ChannelEntity::getIndex))
                              .map(ChannelModel::of).toList();
    }

    @Transactional
    public ChannelModel createDefaultChannel(long groupId)
    {
        ChannelEntity channelEntity = ChannelEntity.builder()
                .name("general")
                .index(0)
                .group(groupId)
                .build();

        channelRepository.save(channelEntity);
        return ChannelModel.of(channelEntity);
    }

    @Transactional
    public ChannelModel createChannel(long groupId, CreateChannelRequest createChannelRequest)
    {
        ChannelEntity channelEntity = ChannelEntity.builder()
                                                   .name(createChannelRequest.getName())
                                                   .index(channelRepository.countByGroup(groupId))
                                                   .group(groupId)
                                                   .build();
        channelRepository.save(channelEntity);
        return ChannelModel.of(channelEntity);
    }

    @Transactional
    public void updateChannel(long channelId, UpdateChannelRequest updateChannelRequest)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        if (updateChannelRequest.getName() != null)
        {
            channelEntity.setName(updateChannelRequest.getName());
        }
        if (updateChannelRequest.getIndex() != null)
        {
            channelEntity.setIndex(updateChannelRequest.getIndex());
        }
    }

    @Transactional
    public void deleteChannel(long channelId)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        if (channelRepository.countByGroup(channelEntity.getGroup()) == 1)
            throw new DeleteChannelException("Cannot delete the last channel of a group");
        int indexOfDeletedChannel = channelEntity.getIndex();
        channelRepository.delete(channelEntity);
        List<ChannelEntity> channelEntities = channelRepository.findAllByGroup(channelEntity.getGroup())
                                                               .stream()
                                                               .filter(c -> c.getIndex() > indexOfDeletedChannel)
                                                               .sorted(Comparator.comparing(ChannelEntity::getIndex)).toList();
        channelEntities.forEach(c -> c.setIndex(c.getIndex() - 1));
    }
}
