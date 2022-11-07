package fr.tripnjoy.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"fr.tripnjoy.users", "fr.tripnjoy.groups"})
public class ChatApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(ChatApplication.class, args);
    }

}
