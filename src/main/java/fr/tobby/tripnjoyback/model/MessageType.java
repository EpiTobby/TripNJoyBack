package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.messaging.MessageTypeEntity;
import fr.tobby.tripnjoyback.repository.messaging.MessageTypeRepository;
import org.jetbrains.annotations.NotNull;

public enum MessageType {
    // /!\ Ids must match the ones in database
    TEXT(1),
    IMAGE(2),
    FILE(3),
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
