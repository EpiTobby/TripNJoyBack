package fr.tripnjoy.groups.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
public class GroupMemoryRequest {
    private String memoryUrl;
}
