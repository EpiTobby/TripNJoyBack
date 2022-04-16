package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.service.MatchMaker;
import fr.tobby.tripnjoyback.service.ProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "matchmaking")
public class MatchMakingController {

    private final ProfileService profileService;
    private final MatchMaker matchMaker;
    private final UserRepository userRepository;

    public MatchMakingController(final ProfileService profileService, final MatchMaker matchMaker,
                                 final UserRepository userRepository)
    {
        this.profileService = profileService;
        this.matchMaker = matchMaker;
        this.userRepository = userRepository;
    }

    @PostMapping
    public String match(@RequestParam("user_id") long userId, @RequestBody ProfileCreationRequest profileCreationRequest)
    {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotConfirmedException::new);
        ProfileModel profile = profileService.createProfile(userId, profileCreationRequest);

        matchMaker.match(userEntity, profile);
        return "ok";
    }
}