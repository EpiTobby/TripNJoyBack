package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.BadAvailabilityException;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "{id}/profiles")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("")
    @Operation(summary = "Get all profiles from a user")
    @ApiResponse(responseCode = "200", description = "Returns list of profiles")
    @ApiResponse(responseCode = "422", description = "If the answers are not valid")
    public List<ProfileModel> getUserProfiles(@PathVariable("id") final long userId) {
        return profileService.getUserProfiles(userId);
    }

    @GetMapping("active")
    @PreAuthorize("hasAuthority('admin')")
    public List<ProfileModel> getActiveProfiles() {
        return profileService.getActiveProfiles();
    }

    @PostMapping("")
    @Operation(summary = "Create a profile")
    @ApiResponse(responseCode = "200", description = "Returns the profile")
    @ApiResponse(responseCode = "422", description = "")
    public ProfileModel createProfile(@PathVariable("id") final long userId, @RequestBody ProfileCreationRequest profileCreationRequest) {
        return profileService.createProfile(userId, profileCreationRequest);
    }

    @PatchMapping("{profile}/update")
    @Operation(summary = "Update a profile")
    @ApiResponse(responseCode = "200", description = "The profile is updated")
    @ApiResponse(responseCode = "422", description = "The answers are not valid")
    public void updateProfile(@PathVariable("id") final long userId, @PathVariable("profile") final long profileId, @RequestBody ProfileUpdateRequest profileUpdateRequest) {
        profileService.updateProfile(userId, profileId, profileUpdateRequest);
    }

    @DeleteMapping("{profile}")
    @Operation(summary = "Delete the profile of a user")
    @ApiResponse(responseCode = "200", description = "The profile is deleted")
    @ApiResponse(responseCode = "422", description = "No profile has been found")
    public void deleteProfile(@PathVariable("id") final long userId, @PathVariable("profile") final long profileId) {
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
}
