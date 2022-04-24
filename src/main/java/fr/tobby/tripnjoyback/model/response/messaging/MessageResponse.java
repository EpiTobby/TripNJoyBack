package fr.tobby.tripnjoyback.model.response.messaging;

import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
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

    public static MessageResponse of(MessageEntity entity)
    {
        return new MessageResponse(entity.getId(),
                entity.getChannel().getId(),
                entity.getSender().getId(),
                entity.getContent(),
                entity.getSendDate(),
                entity.getModifiedDate(),
                entity.isPinned());
    }
}
