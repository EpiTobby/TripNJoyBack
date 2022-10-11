package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.ProfileRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import io.prometheus.client.exporter.HTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class PrometheusConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PrometheusConfiguration.class);

    @Bean
    public PromStats promStats() throws IOException
    {
        logger.info("Prometheus http server start on port 25570");
        HTTPServer httpServer = new HTTPServer(25570);
        return new PromStats(httpServer);
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler()
    {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Bean
    public CommandLineRunner task(ThreadPoolTaskScheduler scheduler, PromStats stats, GroupRepository groupRepository, UserRepository userRepository, ProfileRepository profileRepository)
    {
        return args -> {
            stats.getGroupCount().set(groupRepository.count());
            stats.getUserCount().set(userRepository.count());
            stats.getProfileCount().set(profileRepository.count());
            scheduler.scheduleAtFixedRate(() -> {
                stats.getRamUsed().set(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
            }, Duration.of(1, ChronoUnit.SECONDS));
        };
    }
}