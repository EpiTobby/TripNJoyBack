package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.entity.api.response.LocationResponse;
import fr.tobby.tripnjoyback.repository.PlacesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlacesRepositoryTest {
    private PlacesRepository placesRepository;

    @BeforeEach
    void beforeEach(){
        placesRepository = new PlacesRepository();
    }

    @Test
    void sendRequest(){
        GeocodeAddressRequest request = GeocodeAddressRequest.builder()
                .apiKey("dyEUoy0gpGD7pQOJWON0aQLBM7S2XQoZbaOW7GGzdJ3UfdJb")
                .userId("tripNjoy")
                .address("10 rue Vandrezanne")
                .countryCode("FRA").build();
        LocationResponse location = placesRepository.getCoordinates(request);
        System.out.println(location.getCity() + ", " + location.getAddress());
    }
}
