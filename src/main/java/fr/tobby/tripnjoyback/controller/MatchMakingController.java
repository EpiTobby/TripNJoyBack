package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.model.MatchMakingResult;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.response.MatchMakingResponse;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.service.MatchMaker;
import fr.tobby.tripnjoyback.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

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
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create a profile and start the matchmaking")
    @ApiResponse(responseCode = "202", description = "Matchmaking started")
    public MatchMakingResponse match(@RequestParam("user_id") long userId, @RequestBody ProfileCreationRequest profileCreationRequest)
    {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(UserNotConfirmedException::new);
        ProfileModel profile = profileService.createUserProfile(userId, profileCreationRequest);

        long taskId = matchMaker.match(userEntity, profile);
        return new MatchMakingResponse(taskId, "");
    }

    @GetMapping("{taskId}")
    @Operation(summary = "Get the state of a match making task")
    @ApiResponse(responseCode = "200", description = "State")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public MatchMakingResult getResult(@PathVariable("taskId") long taskId) throws ExecutionException, InterruptedException
    {
        return matchMaker.getTask(taskId);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    String onIllegalStateException(IllegalStateException e)
    {
        return e.getMessage();
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String onNoSuchElementException(NoSuchElementException e)
    {
        return e.getMessage();
    }
}
