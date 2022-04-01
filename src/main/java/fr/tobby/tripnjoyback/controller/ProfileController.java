package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationModel;
import fr.tobby.tripnjoyback.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/profiles")
public class ProfileController {
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private ProfileService profileService;

    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping("{id}")
    public void GetProfiles(@PathVariable("id") final long userId){
        profileService.findByUserId(userId);
    }

    @PostMapping("{id}")
    public ProfileModel CreateProfile(@PathVariable("id") final long userId, @RequestBody ProfileCreationModel profileCreationModel){
        return profileService.createProfile(userId, profileCreationModel);
    }

    @DeleteMapping("{id}")
    public void DeleteProfile(@PathVariable("id") final long profileId){
        profileService.deleteById(profileId);
    }

}
