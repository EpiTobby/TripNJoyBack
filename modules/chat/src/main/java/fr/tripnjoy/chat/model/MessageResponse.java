package fr.tripnjoy.chat.model;

import fr.tripnjoy.chat.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private final long id;
    private final long channelId;
    private final long userId;
    private final String content;
    private final Date sentDate;
    private final Date modifiedDate;
    private final boolean pinned;
    private final MessageType type;

    public static MessageResponse of(MessageEntity entity)
    {
        return new MessageResponse(entity.getId(),
                entity.getChannel().getId(),
                entity.getSenderId(),
                entity.getContent(),
                entity.getSendDate(),
                entity.getModifiedDate(),
                entity.isPinned(),
                MessageType.ofEntity(entity.getType()));
    }
}
