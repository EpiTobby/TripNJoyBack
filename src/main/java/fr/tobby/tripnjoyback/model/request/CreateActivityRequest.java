package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

@Getter
public final class CreateActivityRequest {
    public static final Pattern COLOR_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");

    private final String name;
    private final String description;
    private final Date startDate;
    private final Date endDate;
    private final Collection<Long> participantsIds;
    private final String color;
    private final String location;
    private final String icon;

    @JsonCreator
    public CreateActivityRequest(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("startDate") Date startDate,
            @JsonProperty("endDate") Date endDate,
            @JsonProperty("participantsIds") Collection<Long> participantsIds,
            @JsonProperty("color") String color,
            @JsonProperty("location") final String location,
            @JsonProperty("icon") final String icon)
    {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participantsIds = participantsIds;
        if (!COLOR_PATTERN.matcher(color).matches())
            throw new IllegalArgumentException("Invalid color " + color);
        this.color = color;
        this.location = location;
        this.icon = icon;
    }
}