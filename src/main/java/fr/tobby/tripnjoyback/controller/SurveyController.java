package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "surveys")
public class SurveyController {
    private final SurveyService surveyService;

    public SurveyController(SurveyService surveyService){
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

    @PatchMapping("{id}")
    @Operation(summary = "Update a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public SurveyModel updateSurvey(@PathVariable("id") long surveyId, @RequestBody UpdateSurveyRequest updateSurveyRequest) {
        return surveyService.updateSurvey(surveyId, updateSurveyRequest);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a survey in a channel")
    @ApiResponse(responseCode = "200", description = "The survey has been updated")
    public void updateSurvey(@PathVariable("id") long surveyId) {
        surveyService.deleteSurvey(surveyId);
    }
}
