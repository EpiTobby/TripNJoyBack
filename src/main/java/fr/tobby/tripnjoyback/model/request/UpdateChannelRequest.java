package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Builder
public class UpdateChannelRequest {
    String name;
    Integer index;
}
