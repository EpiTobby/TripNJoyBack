package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.service.ScanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
	public CommandLineRunner startup(@Value("${spring.profiles.active}") String profiles, ScanService scanService)
	{
		return args -> {
			logger.info("Application started with profiles {}", profiles);
//			ScanResponse scan = scanService.scan("https://images-ext-1.discordapp.net/external/J3_EjXEM31n8QSyedYy5V3MqJNiffbfs7EFmopdu85s/https/ocr.space/Content/Images/receipt-ocr-original.jpg?width=459&height=1138");
//
//			scan.getItems().forEach((name, price) -> logger.info("{}: {}", name, price));
//			logger.info("Total: {}", scan.getTotal());
		};
	}
}
