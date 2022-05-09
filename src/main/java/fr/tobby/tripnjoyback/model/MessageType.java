package fr.tobby.tripnjoyback.model;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.messaging.MessageTypeEntity;
import fr.tobby.tripnjoyback.repository.messaging.MessageTypeRepository;

public enum MessageType {
    TEXT,
    IMAGE,
    FILE,
    ;

    public MessageTypeEntity getEntity() throws IllegalStateException
    {
        return SpringContext.getBean(MessageTypeRepository.class)
                            .findByName(this.toString())
                            .orElseThrow(() -> new IllegalStateException("Message type value " + this + " not present in database"));
    }
}
