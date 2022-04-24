package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.exception.ChannelNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.request.messaging.PostMessageRequest;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import fr.tobby.tripnjoyback.repository.messaging.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

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
}
