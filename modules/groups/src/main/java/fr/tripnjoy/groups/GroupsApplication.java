package fr.tripnjoy.groups;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "fr.tripnjoy.users")
public class GroupsApplication {
    private static final Logger logger = LoggerFactory.getLogger(GroupsApplication.class);

    public static void main(String[] args)
    {
        SpringApplication.run(GroupsApplication.class, args);
    }@Bean

    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    public CommandLineRunner startup(@Value("${spring.profiles.active}") String profiles)
    {
        return args -> logger.info("Application started with profiles {}", profiles);
    }

}
