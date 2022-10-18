package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.SurveyVoteException;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.VoteSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
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

    public SurveyController(SurveyService surveyService) {
        this.surveyService = surveyService;
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The data have been successfully retrieved")
    public List<SurveyModel> getSurveysByChannelId(@PathVariable("id") long channelId) {
        return surveyService.getByChannelId(channelId);
    }

    @PostMapping("{id}")
    @Operation(summary = "Get all the surveys in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been created")
    public SurveyModel createSurvey(@PathVariable("id") long channelId, @RequestBody PostSurveyRequest postSurveyRequest) {
        return surveyService.createSurvey(channelId, postSurveyRequest);
    }

    @PostMapping("vote/{id}")
    @Operation(summary = "Vote for a survey")
    @ApiResponse(responseCode = "200", description = "The vote has been submitted")
    public SurveyModel subitVote(@PathVariable("id") long surveyId, @RequestBody VoteSurveyRequest voteSurveyRequest) {
        return surveyService.submitVote(surveyId, voteSurveyRequest);
    }

    @PatchMapping("{id}")
    @Operation(summary = "Update a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public SurveyModel updateSurvey(@PathVariable("id") long surveyId, @RequestBody UpdateSurveyRequest updateSurveyRequest) {
        return surveyService.updateSurvey(surveyId, updateSurveyRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public void deleteSurvey(@PathVariable("id") long surveyId) {
        surveyService.deleteSurvey(surveyId);
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
    public String getError(ForbiddenOperationException exception) {
        logger.debug(ERROR_ON_REQUEST, exception);
        return exception.getMessage();
    }
}
