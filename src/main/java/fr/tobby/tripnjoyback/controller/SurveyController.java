package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.SurveyVoteException;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.VoteSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.service.ChannelService;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import fr.tobby.tripnjoyback.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "surveys")
public class SurveyController {
    private static final Logger logger = LoggerFactory.getLogger(SurveyController.class);
    public static final String ERROR_ON_REQUEST = "Error on request";
    private final SurveyService surveyService;
    private final ChannelService channelService;
    private final IdCheckerService idCheckerService;

    public SurveyController(SurveyService surveyService, ChannelService channelService, IdCheckerService idCheckerService) {
        this.surveyService = surveyService;
        this.channelService = channelService;
        this.idCheckerService = idCheckerService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a survey by id")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public SurveyModel getSurveyById(@PathVariable("id") long channelId) {
        channelService.checkUserHasAccessToChannel(channelId);
        return surveyService.getSurveyById(channelId);
    }

    @GetMapping("channel/{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public List<SurveyModel> getSurveysByChannelId(@PathVariable("id") long channelId) {
        channelService.checkUserHasAccessToChannel(channelId);
        return surveyService.getSurveysByChannelId(channelId);
    }

    @GetMapping("quizz/{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public List<SurveyModel> getQuizz(@PathVariable("id") long channelId) {
        long userId = idCheckerService.getCurrentUserId();
        channelService.checkUserHasAccessToChannel(channelId);
        return surveyService.getQuizz(channelId, userId);
    }

    @PostMapping("{id}")
    @Operation(summary = "Create a survey in a challenge")
    @ApiResponse(responseCode = "200", description = "The survey has been created")
    public SurveyModel createSurvey(@PathVariable("id") long channelId, @RequestBody PostSurveyRequest postSurveyRequest) {
        idCheckerService.checkId(postSurveyRequest.getUserId());
        channelService.checkUserHasAccessToChannel(channelId);
        return surveyService.createSurvey(channelId, postSurveyRequest);
    }

    @PostMapping("vote/{id}")
    @Operation(summary = "Vote for a survey")
    @ApiResponse(responseCode = "200", description = "The vote has been submitted")
    public SurveyModel submitVote(@PathVariable("id") long surveyId, @RequestBody VoteSurveyRequest voteSurveyRequest) {
        idCheckerService.checkId(voteSurveyRequest.getVoterId());
        channelService.checkUserHasAccessToChannel(voteSurveyRequest.getVoterId());
        return surveyService.submitVote(surveyId, voteSurveyRequest);
    }

    @DeleteMapping("vote/{id}")
    @Operation(summary = "Deletes a vote for a survey")
    @ApiResponse(responseCode = "200", description = "The vote has been submitted")
    public void deleteVote(@PathVariable("id") long voteId) {
        surveyService.deleteVote(voteId, idCheckerService.getCurrentUserId());
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public SurveyModel updateSurvey(@PathVariable("id") long surveyId, @RequestBody UpdateSurveyRequest updateSurveyRequest) {
        return surveyService.updateSurvey(surveyId, idCheckerService.getCurrentUserId(), updateSurveyRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public void deleteSurvey(@PathVariable("id") long surveyId) {
        surveyService.deleteSurvey(surveyId,idCheckerService.getCurrentUserId());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(EntityNotFoundException exception)
    {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(SurveyVoteException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String getError(SurveyVoteException exception) {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }
}
