package fr.tripnjoy.chat.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@Getter
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private long senderId;
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelEntity channel;
    private String content;
    private Date sendDate;
    private Date modifiedDate;
    @Setter
    private boolean pinned;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private MessageTypeEntity type;

    public MessageEntity(final long user, final ChannelEntity channel, final String content, MessageTypeEntity type, final Date date)
    {
        this.senderId = user;
        this.channel = channel;
        this.content = content;
        this.type = type;
        this.sendDate = date;
        this.modifiedDate = new Date();
    }
}
