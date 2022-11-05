package fr.tripnjoy.mails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "fr.tripnjoy.users")
public class MailsApplication {

    public static void main(String[] args)
    {
        SpringApplication.run(MailsApplication.class, args);
    }

}
