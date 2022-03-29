package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.exception.auth.UpdatePasswordException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.*;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMailUtils userMailUtils;
    private final PasswordEncoder encoder;
    private final GenderRepository genderRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final UserRoleRepository userRoleRepository;

    public AuthService(final UserRepository userRepository, final UserMailUtils userMailUtils, final PasswordEncoder encoder,
                       final GenderRepository genderRepository,
                       final ConfirmationCodeRepository confirmationCodeRepository,
                       final AuthenticationManager authenticationManager, final TokenManager tokenManager,
                       final UserDetailsService userDetailsService, final UserService userService,
                       final UserRoleRepository userRoleRepository)
    {
        this.userRepository = userRepository;
        this.userMailUtils = userMailUtils;
        this.encoder = encoder;
        this.genderRepository = genderRepository;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public UserModel createUser(UserCreationRequest model) throws UserCreationException
    {
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
                                          .roles(List.of(userRoleRepository.getByName("default")))
                                          .build();
        UserModel created = UserModel.of(createUser(userEntity));
        generateConfirmationCode(created);
        logger.debug("Created new user " + created);
        return created;
    }

    UserEntity createUser(UserEntity entity)
    {
        if (userRepository.findByEmail(entity.getEmail()).isPresent())
            throw new UserCreationException("Email is already in use");
        if (!userMailUtils.userEmailIsValid(entity.getEmail()))
            throw new UserCreationException("Email is not valid");
        return userRepository.save(entity);
    }

    public String login(@NonNull String username, @NonNull String password) throws AuthenticationException
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserModel userModel = userService.findByEmail(username).orElseThrow();
        String token = tokenManager.generateFor(userDetails, userModel.getId());
        logger.debug("User {} logged in. jwt = {}", username, token);
        return token;
    }

    private ConfirmationCodeEntity generateConfirmationCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        logger.debug("Generated account confirmation code {}", confirmationCodeEntity);
        return confirmationCodeEntity;
    }

    private ConfirmationCodeEntity generateForgottenPasswordCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendForgottenPasswordCodeMail(userModel, confirmationCodeEntity.getValue());
        logger.debug("Generated forgotten password code {}", confirmationCodeEntity);
        return confirmationCodeEntity;
    }

    public void confirmUser(long userId, ConfirmationCodeModel confirmationCodeModel)
    {
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(confirmationCodeModel.getValue()).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
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
            throw new BadConfirmationCodeException("Bad Confirmation Code");
    }

    @Transactional
    public void resendConfirmationCode(long userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (user.isConfirmed())
            throw new UserAlreadyConfirmedException("User is already confirmed");
        Optional<ConfirmationCodeEntity> confirmationCodeEntity  = confirmationCodeRepository.findByUserId(userId);
        if (confirmationCodeEntity.isPresent())
            confirmationCodeRepository.delete(confirmationCodeEntity.get());
        generateConfirmationCode(UserModel.of(user));
    }

    @Transactional
    public UserModel updateConfirmation(long userId) throws UserNotFoundException
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setConfirmed(true);
        UserModel userModel = UserModel.of(user);
        userRepository.save(user);
        userMailUtils.sendConfirmationSuccessMail(userModel);
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
        UserEntity userEntity = userRepository.findByEmail(validateCodePasswordRequest.getEmail()).filter(user -> user.isConfirmed())
                                              .orElseThrow(() -> new UserNotFoundException("No user with email " + validateCodePasswordRequest.getEmail()));
        ;
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(validateCodePasswordRequest.getValue()).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
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
                userMailUtils.sendUpdatePasswordMail(UserModel.of(userEntity));
                logger.debug("New password set for user {}", validateCodePasswordRequest.getEmail());
                return UserIdResponse.builder().userId(userEntity.getId()).build();
            }
        }
        else
            throw new BadConfirmationCodeException("Bad confirmation code");
    }

    @Transactional
    public void updatePassword(long userId, UpdatePasswordRequest updatePasswordRequest)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (encoder.matches(updatePasswordRequest.getOldPassword(),user.getPassword())) {
            user.setPassword(encoder.encode(updatePasswordRequest.getNewPassword()));
            userMailUtils.sendUpdatePasswordMail(UserModel.of(user));
        }
        else
            throw new UpdatePasswordException("Bad Password");
    }

    @Transactional
    public String updateEmail(long userId, UpdateEmailRequest updateEmailRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (!encoder.matches(updateEmailRequest.getPassword(),user.getPassword())) {
            throw new BadCredentialsException("Bad Password");
        }
        if (!userMailUtils.userEmailIsValid(updateEmailRequest.getNewEmail())){
            throw new UpdateEmailException("Email is not valid");
        }
        String newEmail = updateEmailRequest.getNewEmail().toLowerCase().trim();
        if (userRepository.findByEmail(newEmail).isEmpty()){
            user.setEmail(newEmail);
            userMailUtils.sendUpdateMail(UserModel.of(user));
            return tokenManager.generateFor(user.getEmail(), userId);
        }
        else
            throw new UpdateEmailException("Email already used");
    }
}
