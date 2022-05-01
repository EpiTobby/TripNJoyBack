package fr.tobby.tripnjoyback.entity.messaging;

import fr.tobby.tripnjoyback.entity.UserEntity;
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
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity sender;
    @ManyToOne
    @JoinColumn(name = "channel_id")
    private ChannelEntity channel;
    private String content;
    private Date sendDate;
    private Date modifiedDate;
    @Setter
    private boolean pinned;

    public MessageEntity(final UserEntity user, final ChannelEntity channel, final String content)
    {
        this.sender = user;
        this.channel = channel;
        this.content = content;
        this.sendDate = new Date();
        this.modifiedDate = new Date();
    }

    public MessageEntity(final UserEntity user, final ChannelEntity channel, final String content, final Date date)
    {
        this.sender = user;
        this.channel = channel;
        this.content = content;
        this.sendDate = date;
        this.modifiedDate = new Date();
    }
}
