package fr.tripnjoy.places.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@JsonAutoDetect
@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {
    private String country;
    private String address;
    private String city;
    private String countryCode;
    private String countryCode3;
    private String postalCode;
    private float latitude;
    private float longitude;
    private String state;
}
