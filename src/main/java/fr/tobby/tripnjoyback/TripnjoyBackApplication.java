package fr.tobby.tripnjoyback;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.repository.CityRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

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
}
