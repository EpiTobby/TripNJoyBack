package fr.tripnjoy.planning.api.client;

import fr.tripnjoy.planning.dto.request.CreateActivityRequest;
import fr.tripnjoy.planning.dto.request.UpdateActivityRequest;
import fr.tripnjoy.planning.dto.response.ActivityResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "SERVICE-PLANNING")
public interface PlanningFeignClient {

    @PostMapping
    ActivityResponse createActivity(@RequestHeader("userId") long userId, @PathVariable(name = "groupId") final long groupId,
                                    @RequestBody final CreateActivityRequest request);

    @GetMapping
    List<ActivityResponse> getActivities(@RequestHeader("userId") long userId, @PathVariable(name = "groupId") final long groupId);

    @PatchMapping("{activityId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void joinActivity(@PathVariable(name = "groupId") final long groupId,
                      @PathVariable(name = "activityId") final long activityId,
                      @RequestParam(name = "userId") final long userId);

    @PatchMapping("{activityId}/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void leaveActivity(@PathVariable(name = "groupId") final long groupId,
                       @PathVariable(name = "activityId") final long activityId,
                       @RequestParam(name = "userId") final long userId);

    @DeleteMapping("{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteActivity(@RequestHeader("userId") long userId,
                        @PathVariable(name = "groupId") final long groupId,
                        @PathVariable(name = "activityId") final long activityId);

    @PatchMapping("{activityId}")
    ActivityResponse updateActivity(@RequestHeader("userId") long userId,
                                    @PathVariable(name = "groupId") final long groupId,
                                    @PathVariable(name = "activityId") final long activityId,
                                    @NotNull @RequestBody final UpdateActivityRequest updateActivityRequest);
}
