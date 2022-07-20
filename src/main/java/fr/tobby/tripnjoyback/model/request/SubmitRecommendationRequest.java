package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
public class SubmitRecommendationRequest {
    @NotNull
    private long reviewedUserId;
    @NotNull
    private String comment;
}
