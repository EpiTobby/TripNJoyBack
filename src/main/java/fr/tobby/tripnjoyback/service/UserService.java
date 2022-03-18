package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.CityModel;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.model.request.UpdateEmailRequest;
import fr.tobby.tripnjoyback.model.request.UserUpdateRequest;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CityService cityService;

    public UserService(UserRepository userRepository, final CityService cityService)
    {
        this.userRepository = userRepository;
        this.cityService = cityService;
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
}
