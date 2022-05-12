package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Date;

public record CreateActivityRequest(
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("startDate") Date startDate,
        @JsonProperty("endDate") Date endDate,
        @JsonProperty("participants") Collection<Long> participantsIds
) {

}
