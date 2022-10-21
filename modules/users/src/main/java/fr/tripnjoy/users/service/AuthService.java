package fr.tripnjoy.users.service;

import fr.tripnjoy.users.entity.ConfirmationCodeEntity;
import fr.tripnjoy.users.entity.UserEntity;
import fr.tripnjoy.users.exception.*;
import fr.tripnjoy.users.model.GoogleTokenVerificationModel;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.*;
import fr.tripnjoy.users.model.response.GoogleUserResponse;
import fr.tripnjoy.users.model.response.UserIdResponse;
import fr.tripnjoy.users.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    public static final String BAD_CONFIRMATION_CODE = "Bad Confirmation Code";

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final GenderRepository genderRepository;
    private final CityService cityService;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final UserRoleRepository userRoleRepository;
    private final LanguageRepository languageRepository;

    @Value("${google.secret}")
    private String googleSecret;

    static final String GOOGLE_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

    public AuthService(final UserRepository userRepository, final PasswordEncoder encoder,
                       final GenderRepository genderRepository,
                       final CityService cityService, final ConfirmationCodeRepository confirmationCodeRepository,
                       final UserRoleRepository userRoleRepository, LanguageRepository languageRepository)
    {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.genderRepository = genderRepository;
        this.cityService = cityService;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.userRoleRepository = userRoleRepository;
        this.languageRepository = languageRepository;
    }

    @Transactional
    public UserModel createUser(UserCreationRequest model) throws UserCreationException
    {

        String city = model.getCity().toUpperCase().trim();
        UserEntity userEntity = UserEntity.builder()
                                          .firstname(model.getFirstname())
                                          .lastname(model.getLastname())
                                          .password(encoder.encode(model.getPassword()))
                                          .email(model.getEmail())
                                          .birthDate(model.getBirthDate().toInstant())
                                          .createdDate(Instant.now())
                                          .gender(genderRepository.findByValue(model.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + model.getGender())))
                                          .phoneNumber(model.getPhoneNumber())
                                          .confirmed(false)
                                          .city(cityService.getOrAddCity(city))
                                          .language(languageRepository.findByValue(model.getLanguage().toUpperCase()).orElseThrow(() -> new UserCreationException("Invalid language " + model.getLanguage())))
                                          .roles(List.of(userRoleRepository.getByName("default")))
                                          .build();
        UserModel created = UserModel.of(createUser(userEntity));
        // FIXME: prom
        //        promStats.getUserCount().set(userRepository.count());
        generateConfirmationCode(created);
        logger.debug("Created new user {}", created);
        return created;
    }

    @Transactional
    public UserModel createAdmin(UserCreationRequest model) throws UserCreationException
    {
        UserEntity userEntity = userRepository.getById(createUser(model).getId());
        userEntity.setRoles(List.of(userRoleRepository.getByName("admin"), userRoleRepository.getByName("default")));
        return UserModel.of(userEntity);
    }

    UserEntity createUser(UserEntity entity)
    {
        if (userRepository.findByEmail(entity.getEmail()).isPresent())
            throw new UserCreationException("Email is already in use");
        // FIXME: mail service
        //        if (!userMailUtils.userEmailIsValid(entity.getEmail()))
        //            throw new UserCreationException("Email is not valid");
        return userRepository.save(entity);
    }

    @Transactional
    public GoogleUserResponse signInUpGoogle(GoogleRequest model) throws UserCreationException
    {
        try
        {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GoogleTokenVerificationModel> response = restTemplate.getForEntity(GOOGLE_URL + model.getAccessToken(), GoogleTokenVerificationModel.class);
            var errorMessage = "Google account is not valid";
            if (response.getStatusCode() != HttpStatus.OK)
                throw new UserCreationException(errorMessage);

            GoogleTokenVerificationModel body = response.getBody();
            if (body == null)
                throw new UserCreationException(errorMessage);

            if (!Objects.equals(body.getEmail(), model.getEmail()) || !Objects.equals(body.getAud(), googleSecret))
                throw new UserCreationException(errorMessage);

        }
        catch (RestClientException e)
        {
            throw new UserCreationException("An error occurred while login with google");
        }

        Optional<UserEntity> user = userRepository.findByEmail(model.getEmail());

        if (user.isPresent())
        {
            return new GoogleUserResponse(UserModel.of(user.get()), false);
        }

        var genders = genderRepository.findAll().iterator();
        UserEntity userEntity = UserEntity.builder()
                                          .firstname(model.getFirstname())
                                          .lastname(model.getLastname())
                                          .password(null)
                                          .email(model.getEmail())
                                          .createdDate(Instant.now())
                                          .phoneNumber(model.getPhoneNumber())
                                          .profilePicture(model.getProfilePicture())
                                          .birthDate(null)
                                          .gender(genders.hasNext() ? genders.next() : null)
                                          .confirmed(true)
                                          .roles(List.of(userRoleRepository.getByName("default")))
                                          .build();

        UserModel userModel = UserModel.of(userRepository.save(userEntity));
        // FIXME: prom
        //        promStats.getUserCount().set(userRepository.count());
        logger.debug("Created new user {}", userModel);
        return new GoogleUserResponse(userModel, true);
    }

    private ConfirmationCodeEntity generateConfirmationCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        // FIXME: send mail via mail service
        // userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        logger.debug("Generated account confirmation code {}", confirmationCodeEntity);
        return confirmationCodeEntity;
    }

    private ConfirmationCodeEntity generateForgottenPasswordCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        // FIXME: send mail via mail service
        // userMailUtils.sendForgottenPasswordCodeMail(userModel, confirmationCodeEntity.getValue());
        logger.debug("Generated forgotten password code {}", confirmationCodeEntity);
        return confirmationCodeEntity;
    }

    @Transactional
    public void confirmUser(long userId, ConfirmationCodeModel confirmationCodeModel)
    {
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(confirmationCodeModel.getValue()).orElseThrow(() -> new BadConfirmationCodeException(BAD_CONFIRMATION_CODE));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (userId == confirmationCode.getUserId())
        {
            confirmationCodeRepository.delete(confirmationCode);
            if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0)
            {
                generateConfirmationCode(UserModel.of(userEntity));
                throw new ExpiredCodeException("This code has expired. A new one has been sent to " + userEntity.getEmail());
            }
            else
            {
                updateConfirmation(userId);
                logger.debug("Confirmation of user account {}", userEntity.getEmail());
            }
        }
        else
            throw new BadConfirmationCodeException(BAD_CONFIRMATION_CODE);
    }

    @Transactional
    public void resendConfirmationCode(long userId)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isConfirmed())
            throw new UserAlreadyConfirmedException("User is already confirmed");
        Optional<ConfirmationCodeEntity> confirmationCodeEntity = confirmationCodeRepository.findByUserId(userId);
        confirmationCodeEntity.ifPresent(confirmationCodeRepository::delete);
        generateConfirmationCode(UserModel.of(user));
    }

    @Transactional
    public UserModel updateConfirmation(long userId) throws UserNotFoundException
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setConfirmed(true);
        UserModel userModel = UserModel.of(user);
        // FIXME: send mail via mail service
        //        userMailUtils.sendConfirmationSuccessMail(userModel);
        return userModel;
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPassword)
    {
        UserEntity userEntity = userRepository.findByEmail(forgotPassword.getEmail())
                                              .orElseThrow(() -> new UserNotFoundException("No user with email " + forgotPassword.getEmail()));
        generateForgottenPasswordCode(UserModel.of(userEntity));
    }

    @Transactional
    public UserIdResponse validateCodePassword(ValidateCodePasswordRequest validateCodePasswordRequest)
    {
        UserEntity userEntity = userRepository.findByEmail(validateCodePasswordRequest.getEmail()).filter(UserEntity::isConfirmed)
                                              .orElseThrow(() -> new UserNotFoundException("No user with email " + validateCodePasswordRequest.getEmail()));
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(validateCodePasswordRequest.getValue()).orElseThrow(() -> new BadConfirmationCodeException(BAD_CONFIRMATION_CODE));
        if (userEntity.getId() == confirmationCode.getUserId())
        {
            confirmationCodeRepository.delete(confirmationCode);
            if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0)
            {
                generateForgottenPasswordCode(UserModel.of(userEntity));
                throw new ExpiredCodeException("This code has expired. A new one has been sent to " + userEntity.getEmail());
            }
            else
            {
                userEntity.setPassword(encoder.encode(validateCodePasswordRequest.getNewPassword()));
                // FIXME: send mail via mail service
                //                userMailUtils.sendUpdatePasswordMail(UserModel.of(userEntity));
                logger.debug("New password set for user {}", validateCodePasswordRequest.getEmail());
                return UserIdResponse.builder().userId(userEntity.getId()).build();
            }
        }
        else
            throw new BadConfirmationCodeException(BAD_CONFIRMATION_CODE);
    }

    @Transactional
    public void updatePassword(long userId, UpdatePasswordRequest updatePasswordRequest)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (encoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword()))
        {
            user.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
            // FIXME: send mail via mail service
            //            userMailUtils.sendUpdatePasswordMail(UserModel.of(user));
        }
        else
            throw new UpdatePasswordException("Bad Password");
    }
}
