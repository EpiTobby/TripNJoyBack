package fr.tripnjoy.profiles.api.client;

import fr.tripnjoy.profiles.dto.request.SubmitRecommendationRequest;
import fr.tripnjoy.profiles.dto.response.RecommendationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "SERVICE-REPORTS", contextId = "SERVICE-REPORTS-RECOMMENDATION", path = "recommendations")
public interface RecommendationFeignClient {

    @PostMapping("")
    RecommendationResponse submitRecommendation(@RequestHeader("userId") long userId, @RequestBody SubmitRecommendationRequest submitReportRequest);

    @GetMapping("{id}")
    List<RecommendationResponse> getByReviewedUserId(@PathVariable("id") long reviewedUserId);

    @DeleteMapping("{id}")
    void deleteRecommendation(@PathVariable("id") long recommendationId);
}
