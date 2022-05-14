package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.PlaceEntity;
import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.model.request.PlacesFromCoordinatesRequest;
import fr.tobby.tripnjoyback.model.request.PlacesFromAddressRequest;
import fr.tobby.tripnjoyback.model.response.PlaceResponse;
import fr.tobby.tripnjoyback.repository.PlacesRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PlacesService {
    private final PlacesRepository placesRepository;

    public PlacesService(PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;
    }

    public Collection<PlaceResponse> getPlacesFromAddress(PlacesFromAddressRequest placesFromAddressRequest){
        GeocodeAddressRequest request = GeocodeAddressRequest.builder()
                .address(placesFromAddressRequest.getAddress())
                .city(placesFromAddressRequest.getCity())
                .countryCode(placesFromAddressRequest.getCountryCode()).build();
        List<PlaceEntity> placeEntities = placesRepository.getPlacesFromAddress(request, placesFromAddressRequest.getCategories().stream().map(p -> p.getCategoryValue()).toList(), placesFromAddressRequest.getRadiusMeter());
        return placeEntities.stream().map(PlaceResponse::of).toList();
    }

    public Collection<PlaceResponse> getPlacesFromCoordinates(PlacesFromCoordinatesRequest placesFromCoordinatesRequest) {
        List<PlaceEntity> placeEntities = placesRepository.getPlacesFromCoordinates(placesFromCoordinatesRequest, placesFromCoordinatesRequest.getCategories().stream().map(p -> p.getCategoryValue()).toList());
        return placeEntities.stream().map(PlaceResponse::of).toList();
    }
}
