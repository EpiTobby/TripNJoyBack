package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonAutoDetect
@NoArgsConstructor
public class CreatePrivateGroupRequest {
    private String name;
    private int maxSize;
}
