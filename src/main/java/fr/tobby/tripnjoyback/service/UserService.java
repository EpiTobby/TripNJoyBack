package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.DeleteUserByAdminRequest;
import fr.tobby.tripnjoyback.model.request.DeleteUserRequest;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    private final CityService cityService;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final PasswordEncoder encoder;
    private final UserMailUtils userMailUtils;

    public UserService(UserRepository userRepository, ProfileService profileService, final CityService cityService, ConfirmationCodeRepository confirmationCodeRepository, PasswordEncoder encoder, UserMailUtils userMailUtils)
    {
        this.userRepository = userRepository;
        this.profileService = profileService;
        this.cityService = cityService;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.encoder = encoder;
        this.userMailUtils = userMailUtils;
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
    public void updateUserInfo(long userId, UserUpdateRequest userUpdateRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (userUpdateRequest.getFirstname() != null)
            user.setFirstname(userUpdateRequest.getFirstname());
        if (userUpdateRequest.getLastname() != null)
            user.setLastname(userUpdateRequest.getLastname());
        if (userUpdateRequest.getProfilePicture() != null )
            user.setProfilePicture(userUpdateRequest.getProfilePicture());
        if (userUpdateRequest.getCity() != null) {
            CityEntity cityEntity = cityService.getOrAddCity(userUpdateRequest.getCity().getName());
            user.setCity(cityEntity);
        }
        if (userUpdateRequest.getPhoneNumber() != null )
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
    }

    public Optional<UserModel> findByEmail(final String email)
    {
        return userRepository.findByEmail(email)
                             .map(UserModel::of);
    }

    @Transactional
    public void deleteUserAccount(long userId, DeleteUserRequest deleteUserRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        if (!encoder.matches(deleteUserRequest.getPassword(),user.getPassword())) {
            throw new BadCredentialsException("Bad Password");
        }
        profileService.deleteProfilesByUserId(userId);
        userRepository.delete(user);
        userMailUtils.sendDeleteAccountMail(UserModel.of(user));
    }

    @Transactional
    public void deleteUserByAdmin(long userId, DeleteUserByAdminRequest deleteUserByAdminRequest){
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("No user with id " + userId));
        userRepository.delete(user);
        userMailUtils.sendDeleteAccountByAdminMail(UserModel.of(user), deleteUserByAdminRequest.getReason());
    }
}
