package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.exception.auth.UpdatePasswordException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.ConfirmationCodeModel;
import fr.tobby.tripnjoyback.model.UserCreationRequest;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordRequest;
import fr.tobby.tripnjoyback.model.request.UpdateEmailRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordRequest;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordRequest;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
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

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMailUtils userMailUtils;
    private final PasswordEncoder encoder;
    private final GenderRepository genderRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public AuthService(final UserRepository userRepository, final UserMailUtils userMailUtils, final PasswordEncoder encoder,
                       final GenderRepository genderRepository,
                       final ConfirmationCodeRepository confirmationCodeRepository,
                       final AuthenticationManager authenticationManager, final TokenManager tokenManager,
                       final UserDetailsService userDetailsService, final UserService userService)
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
    }

    @Transactional
    public UserModel createUser(UserCreationRequest model) throws UserCreationException
    {
        if (userRepository.findByEmail(model.getEmail()).isPresent())
        {
            throw new UserCreationException("Email is already in use");
        }
        if (!userMailUtils.userEmailIsValid(model.getEmail())){
            throw new UserCreationException("Email is not valid");
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
        generateConfirmationCode(userModel);
        return userModel;
    }

    public String login(@NonNull String username, @NonNull String password) throws AuthenticationException
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserModel userModel = userService.findByEmail(username).orElseThrow();
        return tokenManager.generateFor(userDetails, userModel.getId());
    }

    private ConfirmationCodeEntity generateConfirmationCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        return confirmationCodeEntity;
    }

    private ConfirmationCodeEntity generateForgottenPasswordCode(UserModel userModel)
    {
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendForgottenPasswordCodeMail(userModel, confirmationCodeEntity.getValue());
        return confirmationCodeEntity;
    }

    public boolean confirmUser(long userId, ConfirmationCodeModel confirmationCodeModel)
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
                return updateConfirmation(userId).isConfirmed();
            }
        }
        throw new BadConfirmationCodeException("Bad Confirmation Code");
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
    public void updateEmail(long userId, UpdateEmailRequest updateEmailRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (!encoder.matches(updateEmailRequest.getPassword(),user.getPassword())) {
            throw new BadCredentialsException("Bad Password");
        }
        if (!userMailUtils.userEmailIsValid(updateEmailRequest.getNewEmail())){
            throw new UpdateEmailException("Email is not valid");
        }
        user.setEmail(updateEmailRequest.getNewEmail());
        userMailUtils.sendUpdateMail(UserModel.of(user));
    }
}
