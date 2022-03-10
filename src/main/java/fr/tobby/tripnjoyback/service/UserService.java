package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final CityService cityService;
    private final JavaMailSender mailSender;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       final CityService cityService, final JavaMailSender mailSender)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.cityService = cityService;
        this.mailSender = mailSender;
    }

    public Iterable<UserEntity> getAll()
    {
        return userRepository.findAll();
    }

    @Transactional
    public UserModel createUser(UserCreationModel model) throws UserCreationException
    {
        if (userRepository.findByEmail(model.getEmail()).isPresent()) {
            throw new UserCreationException("Email is already in use");
        }
        UserEntity userEntity = UserEntity.builder()
                .firstname(model.getFirstname())
                .lastname(model.getLastname())
                .password(model.getPassword())
                .email(model.getEmail())
                .birthDate(model.getBirthDate())
                .createdDate(Instant.now())
                .gender(genderRepository.findByValue(model.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + model.getGender())))
                .phoneNumber(model.getPhoneNumber())
                .build();
        sendSuccessMail(userEntity);
        return UserModel.of(userRepository.save(userEntity));
    }

    private void sendSuccessMail(UserEntity user)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("tripnjoy.contact@gmail.com");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Confirmation de la création de votre compte TripNJoy");
        mailMessage.setText("Bonjour " + user.getFirstname() + ",\n\tBienvenue dans notre application.\nCordialement, l'équipe TripNJoy");
        mailSender.send(mailMessage);
    }

    public Optional<UserModel> findById(final long id)
    {
        return userRepository.findById(id).map(UserModel::of);
    }

    @Transactional
    public UserModel updatePhoneNumber(long userId, String phoneNumber) throws UserNotFoundException
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setPhoneNumber(phoneNumber);
        return UserModel.of(user);
    }

    @Transactional
    public UserModel updateCity(long userId, String city) throws UserNotFoundException
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setCity(cityService.getOrAddCity(city));
        return UserModel.of(user);
    }
}
