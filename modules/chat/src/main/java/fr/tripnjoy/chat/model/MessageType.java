package fr.tripnjoy.chat.model;

import fr.tripnjoy.chat.SpringContext;
import fr.tripnjoy.chat.entity.MessageTypeEntity;
import fr.tripnjoy.chat.repository.MessageTypeRepository;
import org.jetbrains.annotations.NotNull;

public enum MessageType {
    // /!\ Ids must match the ones in database
    TEXT(1),
    IMAGE(2),
    FILE(3),
    SURVEY(4),
    ;

    public final int id;

    MessageType(final int id)
    {
        this.id = id;
    }

    public MessageTypeEntity getEntity() throws IllegalStateException
    {
        return SpringContext.getBean(MessageTypeRepository.class)
                            .findByName(this.toString())
                            .orElseThrow(() -> new IllegalStateException("Message type value " + this + " not present in database"));
    }

    @NotNull
    public static MessageType ofEntity(MessageTypeEntity entity) throws IllegalArgumentException
    {
        try
        {
            return values()[(int) (entity.getId() - 1)];
        }
        catch (IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("No message type constant for id " + entity.getId());
        }
    }
}
