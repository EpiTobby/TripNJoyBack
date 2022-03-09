package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;

    public UserService(UserRepository userRepository, GenderRepository genderRepository)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
    }

    public Iterable<UserEntity> getAll()
    {
        return userRepository.findAll();
    }

    public UserEntity createUser(UserCreationModel model)
    {
        if (userRepository.findByEmail(model.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        UserEntity userEntity = UserEntity.builder()
                .firstName(model.getFirstname())
                .lastName(model.getLastname())
                .password(model.getPassword())
                .email(model.getEmail())
                .birthdate(model.getBirthDate())
                .createdDate(Instant.now())
                .gender(genderRepository.findByValue(model.getGender()).get())
                .build();
        return userRepository.save(userEntity);
    }
}
