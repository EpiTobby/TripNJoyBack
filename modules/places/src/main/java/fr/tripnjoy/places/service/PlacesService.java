package fr.tripnjoy.places.service;

import fr.tripnjoy.places.dto.request.GeocodeAddressRequest;
import fr.tripnjoy.places.dto.request.PlacesFromAddressRequest;
import fr.tripnjoy.places.dto.request.PlacesFromCoordinatesRequest;
import fr.tripnjoy.places.dto.response.PlaceResponse;
import fr.tripnjoy.places.model.PlaceCategory;
import fr.tripnjoy.places.repository.PlacesRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PlacesService {
    private final PlacesRepository placesRepository;

    public PlacesService(PlacesRepository placesRepository)
    {
        this.placesRepository = placesRepository;
    }

    public Collection<PlaceResponse> getPlacesFromAddress(PlacesFromAddressRequest placesFromAddressRequest)
    {
        GeocodeAddressRequest request = GeocodeAddressRequest.builder()
                                                             .address(placesFromAddressRequest.getAddress())
                                                             .city(placesFromAddressRequest.getCity())
                                                             .countryCode(placesFromAddressRequest.getCountryCode()).build();
        return placesRepository.getPlacesFromAddress(request, placesFromAddressRequest.getCategories().stream().map(PlaceCategory::getCategoryValue).toList(), placesFromAddressRequest.getRadiusMeter());
    }

    public Collection<PlaceResponse> getPlacesFromCoordinates(PlacesFromCoordinatesRequest placesFromCoordinatesRequest)
    {
        return placesRepository.getPlacesFromCoordinates(placesFromCoordinatesRequest, placesFromCoordinatesRequest.getCategories().stream().map(PlaceCategory::getCategoryValue).toList());
    }
}
