package fr.tripnjoy.profiles.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonAutoDetect
public record MatchMakingResponse(long taskId, String errorMessage) {

}
