package fr.tripnjoy.profiles.api.client;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.profiles.dto.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.model.ProfileModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "SERVICE-PROFILES", contextId = "SERVICE-PROFILES-PROFILES")
public interface ProfileFeignClient {

    @PostMapping("/group/{groupId}")
    ProfileModel createGroupProfile(@RequestHeader("roles") final List<String> roles, @PathVariable("groupId") long groupId,
                                    @RequestBody ProfileCreationRequest profileCreationRequest);

    @GetMapping("exists/{profileId}")
    BooleanResponse checkExists(@PathVariable("profileId") final long profileId);
}
