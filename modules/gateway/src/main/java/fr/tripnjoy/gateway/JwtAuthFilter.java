package fr.tripnjoy.gateway;

import fr.tripnjoy.users.api.request.CheckJwtRequest;
import fr.tripnjoy.users.api.response.CheckJwtResponse;
import fr.tripnjoy.users.api.response.JwtUserDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GatewayFilter {

    private final List<String> permitAllPaths = List.of("/users/auth/login");

    private final DiscoveryClient discoveryClient;

    public JwtAuthFilter(final DiscoveryClient discoveryClient)
    {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final GatewayFilterChain chain)
    {
        // Authorize some specific endpoints
        if (permitAllPaths.contains(exchange.getRequest().getPath().toString()))
            return chain.filter(exchange);

        // Get jwt
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> authorization = headers.getOrEmpty("Authorization");
        String token = getJwtToken(authorization);
        if (token == null)
            return unauthorized(exchange);

        CheckJwtResponse checkJwtResponse = checkJwt(token);
        if (checkJwtResponse == null || !checkJwtResponse.isValid() || checkJwtResponse.getUserDetails() == null)
            return unauthorized(exchange);

        // Insert user details into headers
        JwtUserDetails userDetails = checkJwtResponse.getUserDetails();
        exchange.getRequest().mutate().header("userId", String.valueOf(userDetails.getUserId()));
        exchange.getRequest().mutate().header("username", userDetails.getUsername());
        exchange.getRequest().mutate().header("roles", userDetails.getRoles().toArray(new String[0]));

        return chain.filter(exchange);
    }

    private CheckJwtResponse checkJwt(String token)
    {
        List<ServiceInstance> instances = discoveryClient.getInstances("SERVICE-USERS");
        if (instances.isEmpty())
            return null;
        String uri = instances.get(0).getUri().toString() + "/auth/jwtcheck";
        ResponseEntity<CheckJwtResponse> response = new RestTemplate().postForEntity(uri, new CheckJwtRequest(token), CheckJwtResponse.class);
        if (response.getStatusCodeValue() != 200)
            return null;
        return response.getBody();
    }

    @NotNull
    private Mono<Void> unauthorized(final ServerWebExchange exchange)
    {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private String getJwtToken(List<String> authorizations)
    {
        for (final String authorization : authorizations)
        {
            String token = getJwtToken(authorization);
            if (token != null)
                return token;
        }
        return null;
    }

    private String getJwtToken(String authorization)
    {
        if (authorization.startsWith("Bearer "))
            return authorization.substring(7);
        return null;
    }
}
