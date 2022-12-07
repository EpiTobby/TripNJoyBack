package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@Getter
@JsonAutoDetect
@NoArgsConstructor
public class UpdatePublicGroupRequest {
    @Nullable
    private String name;
    @Nullable
    private String description;
    @Nullable
    private Date startOfTrip;
    @Nullable
    private Date endOfTrip;
    @Nullable
    private String picture;
    @Nullable
    private String destination;
}
