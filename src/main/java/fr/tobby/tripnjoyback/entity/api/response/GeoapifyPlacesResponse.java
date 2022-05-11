package fr.tobby.tripnjoyback.entity.api.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mapbox.services.commons.geojson.FeatureCollection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyPlacesResponse {
    private FeatureCollection features;
}
