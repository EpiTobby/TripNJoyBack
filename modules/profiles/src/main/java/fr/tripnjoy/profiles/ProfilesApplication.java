package fr.tripnjoy.profiles;

import fr.tripnjoy.common.broker.RabbitMQConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@EnableFeignClients(basePackages = {"fr.tripnjoy.users", "fr.tripnjoy.groups"})
public class ProfilesApplication {
    private static final Logger logger = LoggerFactory.getLogger(ProfilesApplication.class);

    public static void main(String[] args)
    {
        SpringApplication.run(ProfilesApplication.class, args);
    }

    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

    @Bean
    public CommandLineRunner startup(@Value("${spring.profiles.active}") String profiles)
    {
        return args -> logger.info("Application started with profiles {}", profiles);
    }

    @Bean
    public RabbitTemplate rabbitTemplate()
    {
        return new RabbitMQConfiguration().rabbitTemplate();
    }
}
