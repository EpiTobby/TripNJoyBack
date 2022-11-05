package fr.tripnjoy.profiles.controller;

import fr.tripnjoy.profiles.dto.response.MatchMakingResponse;
import fr.tripnjoy.profiles.dto.response.MatchMakingResult;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.service.MatchMaker;
import fr.tripnjoy.profiles.service.ProfileService;
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

    public MatchMakingController(final ProfileService profileService, final MatchMaker matchMaker)
    {
        this.profileService = profileService;
        this.matchMaker = matchMaker;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Create a profile and start the matchmaking")
    @ApiResponse(responseCode = "202", description = "Matchmaking started")
    public MatchMakingResponse match(@RequestParam("user_id") long userId, @RequestBody ProfileCreationRequest profileCreationRequest)
    {
        ProfileModel profile = profileService.createUserProfile(userId, profileCreationRequest);

        long taskId = matchMaker.match(userId, profile);
        return new MatchMakingResponse(taskId, "");
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Start the matchmaking with an existing profile")
    @ApiResponse(responseCode = "202", description = "Matchmaking started")
    @ApiResponse(responseCode = "404", description = "Profile or user not found")
    public MatchMakingResponse match(@RequestParam("user_id") long userId, @RequestParam("profile_id") long profileId)
    {
        long taskId = matchMaker.match(userId, profileId);
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
