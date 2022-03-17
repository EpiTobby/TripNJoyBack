package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserModel;
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
