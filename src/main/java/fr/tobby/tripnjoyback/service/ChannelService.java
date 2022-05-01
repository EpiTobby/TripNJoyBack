package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.exception.ChannelNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.model.ChannelModel;
import fr.tobby.tripnjoyback.model.request.CreateChannelRequest;
import fr.tobby.tripnjoyback.model.request.UpdateChannelRequest;
import fr.tobby.tripnjoyback.repository.GroupMemberRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
public class ChannelService extends MemberCheckerService {
    private final ChannelRepository channelRepository;
    private final GroupRepository groupRepository;

    public ChannelService(ChannelRepository channelRepository, GroupRepository groupRepository, GroupMemberRepository groupMemberRepository)
    {
        super(groupMemberRepository);
        this.channelRepository = channelRepository;
        this.groupRepository = groupRepository;
    }

    public void checkUserHasAccessToChannel(long channelId)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (channelEntity.getGroup().members.stream().noneMatch(m -> m.getUser().getEmail().equals(email)))
            throw new ForbiddenOperationException("You don't have access to this channel");
    }

    public void checkUserIsOwnerOfGroup(long channelId)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!channelEntity.getGroup().getOwner().getEmail().equals(email))
            throw new ForbiddenOperationException("You don't have access to this channel");
    }

    public Collection<ChannelModel> getGroupChannels(long groupId)
    {
        Collection<ChannelEntity> channelEntities = channelRepository.findAllByGroupId(groupId);
        return channelEntities.stream().sorted(Comparator.comparing(ChannelEntity::getIndex))
                              .map(ChannelModel::of).toList();
    }

    @Transactional
    public ChannelModel createChannel(long groupId, CreateChannelRequest createChannelRequest)
    {
        GroupEntity groupEntity = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        ChannelEntity channelEntity = ChannelEntity.builder()
                                                   .name(createChannelRequest.getName())
                                                   .index(channelRepository.countByGroupId(groupId))
                                                   .group(groupEntity)
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
        int indexOfDeletedChannel = channelEntity.getIndex();
        channelRepository.delete(channelEntity);
        List<ChannelEntity> channelEntities = channelRepository.findAllByGroupId(channelEntity.getGroup().getId()).stream().filter(c -> c.getIndex() > indexOfDeletedChannel).sorted(Comparator.comparing(ChannelEntity::getIndex)).toList();
        channelEntities.stream().forEach(c -> c.setIndex(c.getIndex() - 1));
    }
}
