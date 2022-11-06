package fr.tripnjoy.reports.controller;

import fr.tripnjoy.profiles.dto.request.SubmitRecommendationRequest;
import fr.tripnjoy.profiles.dto.response.RecommendationResponse;
import fr.tripnjoy.profiles.exception.RecommendationNotFoundException;
import fr.tripnjoy.reports.model.RecommendationModel;
import fr.tripnjoy.reports.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "recommendations")
public class RecommendationController {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService)
    {
        this.recommendationService = recommendationService;
    }

    @PostMapping("")
    @Operation(summary = "Create a recommendation")
    @ApiResponse(responseCode = "200", description = "The recommendation has been created")
    @ApiResponse(responseCode = "422", description = "The submitter or reviewed user do not exist")
    public RecommendationResponse submitRecommendation(@RequestHeader("userId") long userId, @RequestBody SubmitRecommendationRequest submitReportRequest)
    {
        return recommendationService.submitRecommendation(userId, submitReportRequest.getReviewedUserId(), submitReportRequest.getComment())
                                    .toDtoResponse();
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the recommendation of a user")
    public List<RecommendationResponse> getByReviewedUserId(@PathVariable("id") long reviewedUserId)
    {
        return recommendationService.getByReviewedUserId(reviewedUserId)
                .stream()
                .map(RecommendationModel::toDtoResponse)
                .toList();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a recommendation")
    @ApiResponse(responseCode = "200", description = "The recommendation has been deleted")
    @ApiResponse(responseCode = "404", description = "The recommendation does not exist")
    public void deleteRecommendation(@PathVariable("id") long recommendationId)
    {
        recommendationService.deleteRecommendation(recommendationId);
    }

    @ExceptionHandler(RecommendationNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(RecommendationNotFoundException exception)
    {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
