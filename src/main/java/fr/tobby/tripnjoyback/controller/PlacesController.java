package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.AddressNotFoundException;
import fr.tobby.tripnjoyback.exception.GeoapifyPlacesException;
import fr.tobby.tripnjoyback.exception.GeocodeAddressException;
import fr.tobby.tripnjoyback.model.request.PlacesFromCoordinatesRequest;
import fr.tobby.tripnjoyback.model.request.PlacesFromAddressRequest;
import fr.tobby.tripnjoyback.model.response.PlaceResponse;
import fr.tobby.tripnjoyback.service.PlacesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "places")
public class PlacesController {
    private static final Logger logger = LoggerFactory.getLogger(PlacesController.class);
    private final PlacesService placesService;

    public PlacesController(PlacesService placesService) {
        this.placesService = placesService;
    }

    @PostMapping("address")
    @Operation(summary = "Retrieve 10 places around an address")
    @ApiResponse(responseCode = "200", description = "Return a list of places")
    @ApiResponse(responseCode = "422", description = "Address not found")
    public Collection<PlaceResponse> getPlacesFromAddress(@RequestBody PlacesFromAddressRequest placesFromAddressRequest){
        return placesService.getPlacesFromAddress(placesFromAddressRequest);
    }

    @PostMapping("coordinates")
    @Operation(summary = "Retrieve 10 places around geographic coordinates")
    @ApiResponse(responseCode = "200", description = "Return a list of places")
    @ApiResponse(responseCode = "422", description = "Address not found")
    public Collection<PlaceResponse> getPlacesFromCoordinates(@RequestBody PlacesFromCoordinatesRequest placesFromCoordinatesRequest){
        return placesService.getPlacesFromCoordinates(placesFromCoordinatesRequest);
    }


    @ExceptionHandler(AddressNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(AddressNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(GeocodeAddressException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String getError(GeocodeAddressException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(GeoapifyPlacesException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String getError(GeoapifyPlacesException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
