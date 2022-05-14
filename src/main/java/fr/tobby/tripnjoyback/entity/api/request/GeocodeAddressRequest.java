package fr.tobby.tripnjoyback.entity.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
@Builder
public class GeocodeAddressRequest {
    @JsonProperty("api-key")
    private String apiKey;
    @JsonProperty("user-id")
    private String userId;
    private String address;
    @JsonProperty("country-code")
    private String countryCode;
    private String city;
    @JsonProperty("language-code")
    private String languageCode;
}
