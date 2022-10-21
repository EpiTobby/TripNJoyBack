package com.example.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class GatewayApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, @Value("${server.port}") String port)
    {
        final String[] services = new String[] {};
        RouteLocatorBuilder.Builder routes = builder.routes();

        for (final String service : services)
        {
            String path = String.format("/%s/**", service);
            String serviceUri = "lb://SERVICE-" + service.toUpperCase() + "/";
            routes = routes.route(r -> r.path(path)
                                        .filters(f -> f.rewritePath("/" + service + "/(?<path>.*)", "/${path}"))
                                        .uri(serviceUri));
        }
        return routes.route(r -> r.path("/v3/api-docs/**")
                                  .filters(f -> f.rewritePath("/v3/api-docs/(?<path>.*)", "/${path}/v3/api-docs"))
                                  .uri("http://localhost:" + port)
        ).build();
    }
}
