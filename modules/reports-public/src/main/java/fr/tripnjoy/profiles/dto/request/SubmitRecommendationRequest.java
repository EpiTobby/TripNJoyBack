package fr.tripnjoy.profiles.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@JsonAutoDetect
public class SubmitRecommendationRequest {
    private final long reviewedUserId;
    @NotNull
    private final String comment;

    public SubmitRecommendationRequest(@JsonProperty("reviewedUserId") final long reviewedUserId, @NotNull @JsonProperty("comment") final String comment)
    {
        this.reviewedUserId = reviewedUserId;
        this.comment = comment;
    }
}
