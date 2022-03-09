package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final CityService cityService;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       final CityService cityService)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.cityService = cityService;
    }

    public Iterable<UserEntity> getAll()
    {
        return userRepository.findAll();
    }

    public UserEntity createUser(UserCreationModel model) throws UserCreationException
    {
        if (userRepository.findByEmail(model.getEmail()).isPresent()) {
            throw new UserCreationException("Email is already in use");
        }
        UserEntity userEntity = UserEntity.builder()
                .firstName(model.getFirstname())
                .lastName(model.getLastname())
                .password(model.getPassword())
                .email(model.getEmail())
                .birthdate(model.getBirthDate())
                .createdDate(Instant.now())
                .gender(genderRepository.findByValue(model.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + model.getGender())))
                .phoneNumber(model.getPhoneNumber())
                .build();
        return userRepository.save(userEntity);
    }

    public Optional<UserEntity> findById(final long id)
    {
        return userRepository.findById(id);
    }

    public UserEntity updatePhoneNumber(long userId, String phoneNumber)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    public UserEntity updateCity(long userId, String city)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setCity(cityService.getOrAddCity(city));
        return user;
    }
}
