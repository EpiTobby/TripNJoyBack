package fr.tripnjoy.groups.dto.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonAutoDetect
public class CreatePublicGroupRequest {
    private final long user1;
    private final long user2;
    private final long profile1;
    private final long profile2;
    private final int maxSize;
}
