package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserCreationModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static java.time.LocalTime.now;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final CityService cityService;
    private final JavaMailSender mailSender;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       final ConfirmationCodeRepository confirmationCodeRepository, final CityService cityService,
                       final JavaMailSender mailSender, final PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.cityService = cityService;
        this.mailSender = mailSender;
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
        sendConfirmationCodeMail(userEntity, confirmationCodeEntity);
        return UserModel.of(userRepository.save(userEntity));
    }

    private void sendConfirmationCodeMail(UserEntity user, ConfirmationCodeEntity confirmationCode)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("tripnjoy.contact@gmail.com");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Code de confirmation TripNJoy");
        mailMessage.setText("Bonjour " + user.getFirstname() + ",\n\tVoici votre code de confirmation: "
                + confirmationCode.getValue() +"\nCe dernier expirera dans 24 heures.\nCordialement, l'équipe TripNJoy");
        mailSender.send(mailMessage);
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
        sendSuccessMail(user);
        return UserModel.of(user);
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
