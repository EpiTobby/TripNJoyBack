package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ConfirmationCodeEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.BadConfirmationCodeException;
import fr.tobby.tripnjoyback.exception.ExpiredCodeException;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.*;
import fr.tobby.tripnjoyback.model.request.ForgotPasswordRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePasswordRequest;
import fr.tobby.tripnjoyback.model.request.ValidateCodePasswordRequest;
import fr.tobby.tripnjoyback.model.response.UserIdResponse;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
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
        generateConfirmationCode(userModel);
        return userModel;
    }

    private ConfirmationCodeEntity generateConfirmationCode(UserModel userModel){
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendConfirmationCodeMail(userModel, confirmationCodeEntity.getValue());
        return confirmationCodeEntity;
    }

    private ConfirmationCodeEntity generateForgottenPasswordCode(UserModel userModel){
        ConfirmationCodeEntity confirmationCodeEntity = new ConfirmationCodeEntity(userModel.getId());
        confirmationCodeRepository.save(confirmationCodeEntity);
        userMailUtils.sendForgottenPasswordCodeMail(userModel, confirmationCodeEntity.getValue());
        return confirmationCodeEntity;
    }
    
    public Optional<UserModel> findById(final long id)
    {
        return userRepository.findById(id).map(UserModel::of);
    }

    public boolean confirmUser(long userId, ConfirmationCodeModel confirmationCodeModel){
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(confirmationCodeModel.getValue()).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (userId == confirmationCode.getUserId()) {
            confirmationCodeRepository.delete(confirmationCode);
            if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0) {
                generateConfirmationCode(UserModel.of(userEntity));
                throw new ExpiredCodeException("This code has expired. A new one has been sent to " + userEntity.getEmail());
            }
            else {
                return updateConfirmation(userId).isConfirmed();
            }
        }
        throw new BadConfirmationCodeException("Bad Confirmation Code");
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
    public void forgotPassword(ForgotPasswordRequest forgotPassword){
        UserEntity userEntity = userRepository.findByEmail(forgotPassword.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user with email " + forgotPassword.getEmail()));
        generateForgottenPasswordCode(UserModel.of(userEntity));
    }

    @Transactional
    public UserIdResponse validateCodePassword(ValidateCodePasswordRequest validateCodePasswordRequest){
        UserEntity userEntity = userRepository.findByEmail(validateCodePasswordRequest.getEmail()).filter(user ->user.isConfirmed())
                .orElseThrow(() -> new UserNotFoundException("No user with email " + validateCodePasswordRequest.getEmail()));;
        ConfirmationCodeEntity confirmationCode = confirmationCodeRepository.findByValue(validateCodePasswordRequest.getValue()).orElseThrow(() -> new BadConfirmationCodeException("Bad Confirmation Code"));
        if (userEntity.getId() == confirmationCode.getUserId())
        {
            confirmationCodeRepository.delete(confirmationCode);
            if (Instant.now().compareTo(confirmationCode.getExpirationDate()) > 0) {
                generateForgottenPasswordCode(UserModel.of(userEntity));
                throw new ExpiredCodeException("This code has expired. A new one has been sent to " + userEntity.getEmail());
            }
            else{
                return UserIdResponse.builder().userId(userEntity.getId()).build();
            }
        }
        else
            throw new BadConfirmationCodeException("Bad confirmation code");
    }

    @Transactional
    public void updatePassword(long userId, UpdatePasswordRequest updatePasswordRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setPassword(encoder.encode(updatePasswordRequest.getPassword()));
        userMailUtils.sendUpdatePasswordMail(UserModel.of(user));
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

    public Optional<UserModel> findByEmail(final String email)
    {
        return userRepository.findByEmail(email)
                .map(UserModel::of);
    }
}
