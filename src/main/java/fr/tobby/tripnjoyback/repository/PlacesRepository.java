package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.entity.api.response.GeocodeAddressResponse;
import fr.tobby.tripnjoyback.entity.api.response.LocationResponse;
import fr.tobby.tripnjoyback.exception.GeocodeAddressException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class PlacesRepository {
    @Value("${neutrino.user.id}")
    private String neutrinoUserId;

    @Value("${neutrino.api.key}")
    private String neutrinoApiKey;

    @Value("${places.api.key}")
    private String placesApiKey;

    static final String GEOAPIFY_PLACES_URL = "https://api.geoapify.com/v2/places?";
    static final String GEOCODE_ADDRESS_URL = "https://neutrinoapi.net/geocode-address";

    private LocationResponse filterAdresses(GeocodeAddressRequest request, List<LocationResponse> locations){
        LocationResponse result = null;
        for(LocationResponse location : locations){
            //TODO
        }
        return locations.get(1);
    }

    public Optional<LocationResponse> getCoordinates(GeocodeAddressRequest request){
        if (neutrinoApiKey != null && neutrinoUserId != null) {
            request.setUserId(neutrinoUserId);
            request.setApiKey(neutrinoApiKey);
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GeocodeAddressResponse> response = restTemplate.postForEntity(GEOCODE_ADDRESS_URL,request,GeocodeAddressResponse.class);
            if (response.getStatusCode() != HttpStatus.OK){
                throw new GeocodeAddressException("Invalid Status Code");
            }
            GeocodeAddressResponse geocodeAddressResponse = response.getBody();
            if (geocodeAddressResponse.getFound() == 0)
                throw new GeocodeAddressException("Address Not Found");
            if (geocodeAddressResponse.getFound() == 1)
                return Optional.of(geocodeAddressResponse.getLocations().get(0));
            else
                return Optional.of(filterAdresses(request, geocodeAddressResponse.getLocations()));
        } catch (RestClientException e) {
            throw new GeocodeAddressException("An error occurred with Geocode Address");
        }
    }

    private String buildQuery(List<String> categories, double lat, double lon, int radiusMeter){
        String url = GEOAPIFY_PLACES_URL + '?';
        if (!categories.isEmpty()){
            url += "categories=";
            StringJoiner stringJoiner = new StringJoiner(",");
            categories.stream().forEach(c -> stringJoiner.add(c));
            url+=stringJoiner.toString() + '&';
        }
        url += "filter=" + lat + ',' + lon + ',' + radiusMeter + "&limit=20&apiKey=" + placesApiKey;
        return url;
    }

    public List<String> getPlaces(GeocodeAddressRequest request, List<String> categories, int radiusMeter){
        LocationResponse location = getCoordinates(request).get();
        String url = buildQuery(categories, location.getLatitude(), location.getLongitude(), radiusMeter);
        return null;
    }
}
