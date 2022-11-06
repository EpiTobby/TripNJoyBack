package fr.tripnjoy.planning.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public record ActivityResponse(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("startDate") Date startDate,
        @JsonProperty("endDate") Date endDate,
        @JsonProperty("participants") Collection<Long> participants,
        @JsonProperty("color") String color,
        @JsonProperty("location") String location,
        @JsonProperty("icon") String icon,
        @JsonProperty("infos") List<String> infos
) {

}
