package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.PlaceEntity;
import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.model.request.PlaceRequest;
import fr.tobby.tripnjoyback.model.response.PlaceResponse;
import fr.tobby.tripnjoyback.repository.PlacesRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ActivityService {
    private final PlacesRepository placesRepository;

    public ActivityService(PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;
    }

    public Collection<PlaceResponse> getPlacesfromAddress(PlaceRequest placeRequest){
        GeocodeAddressRequest request = GeocodeAddressRequest.builder()
                .address(placeRequest.getAddress())
                .city(placeRequest.getCity())
                .countryCode(placeRequest.getCountryCode()).build();
        List<PlaceEntity> placeEntities = placesRepository.getPlacesfromAddress(request, placeRequest.getCategories(),placeRequest.getRadiusMeter());
        return placeEntities.stream().map(PlaceResponse::of).toList();
    }
}
