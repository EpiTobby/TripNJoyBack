package fr.tobby.tripnjoyback.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.entity.ActivityEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Date;

public record ActivityModel(
        @JsonProperty("id") long id,
        @JsonProperty("name") String name,
        @JsonProperty("description") String description,
        @JsonProperty("startDate") Date startDate,
        @JsonProperty("endDate") Date endDate,
        @JsonProperty("participants") Collection<GroupMemberModel> participants,
        @JsonProperty("color") String color,
        @JsonProperty("location") String location,
        @JsonProperty("icon") String icon
) {

    @NotNull
    public static ActivityModel from(ActivityEntity entity)
    {
        return new ActivityModel(entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getParticipants().stream().map(member -> GroupMemberModel.of(member.getUser())).toList(),
                entity.getColor(),
                entity.getLocation(),
                entity.getIcon());
    }
}
