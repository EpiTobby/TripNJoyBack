package fr.tobby.tripnjoyback.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
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
    private String description;
    private State state;
    @Nullable
    private UserModel owner;
    private int maxSize;
    @Nullable
    private Date startOfTrip;
    @Nullable
    private Date endOfTrip;
    private Date createdDate;
    @Nullable
    private String picture;
    @Nullable
    private String destination;
    private List<MemberModel> members;
    private List<ChannelModel> channels;

    @JsonProperty("createdDate")
    public String getCreatedDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(createdDate);
    }

    public static GroupModel of(GroupEntity groupEntity){
        return GroupModel.builder()
                         .id(groupEntity.getId())
                         .name(groupEntity.getName())
                         .description(groupEntity.getDescription())
                         .state(State.valueOf(groupEntity.getStateEntity().getValue()))
                         .owner(groupEntity.getOwner() == null ? null : UserModel.of(groupEntity.getOwner()))
                         .maxSize(groupEntity.getMaxSize())
                         .startOfTrip(groupEntity.getStartOfTrip())
                         .endOfTrip(groupEntity.getEndOfTrip())
                         .createdDate(groupEntity.getCreatedDate())
                         .picture(groupEntity.getPicture())
                         .destination(groupEntity.getDestination())
                         .members(groupEntity.members.stream().filter(m -> !m.isPending()).map(m -> MemberModel.of(m.getUser())).toList())
                         .channels(groupEntity.channels.stream().map(ChannelModel::of).toList())
                         .build();
    }
}
