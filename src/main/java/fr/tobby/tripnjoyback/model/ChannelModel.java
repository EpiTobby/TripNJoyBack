package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class ChannelModel {
    private long id;
    private String name;
    private int index;

    public static ChannelModel of(ChannelEntity channelEntity) {
        return ChannelModel.builder().id(channelEntity.getId()).name(channelEntity.getName()).index(channelEntity.getIndex()).build();
    }
}