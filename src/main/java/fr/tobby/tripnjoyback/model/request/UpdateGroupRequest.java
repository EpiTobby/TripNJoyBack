package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class UpdateGroupRequest {
    private String name;
    private State state;
    private Long ownerId;
    private int maxSize;
    private Date startOfTrip;
    private Date endOfTrip;
}
