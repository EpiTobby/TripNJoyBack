package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.utils.ColorUtils;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collection;
import java.util.Date;

@Getter
public final class UpdateActivityRequest {
    @Nullable
    private final String name;
    @Nullable
    private final String description;
    @Nullable
    private final Date startDate;
    @Nullable
    private final Date endDate;
    @Nullable
    private final String color;
    @Nullable
    private final String location;
    @Nullable
    private final String icon;
    @Nullable
    private final Collection<String> infos;
    @Nullable
    private final Collection<Long> participants;

    public UpdateActivityRequest(@Nullable @JsonProperty("name") final String name,
                                 @Nullable @JsonProperty("description") final String description,
                                 @Nullable @JsonProperty("startDate") final Date startDate,
                                 @Nullable @JsonProperty("endDate") final Date endDate,
                                 @Nullable @JsonProperty("color") final String color,
                                 @Nullable @JsonProperty("location") final String location,
                                 @Nullable @JsonProperty("icon") final String icon,
                                 @Nullable @JsonProperty("infos") final Collection<String> infos,
                                 @Nullable @JsonProperty("participants") final Collection<Long> participants)
    {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        if (color != null && !CreateActivityRequest.COLOR_PATTERN.matcher(color).matches())
            throw new IllegalArgumentException("Invalid color " + color);
        this.color = color;
        this.location = location;
        this.icon = icon;
        this.infos = infos;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder {
        @Nullable
        private String name = null;
        @Nullable
        private String description = null;
        @Nullable
        private Date startDate = null;
        @Nullable
        private Date endDate = null;
        @Nullable
        private Color color = null;
        @Nullable
        private String location = null;
        @Nullable
        private String icon = null;
        @Nullable
        private Collection<String> infos = null;
        @Nullable
        private Collection<Long> participants = null;

        Builder() {}

        public UpdateActivityRequest build()
        {
            return new UpdateActivityRequest(
                    name,
                    description,
                    startDate,
                    endDate,
                    color == null ? null : ColorUtils.colorToString(color),
                    location,
                    icon,
                    infos,
                    participants
            );
        }

        public Builder setName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder setDescription(final String description)
        {
            this.description = description;
            return this;
        }

        public Builder setStartDate(final Date startDate)
        {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(final Date endDate)
        {
            this.endDate = endDate;
            return this;
        }

        public Builder setColor(final Color color)
        {
            this.color = color;
            return this;
        }

        public Builder setLocation(final String location)
        {
            this.location = location;
            return this;
        }

        public Builder setIcon(final String icon)
        {
            this.icon = icon;
            return this;
        }

        public Builder setInfos(final Collection<String> infos)
        {
            this.infos = infos;
            return this;
        }

        public Builder setParticipants(final Collection<Long> participants)
        {
            this.participants = participants;
            return this;
        }
    }
}
