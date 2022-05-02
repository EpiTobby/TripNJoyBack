package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.exception.ChannelNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.MessageNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.request.messaging.PostMessageRequest;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import fr.tobby.tripnjoyback.repository.messaging.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessageService(final ChannelRepository channelRepository,
                          final MessageRepository messageRepository, final UserRepository userRepository)
    {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public MessageEntity postMessage(final long channelId, final PostMessageRequest message)
    {
        ChannelEntity channel = channelRepository.findById(channelId)
                                                 .orElseThrow(() -> new ChannelNotFoundException(channelId));
        UserEntity sender = userRepository.findById(message.getUserId())
                                          .orElseThrow(() -> new UserNotFoundException(message.getUserId()));

        MessageEntity created = messageRepository.save(new MessageEntity(sender, channel, message.getContent(), new Date()));
        logger.debug("Posted message in channel {} by user {}, content: {}", channel.getName(), sender.getEmail(), message.getContent());
        return created;
    }

    public List<MessageEntity> getChannelMessages(final long channelId, final String username, final int page)
    {
        checkUserIsInChannelGroup(channelId, username);

        return messageRepository.findAllByChannelIdOrderBySendDateDesc(channelId, PageRequest.of(page, 50));
    }

    public List<MessageEntity> getChannelPinnedMessages(final long channelId, final String username)
    {
        checkUserIsInChannelGroup(channelId, username);

        return messageRepository.findAllByChannelIdAndPinnedIsTrueOrderBySendDateDesc(channelId);
    }

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
        UserEntity user = userRepository.findByEmail(username).orElseThrow(UserNotFoundException::new);
        ChannelEntity channel = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));

        if (channel.getGroup().getMembers().stream().noneMatch(member -> member.getId().equals(user.getId())))
            throw new ForbiddenOperationException("User " + username + " does not belong to group id " + channel.getGroup().getId());
    }
}
