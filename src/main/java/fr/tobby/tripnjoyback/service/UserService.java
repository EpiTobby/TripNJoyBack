package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final CityService cityService;
    private final UserMailUtils userMailUtils;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       final ConfirmationCodeRepository confirmationCodeRepository, final CityService cityService,
                       final UserMailUtils userMailUtils, final PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.cityService = cityService;
        this.userMailUtils = userMailUtils;
        this.encoder = encoder;
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
                .password(encoder.encode(model.getPassword()))
                .email(model.getEmail())
                .birthDate(model.getBirthDate())
                .createdDate(Instant.now())
                .gender(genderRepository.findByValue(model.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + model.getGender())))
                .phoneNumber(model.getPhoneNumber())
                .confirmed(false)
                .build();
        UserModel userModel = UserModel.of(userRepository.save(userEntity));
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userRepository.findByEmail(userEntity.getEmail()).get().getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        return userModel;
    }

    public Optional<UserModel> findById(final long id)
    {
        return userRepository.findById(id).map(UserModel::of);
    }

    public boolean confirmUser(long userId, ConfirmationCodeModel confirmationCodeModel){
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(confirmationCodeModel.getValue()).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
        Boolean isValid = userId == confirmationCode.getUserId();
        if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0)
            throw new ExpiredCodeException("This code has expired");
        if (isValid) {
            confirmationCodeRepository.delete(confirmationCode);
            return updateConfirmation(userId).isConfirmed();
        }
        else
            return false;
    }

    @Transactional
    public UserModel updateConfirmation(long userId) throws UserNotFoundException{
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setConfirmed(true);
        UserModel userModel = UserModel.of(user);
        userRepository.save(user);
        userMailUtils.sendConfirmationSuccessMail(userModel);
        return userModel;
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
