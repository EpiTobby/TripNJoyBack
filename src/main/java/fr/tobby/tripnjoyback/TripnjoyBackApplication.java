package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.repository.CityRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@SpringBootApplication
public class TripnjoyBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripnjoyBackApplication.class, args);
	}

	@Bean
	public CommandLineRunner test(UserRepository userRepository, GenderRepository genderRepository, CityRepository cityRepository) {
		return args -> {

			GenderEntity gender = genderRepository.findByValue("male").get();
			System.out.println(gender.getId());
			// var city = cityRepository.save(new CityEntity("Paris"));

			// userRepository.save(new UserEntity("coucou", "Robert", "passwd", "truc", Instant.now(), gender, "", city, null));

			userRepository.findAll().forEach(user -> System.out.println(user.getId()));
		};
	}

	@Bean
	public JavaMailSender getJavaMailSender(@Value("${spring.mail.password}") String mailPassword) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);

		mailSender.setUsername("tripnjoy.contact@gmail.com");
		mailSender.setPassword(mailPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return mailSender;
	}
}
