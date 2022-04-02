package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationModel;
import fr.tobby.tripnjoyback.model.request.anwsers.AvailabilityAnswerModel;
import fr.tobby.tripnjoyback.service.ProfileService;
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

    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping("")
    public List<ProfileModel> getUserProfiles(@PathVariable("id") final long userId){
        return profileService.getUserProfiles(userId);
    }

    @GetMapping("active")
    @PreAuthorize("hasAuthority('admin')")
    public List<ProfileModel> getActiveProfiles(){
        return profileService.getActiveProfiles();
    }

    @PostMapping("")
    public ProfileModel createProfile(@PathVariable("id") final long userId, @RequestBody ProfileCreationModel profileCreationModel){
        return profileService.createProfile(userId, profileCreationModel);
    }

    @PatchMapping("{profile}/availability")
    public void updateProfileAvailability(@PathVariable("id") final long userId, @PathVariable("profile") final long profileId, AvailabilityAnswerModel availability){
        profileService.updateProfileAvailability(userId, profileId, availability);
    }

    @DeleteMapping("{profile}")
    public void deleteProfile(@PathVariable("id") final long userId, @PathVariable("profile") final long profileId){
        profileService.deleteProfile(userId, profileId);
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(ProfileNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
