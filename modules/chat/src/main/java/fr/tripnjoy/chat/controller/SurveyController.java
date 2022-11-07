package fr.tripnjoy.chat.controller;

import fr.tripnjoy.chat.dto.request.PostSurveyRequest;
import fr.tripnjoy.chat.dto.request.UpdateSurveyRequest;
import fr.tripnjoy.chat.dto.request.VoteSurveyRequest;
import fr.tripnjoy.chat.exception.SurveyVoteException;
import fr.tripnjoy.chat.model.SurveyModel;
import fr.tripnjoy.chat.service.ChannelService;
import fr.tripnjoy.chat.service.SurveyService;
import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
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

    public SurveyController(SurveyService surveyService, ChannelService channelService)
    {
        this.surveyService = surveyService;
        this.channelService = channelService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a survey by id")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public SurveyModel getSurveyById(@RequestHeader("userId") long userId, @PathVariable("id") long surveyId)
    {
        SurveyModel surveyModel = surveyService.getSurveyById(surveyId);
        channelService.checkUserHasAccessToChannel(surveyModel.getChannelId(), userId);
        return surveyModel;
    }

    @GetMapping("channel/{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public List<SurveyModel> getSurveysByChannelId(@RequestHeader("userId") long userId, @PathVariable("id") long channelId)
    {
        channelService.checkUserHasAccessToChannel(channelId, userId);
        return surveyService.getSurveysByChannelId(channelId);
    }

    @GetMapping("quizz/{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public List<SurveyModel> getQuizz(@RequestHeader("userId") long userId, @PathVariable("id") long channelId)
    {
        channelService.checkUserHasAccessToChannel(channelId, userId);
        return surveyService.getQuizz(channelId, userId);
    }

    @PostMapping("{id}")
    @Operation(summary = "Create a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been created")
    public SurveyModel createSurvey(@RequestHeader("userId") long userId, @PathVariable("id") long channelId,
                                    @RequestBody PostSurveyRequest postSurveyRequest)
    {
        channelService.checkUserHasAccessToChannel(channelId, userId);
        return surveyService.createSurvey(channelId, userId, postSurveyRequest);
    }

    @PostMapping("vote/{id}")
    @Operation(summary = "Vote for a survey")
    @ApiResponse(responseCode = "200", description = "The vote has been submitted")
    public SurveyModel submitVote(@RequestHeader("userId") long userId, @PathVariable("id") long surveyId,
                                  @RequestBody VoteSurveyRequest voteSurveyRequest)
    {
        channelService.checkUserHasAccessToChannel(surveyService.getSurveyById(surveyId).getChannelId(), userId);
        return surveyService.submitVote(surveyId, userId, voteSurveyRequest);
    }

    @DeleteMapping("vote/{id}")
    @Operation(summary = "Deletes a vote for a survey")
    @ApiResponse(responseCode = "200", description = "The vote has been submitted")
    public void deleteVote(@RequestHeader("userId") long userId, @PathVariable("id") long voteId)
    {
        surveyService.deleteVote(voteId, userId);
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public SurveyModel updateSurvey(@RequestHeader("userId") long userId, @PathVariable("id") long surveyId,
                                    @RequestBody UpdateSurveyRequest updateSurveyRequest)
    {
        return surveyService.updateSurvey(surveyId, userId, updateSurveyRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public void deleteSurvey(@RequestHeader("userId") long userId, @PathVariable("id") long surveyId)
    {
        surveyService.deleteSurvey(surveyId, userId);
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
    public String getError(SurveyVoteException exception)
    {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception)
    {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }
}
