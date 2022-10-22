package fr.tripnjoy.users.api.client;

import fr.tripnjoy.users.api.request.CheckJwtRequest;
import fr.tripnjoy.users.api.response.CheckJwtResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("SERVICE-USERS")
public interface AuthFeignClient {

    @RequestMapping(path = "/auth/jwtcheck", method = RequestMethod.POST)
    CheckJwtResponse checkJwt(@RequestBody CheckJwtRequest request);
}
