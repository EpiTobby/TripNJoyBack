package fr.tobby.tripnjoyback.repository;

import fr.tobby.tripnjoyback.entity.PlaceEntity;
import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.entity.api.response.FeatureResponse;
import fr.tobby.tripnjoyback.entity.api.response.GeoapifyPlacesResponse;
import fr.tobby.tripnjoyback.entity.api.response.GeocodeAddressResponse;
import fr.tobby.tripnjoyback.entity.api.response.LocationResponse;
import fr.tobby.tripnjoyback.exception.AddressNotFoundException;
import fr.tobby.tripnjoyback.exception.GeoapifyPlacesException;
import fr.tobby.tripnjoyback.exception.GeocodeAddressException;
import fr.tobby.tripnjoyback.model.request.PlacesFromCoordinatesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

@Component
public class PlacesRepository {
    @Value("${neutrino.user.id}")
    private String neutrinoUserId;

    @Value("${neutrino.api.key}")
    private String neutrinoApiKey;

    @Value("${places.api.key}")
    private String placesApiKey;

    static final String GEOCODE_ADDRESS_URL = "https://neutrinoapi.net/geocode-address";

    private LocationResponse filterAdresses(GeocodeAddressRequest request, List<LocationResponse> locations){
        String requestedCity = request.getCity();
        String countryCode = request.getCountryCode();
        for(LocationResponse location : locations){
            if (countryCode != null && !countryCode.equals(location.getCountryCode3()))
                continue;
            if (requestedCity != null && requestedCity.equals(location.getCity()))
                return location;
        }
        return locations.get(0);
    }

    public LocationResponse getCoordinates(GeocodeAddressRequest request){
        //if DAILY API LIMIT EXCEEDED
//        return Optional.of(LocationResponse.builder().longitude(2.3548f).latitude(48.8279f).build());
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
            if (geocodeAddressResponse == null){
                throw new GeocodeAddressException("Could not retrieve data from address");
            }
            if (geocodeAddressResponse.getFound() == 0)
                throw new AddressNotFoundException("Address Not Found");
            if (geocodeAddressResponse.getFound() == 1)
                return geocodeAddressResponse.getLocations().get(0);
            else
                return filterAdresses(request, geocodeAddressResponse.getLocations());
        } catch (RestClientException e) {
            throw new GeocodeAddressException("An error occurred with Geocode Address", e);
        }
    }

    private String buildQuery(List<String> categories, double lat, double lon, int radiusMeter){
        return new GeoapifyQueryBuilder().setCategories(categories)
                                         .setLimit(10)
                                         .setApiKey(placesApiKey)
                                         .setCircle(new SearchCircle(lon, lat, radiusMeter))
                                         .build();
    }

    public List<PlaceEntity> getPlacesFromAddress(GeocodeAddressRequest request, List<String> categories, int radiusMeter){
        LocationResponse location = getCoordinates(request);
        String url = buildQuery(categories, location.getLatitude(), location.getLongitude(), radiusMeter);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GeoapifyPlacesResponse> response = restTemplate.getForEntity(url, GeoapifyPlacesResponse.class);
            if (response.getStatusCode() != HttpStatus.OK){
                throw new GeoapifyPlacesException("Invalid Status Code");
            }
            GeoapifyPlacesResponse geoapifyPlacesResponse = response.getBody();
            if (geoapifyPlacesResponse == null){
                throw new GeoapifyPlacesException("Could not retrieve places");
            }
            List<FeatureResponse> features = geoapifyPlacesResponse.getFeatures();
            return features.stream().map(FeatureResponse::getPlace).toList();

        } catch (RestClientException e) {
            e.printStackTrace();
            throw new GeoapifyPlacesException("An error occurred with Geocode Address");
        }
    }

    public List<PlaceEntity> getPlacesFromCoordinates(PlacesFromCoordinatesRequest placesFromCoordinatesRequest, List<String> categories){
        String url = buildQuery(categories, placesFromCoordinatesRequest.getLatitude(), placesFromCoordinatesRequest.getLongitude(), placesFromCoordinatesRequest.getRadiusMeter());
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GeoapifyPlacesResponse> response = restTemplate.getForEntity(url, GeoapifyPlacesResponse.class);
            if (response.getStatusCode() != HttpStatus.OK){
                throw new GeoapifyPlacesException("Invalid Status Code");
            }
            GeoapifyPlacesResponse geoapifyPlacesResponse = response.getBody();
            if (geoapifyPlacesResponse == null){
                throw new GeoapifyPlacesException("Could not retrieve places");
            }
            List<FeatureResponse> features = geoapifyPlacesResponse.getFeatures();
            return features.stream().map(FeatureResponse::getPlace).toList();

        } catch (RestClientException e) {
            e.printStackTrace();
            throw new GeoapifyPlacesException("An error occurred with Geocode Address");
        }
    }
}

final class GeoapifyQueryBuilder {

    private Collection<String> categories = new ArrayList<>();
    private SearchCircle circle;
    private int limit = 10;
    private String apiKey;

    static final String GEOAPIFY_PLACES_URL = "https://api.geoapify.com/v2/places";

    public String build()
    {
        if (apiKey == null || circle == null)
            throw new IllegalStateException();
        StringJoiner joinerCategories = new StringJoiner(",");
        categories.forEach(joinerCategories::add);
        return String.format(GEOAPIFY_PLACES_URL + "?categories=%s&filter=%s&bias=proximity:2.25,48.8&limit=%d&apiKey=%s",
                joinerCategories,
                circle.toString(),
                limit,
                apiKey);
    }

    public GeoapifyQueryBuilder setCategories(final Collection<String> categories)
    {
        this.categories = categories;
        return this;
    }

    public GeoapifyQueryBuilder addCategory(String category)
    {
        categories.add(category);
        return this;
    }

    public GeoapifyQueryBuilder setCircle(final SearchCircle circle)
    {
        this.circle = circle;
        return this;
    }

    public GeoapifyQueryBuilder setLimit(final int limit)
    {
        this.limit = limit;
        return this;
    }

    public GeoapifyQueryBuilder setApiKey(final String apiKey)
    {
        this.apiKey = apiKey;
        return this;
    }
}

final record SearchCircle(double longitude, double latitude, int radius)
{
    @Override
    public String toString()
    {
        return "circle:" + longitude + ","+latitude+"," +radius;
    }
}