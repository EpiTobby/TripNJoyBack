package fr.tobby.tripnjoyback.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PlaceRequest {
    private List<String> categories;//TODO enum
    @NotNull
    private int radiusMeter;
    @NotNull
    private String address;
    @JsonProperty("country-code")
    private String countryCode;
    private String city;
}
