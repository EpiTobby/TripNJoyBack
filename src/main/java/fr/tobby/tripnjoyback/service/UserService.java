package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
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
    private final JavaMailSender mailSender;
    private final UserMailUtils userMailUtils;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       final ConfirmationCodeRepository confirmationCodeRepository, final CityService cityService,
                       final JavaMailSender mailSender, final UserMailUtils userMailUtils, final PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.cityService = cityService;
        this.mailSender = mailSender;
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
                .registered(false)
                .build();
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userEntity.getId());
        UserModel userModel = UserModel.of(userRepository.save(userEntity));
        userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        return userModel;
    }

    public Optional<UserModel> findById(final long id)
    {
        return userRepository.findById(id).map(UserModel::of);
    }

    public boolean registerUser(long userId, String value){
        if (isValidConfirmationCode(userId, value))
            return updateRegistration(userId).isRegistered();
        else
            return false;
    }

    public boolean isValidConfirmationCode(long userId, String value) throws BadConfirmationCodeException, ExpiredCodeException{
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(value).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
        Boolean isValid = userId == confirmationCode.getUserId();
        if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0)
            throw new ExpiredCodeException("This code has expired");
        return isValid;
    }

    @Transactional
    public UserModel updateRegistration(long userId) throws UserNotFoundException{
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setRegistered(true);
        UserModel userModel = UserModel.of(user);
        userMailUtils.sendRegistrationSuccessMail(userModel);
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
