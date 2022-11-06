package fr.tripnjoy.places.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoapifyPlacesResponse {
    private List<FeatureResponse> features;
}
