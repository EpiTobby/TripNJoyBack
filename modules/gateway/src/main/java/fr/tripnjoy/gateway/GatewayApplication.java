package fr.tripnjoy.gateway;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.*;

@SpringBootApplication
@EnableWebFlux
public class GatewayApplication {

    public static final String[] SERVICES = {"users", "groups", "profiles", "reports", "mails", "chat", "expenses", "places", "planning"};

    public static void main(String[] args)
    {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, @Value("${server.port}") String port, JwtAuthFilter authFilter)
    {
        RouteLocatorBuilder.Builder routes = builder.routes();

        for (final String service : SERVICES)
        {
            String path = String.format("/%s/**", service);
            String serviceUri = "lb://SERVICE-" + service.toUpperCase() + "/";
            routes = routes.route(r -> r.path(path)
                                        .filters(f -> f.filter(authFilter).rewritePath("/" + service + "/(?<path>.*)", "/${path}"))
                                        .uri(serviceUri));
        }
        return routes.route(r -> r.path("/v3/api-docs/**")
                                  .filters(f -> f.rewritePath("/v3/api-docs/(?<path>.*)", "/${path}/v3/api-docs"))
                                  .uri("http://localhost:" + port)
        ).build();
    }

    @Autowired
    RouteDefinitionLocator locator;

    @Bean
    public List<GroupedOpenApi> apis()
    {
        List<GroupedOpenApi> groups = new ArrayList<>();
        for (final String service : SERVICES)
        {
            groups.add(GroupedOpenApi.builder()
                                     .pathsToMatch("/" + service + "/**").setGroup(service)
                                     .build());
        }
        return groups;
    }
}

@RestController
class MergeApiDocsController
{
    @Autowired
    private DiscoveryClient discoveryClient;

    private ApiDocs getApiDocsForService(String serviceName, String servicePath)
    {
        ServiceInstance clienta = discoveryClient.getInstances(serviceName).get(0);
        String url = clienta.getUri().toString() + "/v3/api-docs";
        ResponseEntity<ApiDocs> resp = new RestTemplate().getForEntity(url, ApiDocs.class);
        if (resp.getStatusCodeValue() != 200 || resp.getBody() == null)
            return null;
        ApiDocs apiDocs = resp.getBody();
        Map<String, Object> newPaths = new HashMap<>();
        apiDocs.getPaths().forEach((path, obj) -> newPaths.put(servicePath + path, obj));
        apiDocs.setPaths(newPaths);
        return apiDocs;
    }

    private ApiDocs mergeApiDocs(Collection<ApiDocs> docs)
    {
        ApiDocs aggregate = new ApiDocs();
        for (final ApiDocs doc : docs)
        {
            aggregate.getPaths().putAll(doc.getPaths());
            aggregate.getComponents().getSchemas().putAll(doc.getComponents().getSchemas());
        }
        return aggregate;
    }

    @GetMapping("docs")
    public ApiDocs docs()
    {
        Collection<ApiDocs> docs = new ArrayList<>();
        for (final String service : GatewayApplication.SERVICES)
        {
            ApiDocs api = getApiDocsForService("SERVICE-" + service.toUpperCase(), "/" + service);
            docs.add(api);
        }

        return mergeApiDocs(docs);
    }
}

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
class ApiDocs
{
    private String openapi = "3.0.1";
    private Infos info = new Infos();
    private Map<String, Object> paths = new HashMap<>();
    private Components components = new Components();

    @Getter
    @Setter
    public static class Infos {
        private String title = "OpenAPI definition";
        private String version = "v0";
    }

    @Getter
    @Setter
    public static class Components {
        private Map<String, Object> schemas = new HashMap<>();
    }
}