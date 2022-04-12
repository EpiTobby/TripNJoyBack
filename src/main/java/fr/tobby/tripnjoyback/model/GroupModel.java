package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.Instant;
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
    private String owner;
    private int maxSize;
    private Date startOfTrip;
    private Date endOfTrip;
    private Date createdDate;
    private List<String> users;

    @JsonProperty("createdDate")
    public String getCreatedDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(createdDate);
    }

    public static GroupModel of(GroupEntity groupEntity){
        return GroupModel.builder()
                .id(groupEntity.getId())
                .name(groupEntity.getName())
                .state(State.valueOf(groupEntity.getStateEntity().getValue()))
                .owner(groupEntity.getOwner().getFirstname() + " " + groupEntity.getOwner().getLastname())
                .maxSize(groupEntity.getMaxSize())
                .startOfTrip(groupEntity.getStartOfTrip())
                .endOfTrip(groupEntity.getEndOfTrip())
                .createdDate(groupEntity.getCreatedDate())
                .users(groupEntity.members.stream().map(m -> m.getUser().getFirstname() + " " + m.getUser().getLastname()).toList())
                .build();
    }
}
