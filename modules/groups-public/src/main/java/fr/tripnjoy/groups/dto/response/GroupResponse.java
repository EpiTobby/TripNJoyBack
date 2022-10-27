package fr.tripnjoy.groups.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tripnjoy.groups.model.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Getter
@JsonAutoDetect
@AllArgsConstructor
public class GroupResponse {

    private final long id;
    private final String name;
    private final String description;
    private final State state;
    @Nullable
    private final Long ownerId;
    private final int maxSize;
    @Nullable
    private final Date startOfTrip;
    @Nullable
    private final Date endOfTrip;
    private final Date createdDate;
    @Nullable
    private final String picture;
    @Nullable
    private final String destination;
    private final List<Long> members;

    @JsonProperty("createdDate")
    public String getCreatedDate() {
        return new SimpleDateFormat("dd-MM-yyyy").format(createdDate);
    }
}
