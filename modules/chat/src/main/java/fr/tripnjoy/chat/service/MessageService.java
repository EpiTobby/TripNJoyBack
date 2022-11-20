package fr.tripnjoy.chat.service;

import fr.tripnjoy.chat.dto.request.PostMessageRequest;
import fr.tripnjoy.chat.entity.ChannelEntity;
import fr.tripnjoy.chat.entity.MessageEntity;
import fr.tripnjoy.chat.exception.ChannelNotFoundException;
import fr.tripnjoy.chat.exception.MessageNotFoundException;
import fr.tripnjoy.chat.model.MessageType;
import fr.tripnjoy.chat.repository.ChannelRepository;
import fr.tripnjoy.chat.repository.MessageRepository;
import fr.tripnjoy.common.broker.RabbitMQConfiguration;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.groups.dto.response.GroupInfoModel;
import fr.tripnjoy.notifications.dto.request.ToTopicNotificationRequest;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import fr.tripnjoy.users.api.response.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserFeignClient userFeignClient;
    private final GroupFeignClient groupFeignClient;
    private final RabbitTemplate rabbitTemplate;

    public MessageService(final ChannelRepository channelRepository,
                          final MessageRepository messageRepository, final UserFeignClient userFeignClient,
                          final GroupFeignClient groupFeignClient, final RabbitTemplate rabbitTemplate)
    {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userFeignClient = userFeignClient;
        this.groupFeignClient = groupFeignClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public MessageEntity postMessage(final long channelId, final PostMessageRequest message)
    {
        ChannelEntity channel = channelRepository.findById(channelId)
                                                 .orElseThrow(() -> new ChannelNotFoundException(channelId));
        if (!userFeignClient.exists(message.getUserId()).value())
            throw new UserNotFoundException(message.getUserId());
        UserResponse sender = userFeignClient.getUserById(List.of("admin"), message.getUserId());
        GroupInfoModel groupInfo = groupFeignClient.getInfo(channel.getGroup());

        MessageEntity created = messageRepository.save(new MessageEntity(message.getUserId(), channel, message.getContent(), message.getType().getEntity(), new Date()));
        logger.debug("Posted message in channel {} by user {}, content: {}", channel.getName(), message.getUserId(), message.getContent());
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.TOPIC_EXCHANGE, "notif", new ToTopicNotificationRequest(
                String.format("%s : %s#%s", sender.getFirstname(), groupInfo.name(), channel.getName()),
                message.getContent(),
                Map.of("channel", String.valueOf(channelId),
                        "sender", String.valueOf(sender.getId()),
                        "content", message.getContent()),
                "chat_" + channel.getGroup(),
                false
        ));
        return created;
    }

    public List<MessageEntity> getChannelMessages(final long channelId, final String username, final int page)
    {
        checkUserIsInChannelGroup(channelId, username);

        return messageRepository.findAllByChannelIdOrderBySendDateDesc(channelId, PageRequest.of(page, 50));
    }

    public List<MessageEntity> getChannelMessages(final long channelId, final String username, final Collection<MessageType> types, final int page)
    {
        checkUserIsInChannelGroup(channelId, username);

        return messageRepository.findAllByChannelIdAndTypeInOrderBySendDateDesc(channelId,
                types.stream().map(MessageType::getEntity).toList(),
                PageRequest.of(page, 50));
    }

    public List<MessageEntity> getChannelPinnedMessages(final long channelId, final String username)
    {
        checkUserIsInChannelGroup(channelId, username);

        return messageRepository.findAllByChannelIdAndPinnedIsTrueOrderBySendDateDesc(channelId);
    }

    @Transactional
    public MessageEntity pinMessage(final long messageId, final boolean pin)
    {
        MessageEntity message = messageRepository.findById(messageId)
                                                 .orElseThrow(() -> new MessageNotFoundException("No message found with id " + messageId));
        message.setPinned(pin);
        return message;
    }

    /**
     * Check that a user is a member of the channel's group. Throw an exception otherwise
     *
     * @throws UserNotFoundException If the user does not exist
     * @throws ChannelNotFoundException If the channel does not exist
     * @throws ForbiddenOperationException If the user is not a member of the group
     */
    private void checkUserIsInChannelGroup(final long channelId, final String username)
            throws UserNotFoundException, ChannelNotFoundException, ForbiddenOperationException
    {
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));

        UserResponse user = userFeignClient.getCurrentUser(username);
        boolean isInGroup = groupFeignClient.getInfo(channel.getGroup()).members().stream()
                                            .anyMatch(member -> member.id() == user.getId());

        if (!isInGroup)
            throw new ForbiddenOperationException("User " + username + " does not belong to group id " + channel.getGroup());
    }
}
