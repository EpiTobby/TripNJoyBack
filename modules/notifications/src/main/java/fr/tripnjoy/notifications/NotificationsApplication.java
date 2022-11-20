package fr.tripnjoy.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"fr.tripnjoy.users", "fr.tripnjoy.groups"})
@EnableDiscoveryClient
public class NotificationsApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(NotificationsApplication.class, args);
    }

}
