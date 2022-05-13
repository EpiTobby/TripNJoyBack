package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.api.request.GeocodeAddressRequest;
import fr.tobby.tripnjoyback.entity.api.response.GeoapifyPlacesResponse;
import fr.tobby.tripnjoyback.exception.AddressNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GeoapifyPlacesException;
import fr.tobby.tripnjoyback.exception.GeocodeAddressException;
import fr.tobby.tripnjoyback.model.ChannelModel;
import fr.tobby.tripnjoyback.model.request.PlaceRequest;
import fr.tobby.tripnjoyback.model.response.PlaceResponse;
import fr.tobby.tripnjoyback.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(path = "activities")
public class ActivityController {
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("suggest")
    @Operation(summary = "Retrieve 20 places around an address")
    @ApiResponse(responseCode = "200", description = "Return a list of places")
    @ApiResponse(responseCode = "422", description = "Address not found")
    public Collection<PlaceResponse> getPlacesByAddress(@RequestBody PlaceRequest placeRequest){
        return activityService.getPlacesfromAddress(placeRequest);
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
