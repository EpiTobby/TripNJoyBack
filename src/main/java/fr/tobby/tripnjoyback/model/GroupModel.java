package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class GroupModel {

    private long id;
    private String name;
    private State state;
    private long ownerId;
    private int maxSize;
    private int destinationId;
    private Date startOfTrip;
    private Date endOfTrip;
    private List<String> users;

    public static GroupModel of(GroupEntity groupEntity){
        return GroupModel.builder()
                .name(groupEntity.getName())
                .state(State.valueOf(groupEntity.getStateEntity().getValue()))
                .ownerId(groupEntity.getOwner().getId())
                .maxSize(groupEntity.getMaxSize())
                .startOfTrip(groupEntity.getStartOfTrip())
                .endOfTrip(groupEntity.getEndOfTrip())
                .build();
    }
}
