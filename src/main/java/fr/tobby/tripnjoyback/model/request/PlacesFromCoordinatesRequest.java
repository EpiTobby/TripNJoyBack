package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("radiusMeter")
    public void setRadiusMeter(int radiusMeter){
        if (radiusMeter < 500)
            radiusMeter = 500;
        else if (radiusMeter > 5000)
            radiusMeter = 5000;
        this.radiusMeter = radiusMeter;
    }
}
