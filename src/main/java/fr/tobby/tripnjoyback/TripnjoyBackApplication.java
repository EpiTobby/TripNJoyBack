package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.model.response.ScanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@SpringBootApplication
public class TripnjoyBackApplication {
	private static final Logger logger = LoggerFactory.getLogger(TripnjoyBackApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TripnjoyBackApplication.class, args);
	}

	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

	@Bean
	public CommandLineRunner startup(@Value("${spring.profiles.active}") String profiles)
	{
		return args -> {
			logger.info("Application started with profiles {}", profiles);
		};
	}

	@Bean
	public CommandLineRunner testScan()
	{
		return args -> {

			ScanResponse scan = service.scan();

			for (final Map.Entry<String, Float> entry : scan.getItems().entrySet())
			{
				logger.info("{}: {}", entry.getKey(), entry.getValue());
			}
		};
	}
}
