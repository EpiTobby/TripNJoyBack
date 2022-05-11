package fr.tobby.tripnjoyback.entity.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
public class GeocodeAddressResponse {
    int found;
    private List<LocationResponse> locations;
}
