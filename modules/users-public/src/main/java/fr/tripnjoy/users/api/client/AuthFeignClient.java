package fr.tripnjoy.users.api.client;

import fr.tripnjoy.users.api.request.CheckJwtRequest;
import fr.tripnjoy.users.api.response.CheckJwtResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "SERVICE-USERS", contextId = "SERVICE-USERS-AUTH")
public interface AuthFeignClient {

    @PostMapping("/auth/jwtcheck")
    CheckJwtResponse checkJwt(@RequestBody CheckJwtRequest request);
}
