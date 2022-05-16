package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.tobby.tripnjoyback.model.PlaceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacesFromAddressRequest {
    private List<PlaceCategory> categories;
    private int radiusMeter;
    @NotNull
    private String address;
    private String countryCode;
    private String city;

    @JsonProperty("radiusMeter")
    public void setRadiusMeter(int radiusMeter){
        if (radiusMeter < 500)
            radiusMeter = 500;
        else if (radiusMeter > 5000)
            radiusMeter = 5000;
        this.radiusMeter = radiusMeter;
    }

    @JsonProperty("address")
    public void setAddress(String address){
        this.address = address.trim();
    }

    @JsonProperty("city")
    public void setCity(String city){
        this.city = city.trim();
    }

    @JsonProperty("countryCode")
    public void setCountryCode(String countryCode){
        this.countryCode = countryCode.toUpperCase().trim();
    }
}
