package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.tobby.tripnjoyback.model.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacesFromCoordinatesRequest {
    private double longitude;
    private double latitude;
    private int radiusMeter;
    private List<PlaceCategory> categories;
}
