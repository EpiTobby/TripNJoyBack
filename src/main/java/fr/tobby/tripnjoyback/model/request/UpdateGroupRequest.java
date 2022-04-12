package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UpdateGroupRequest {
    @Nullable
    private String name;
    @Nullable
    private State state;
    @Nullable
    private Long ownerId;
    @Nullable
    private Integer maxSize;
    @Nullable
    private Date startOfTrip;
    @Nullable
    private Date endOfTrip;
}
