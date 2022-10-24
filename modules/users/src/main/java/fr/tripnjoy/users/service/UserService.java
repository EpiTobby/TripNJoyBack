package fr.tripnjoy.users.service;

import fr.tripnjoy.users.api.exception.UserNotFoundException;
import fr.tripnjoy.users.entity.CityEntity;
import fr.tripnjoy.users.entity.UserEntity;
import fr.tripnjoy.users.exception.UserCreationException;
import fr.tripnjoy.users.model.UserModel;
import fr.tripnjoy.users.model.request.DeleteUserByAdminRequest;
import fr.tripnjoy.users.model.request.DeleteUserRequest;
import fr.tripnjoy.users.model.request.UserUpdateRequest;
import fr.tripnjoy.users.repository.GenderRepository;
import fr.tripnjoy.users.repository.LanguageRepository;
import fr.tripnjoy.users.repository.UserRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final LanguageRepository languageRepository;
    private final CityService cityService;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, GenderRepository genderRepository,
                       LanguageRepository languageRepository, final CityService cityService, PasswordEncoder encoder)
    {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.languageRepository = languageRepository;
        this.cityService = cityService;
        this.encoder = encoder;
    }

    public Iterable<UserEntity> getAll()
    {
        return userRepository.findAll();
    }

    public Optional<UserModel> findById(final long id)
    {
        return userRepository.findById(id).map(UserModel::of);
    }

    @Transactional
    public void updateUserInfo(long userId, UserUpdateRequest userUpdateRequest)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (userUpdateRequest.getFirstname() != null)
            user.setFirstname(userUpdateRequest.getFirstname());
        if (userUpdateRequest.getLastname() != null)
            user.setLastname(userUpdateRequest.getLastname());
        if (userUpdateRequest.getProfilePicture() != null)
            user.setProfilePicture(userUpdateRequest.getProfilePicture());
        if (userUpdateRequest.getCity() != null)
        {
            CityEntity cityEntity = cityService.getOrAddCity(userUpdateRequest.getCity().getName());
            user.setCity(cityEntity);
        }
        if (userUpdateRequest.getPhoneNumber() != null)
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        if (userUpdateRequest.getBirthdate() != null)
        {
            user.setBirthDate(userUpdateRequest.getBirthdate().toInstant());
        }
        if (userUpdateRequest.getGender() != null)
        {
            user.setGender(genderRepository.findByValue(userUpdateRequest.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + userUpdateRequest.getGender())));
        }
        if (userUpdateRequest.getLanguage() != null)
        {
            user.setLanguage(languageRepository.findByValue(userUpdateRequest.getLanguage().toUpperCase()).orElseThrow(() -> new UserCreationException("Invalid langage " + userUpdateRequest.getLanguage())));
        }
    }

    public Optional<UserModel> findByEmail(final String email)
    {
        return userRepository.findByEmail(email)
                             .map(UserModel::of);
    }

    @Transactional
    public void deleteUserAccount(long userId, DeleteUserRequest deleteUserRequest)
    {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (!encoder.matches(deleteUserRequest.getPassword(), userEntity.getPassword()))
        {
            throw new BadCredentialsException("Bad Password");
        }
        // FIXME: delete profile via profile service
        //        profileService.deleteProfilesByUserId(userId);
        deleteUserEntity(userEntity);
        // FIXME: send mail via mail service
        //        userMailUtils.sendDeleteAccountMail(UserModel.of(userEntity));
    }

    @Transactional
    public void deleteUserByAdmin(long userId, DeleteUserByAdminRequest deleteUserByAdminRequest)
    {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        // FIXME: delete profile via profile service
        //        profileService.deleteProfilesByUserId(userId);
        deleteUserEntity(userEntity);
        // FIXME: send mail via mail service
        //        userMailUtils.sendDeleteAccountByAdminMail(UserModel.of(userEntity), deleteUserByAdminRequest.getReason());
    }

    @Transactional
    public void deleteUserEntity(UserEntity userEntity)
    {
        userRepository.delete(userEntity);
        // FIXME: prom
        //        promStats.getUserCount().set(userRepository.count());
    }

    @Nullable
    public String getFirebaseToken(long userId)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        return user.getFirebaseToken();
    }

    @Transactional
    public void setFirebaseToken(long userId, @Nullable String token)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        user.setFirebaseToken(token);
    }
}
