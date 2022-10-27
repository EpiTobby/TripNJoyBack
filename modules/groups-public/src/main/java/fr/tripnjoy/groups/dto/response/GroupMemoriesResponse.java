package fr.tripnjoy.groups.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@JsonSerialize
@JsonAutoDetect
@NoArgsConstructor
public class GroupMemoriesResponse {
    private List<String> memories;
}
