package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.awt.*;
import java.util.Collection;
import java.util.Date;

@Getter
public final class CreateActivityRequest {
    private final String name;
    private final String description;
    private final Date startDate;
    private final Date endDate;
    private final Collection<Long> participantsIds;
    private final Color color;
    private final String location;
    private final String icon;

    @JsonCreator
    public CreateActivityRequest(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("startDate") Date startDate,
            @JsonProperty("endDate") Date endDate,
            @JsonProperty("participants") Collection<Long> participantsIds,
            @JsonProperty("color") String color,
            @JsonProperty("location") final String location,
            @JsonProperty("icon") final String icon)
    {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participantsIds = participantsIds;
        this.color = Color.decode(color);
        this.location = location;
        this.icon = icon;
    }
}