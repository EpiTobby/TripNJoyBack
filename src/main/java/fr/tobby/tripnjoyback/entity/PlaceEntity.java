package fr.tobby.tripnjoyback.entity;

import com.mapbox.services.commons.geojson.Feature;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlaceEntity {
    private String name;
    private String street;
    private String city;
    private String country;

    public static PlaceEntity of(Feature feature){
        String name = feature.getStringProperty("name");
        String street = feature.getStringProperty("street");
        String city = feature.getStringProperty("city");
        String country = feature.getStringProperty("country");
        return PlaceEntity.builder().name(name).street(street).city(city).country(country).build();
    }
}
