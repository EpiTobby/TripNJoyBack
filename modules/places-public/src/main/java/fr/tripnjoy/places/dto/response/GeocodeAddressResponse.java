package fr.tripnjoy.places.dto.response;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@JsonAutoDetect
@Getter
public class GeocodeAddressResponse {
    private final int found;
    private final List<LocationResponse> locations;

    public GeocodeAddressResponse(@JsonProperty("found") final int found, @JsonProperty("locations") final List<LocationResponse> locations)
    {
        this.found = found;
        this.locations = locations;
    }
}
