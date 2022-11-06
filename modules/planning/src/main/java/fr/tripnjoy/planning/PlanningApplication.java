package fr.tripnjoy.planning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "fr.tripnjoy.groups")
public class PlanningApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(PlanningApplication.class, args);
    }

}
