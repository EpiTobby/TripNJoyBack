package fr.tobby.tripnjoyback.entity.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "message_type")
public class MessageTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public MessageTypeEntity(final String name)
    {
        this.name = name;
    }
}
