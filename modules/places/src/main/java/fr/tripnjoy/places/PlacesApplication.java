package fr.tripnjoy.places;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PlacesApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(PlacesApplication.class, args);
    }

}
