package fr.tripnjoy.places.api.client;

import fr.tripnjoy.places.dto.request.PlacesFromAddressRequest;
import fr.tripnjoy.places.dto.request.PlacesFromCoordinatesRequest;
import fr.tripnjoy.places.dto.response.PlaceResponse;
import fr.tripnjoy.places.model.PlaceCategory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

@FeignClient(value = "SERVICE-PLACES")
public interface PlacesFeignClient {

    @GetMapping("categories")
    Collection<PlaceCategory> getCategories();

    @PostMapping("address")
    Collection<PlaceResponse> getPlacesFromAddress(@RequestBody PlacesFromAddressRequest placesFromAddressRequest);

    @PostMapping("coordinates")
    Collection<PlaceResponse> getPlacesFromCoordinates(@RequestBody PlacesFromCoordinatesRequest placesFromCoordinatesRequest);
}
