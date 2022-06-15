package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.RecommendationNotFoundException;
import fr.tobby.tripnjoyback.model.RecommendationModel;
import fr.tobby.tripnjoyback.model.request.SubmitRecommendationRequest;
import fr.tobby.tripnjoyback.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "recommendations")
public class RecommendationController {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("")
    @Operation(summary = "Create a recommendation")
    @ApiResponse(responseCode = "200", description = "The recommendation has been created")
    @ApiResponse(responseCode = "422", description = "The submitter or reviewed user do not exist")
    public RecommendationModel submitRecommendation(@RequestBody SubmitRecommendationRequest submitReportRequest) {
        String submitterEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return recommendationService.submitRecommendation(submitterEmail, submitReportRequest);
    }

    @GetMapping("{id}")
    @Operation(summary = "Get all the recommendation of a user")
    public List<RecommendationModel> getByReviewedUserId(@PathVariable("id") long reviewedUserId) {
        return recommendationService.getByReviewedUserId(reviewedUserId);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Delete a recommendation")
    @ApiResponse(responseCode = "200", description = "The recommendation has been deleted")
    @ApiResponse(responseCode = "404", description = "The recommendation does not exist")
    public void deleteRecommendation(@PathVariable("id") long recommendationId) {
        recommendationService.deleteRecommendation(recommendationId);
    }

    @ExceptionHandler(RecommendationNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(RecommendationNotFoundException exception) {
        logger.debug("Error on request", exception);
        return exception.getMessage();
    }
}
