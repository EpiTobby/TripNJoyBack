package fr.tripnjoy.profiles.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class RecommendationResponse {

    private final long id;
    private final long reviewer;
    private final long recommendedUser;
    private final String comment;
}
