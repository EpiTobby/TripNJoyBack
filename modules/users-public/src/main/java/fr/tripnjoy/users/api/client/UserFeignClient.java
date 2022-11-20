package fr.tripnjoy.users.api.client;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.common.exception.UnauthorizedException;
import fr.tripnjoy.users.api.response.FirebaseTokenResponse;
import fr.tripnjoy.users.api.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value = "SERVICE-USERS", path = "/users", contextId = "SERVICE-USERS-USERS")
public interface UserFeignClient {

    @GetMapping("{id}")
    UserResponse getUserById(@RequestHeader("roles") List<String> roles, @PathVariable("id") final long userId) throws UnauthorizedException;

    @GetMapping("me")
    UserResponse getCurrentUser(@RequestHeader("username") String username);

    @GetMapping("/{id}/exists")
    BooleanResponse exists(@PathVariable("id") final long userId);

    @GetMapping("{id}/firebase")
    FirebaseTokenResponse getFirebaseToken(@PathVariable("id") final long userId);
}
