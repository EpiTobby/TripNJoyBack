package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.DeleteUserByAdminRequest;
import fr.tobby.tripnjoyback.model.request.DeleteUserRequest;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.LanguageRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
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
    private final ProfileService profileService;
    private final LanguageRepository languageRepository;
    private final CityService cityService;
    private final PasswordEncoder encoder;
    private final UserMailUtils userMailUtils;

    public UserService(UserRepository userRepository, GenderRepository genderRepository, ProfileService profileService,
                       LanguageRepository languageRepository, final CityService cityService, PasswordEncoder encoder, UserMailUtils userMailUtils) {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.profileService = profileService;
        this.languageRepository = languageRepository;
        this.cityService = cityService;
        this.encoder = encoder;
        this.userMailUtils = userMailUtils;
    }

    public Iterable<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public Optional<UserModel> findById(final long id) {
        return userRepository.findById(id).map(UserModel::of);
    }

    @Transactional
    public void updateUserInfo(long userId, UserUpdateRequest userUpdateRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (userUpdateRequest.getFirstname() != null)
            user.setFirstname(userUpdateRequest.getFirstname());
        if (userUpdateRequest.getLastname() != null)
            user.setLastname(userUpdateRequest.getLastname());
        if (userUpdateRequest.getProfilePicture() != null)
            user.setProfilePicture(userUpdateRequest.getProfilePicture());
        if (userUpdateRequest.getCity() != null) {
            CityEntity cityEntity = cityService.getOrAddCity(userUpdateRequest.getCity().getName());
            user.setCity(cityEntity);
        }
        if (userUpdateRequest.getPhoneNumber() != null)
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        if (userUpdateRequest.getBirthdate() != null) {
            user.setBirthDate(userUpdateRequest.getBirthdate().toInstant());
        }
        if (userUpdateRequest.getGender() != null) {
            user.setGender(genderRepository.findByValue(userUpdateRequest.getGender()).orElseThrow(() -> new UserCreationException("Invalid gender " + userUpdateRequest.getGender())));
        }
        if (userUpdateRequest.getLanguage() != null) {
            user.setLanguage(languageRepository.findByValue(userUpdateRequest.getLanguage().toUpperCase()).orElseThrow(() -> new UserCreationException("Invalid langage " + userUpdateRequest.getLanguage())));
        }
    }

    public Optional<UserModel> findByEmail(final String email) {
        return userRepository.findByEmail(email)
                .map(UserModel::of);
    }

    @Transactional
    public void deleteUserAccount(long userId, DeleteUserRequest deleteUserRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (!encoder.matches(deleteUserRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Bad Password");
        }
        profileService.deleteProfilesByUserId(userId);
        userRepository.delete(user);
        userMailUtils.sendDeleteAccountMail(UserModel.of(user));
    }

    @Transactional
    public void deleteUserByAdmin(long userId, DeleteUserByAdminRequest deleteUserByAdminRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        profileService.deleteProfilesByUserId(userId);
        userRepository.delete(user);
        userMailUtils.sendDeleteAccountByAdminMail(UserModel.of(user), deleteUserByAdminRequest.getReason());
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
