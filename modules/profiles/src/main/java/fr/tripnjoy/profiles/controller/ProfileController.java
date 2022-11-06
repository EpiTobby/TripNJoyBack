package fr.tripnjoy.profiles.controller;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.profiles.dto.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.exception.BadAvailabilityException;
import fr.tripnjoy.profiles.exception.ProfileNotFoundException;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.request.ProfileUpdateRequest;
import fr.tripnjoy.profiles.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/profiles")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/")
    @Operation(summary = "Get all profiles from a user")
    @ApiResponse(responseCode = "200", description = "Returns list of profiles")
    @ApiResponse(responseCode = "422", description = "If the answers are not valid")
    public List<ProfileModel> getUserProfiles(@RequestHeader("userId") final long userId) {
        return profileService.getUserProfiles(userId);
    }

    @GetMapping("active")
    public List<ProfileModel> getActiveProfiles(@RequestHeader("roles") List<String> roles) {
        if (!roles.contains("admin"))
            throw new UnauthorizedException();
        return profileService.getActiveProfiles();
    }

    @PostMapping("")
    @Operation(summary = "Create a profile")
    @ApiResponse(responseCode = "200", description = "Returns the profile")
    @ApiResponse(responseCode = "422", description = "")
    public ProfileModel createProfile(@RequestHeader("userId") final long userId, @RequestBody ProfileCreationRequest profileCreationRequest) {
        return profileService.createUserProfile(userId, profileCreationRequest);
    }

    @PatchMapping("{profile}/update")
    @Operation(summary = "Update a profile")
    @ApiResponse(responseCode = "200", description = "The profile is updated")
    @ApiResponse(responseCode = "422", description = "The answers are not valid")
    public void updateProfile(@RequestHeader("userId") final long userId, @PathVariable("profile") final long profileId, @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        profileService.updateProfile(userId, profileId, profileUpdateRequest);
    }

    @DeleteMapping("{profile}")
    @Operation(summary = "Delete the profile of a user")
    @ApiResponse(responseCode = "200", description = "The profile is deleted")
    @ApiResponse(responseCode = "422", description = "No profile has been found")
    public void deleteProfile(@RequestHeader("userId") final long userId, @PathVariable("profile") final long profileId) {
        profileService.deleteProfile(userId, profileId);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(ProfileNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(BadAvailabilityException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(BadAvailabilityException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(IllegalArgumentException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
