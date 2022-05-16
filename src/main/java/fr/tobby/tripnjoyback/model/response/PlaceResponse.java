package fr.tobby.tripnjoyback.model.response;

import fr.tobby.tripnjoyback.entity.PlaceEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
    private String name;
    private String street;
    private String city;
    private String country;

    public static PlaceResponse of(PlaceEntity placeEntity){
        return PlaceResponse.builder()
                .name(placeEntity.getName())
                .street(placeEntity.getStreet())
                .city(placeEntity.getCity())
                .country(placeEntity.getCountry())
                .build();
    }
}
