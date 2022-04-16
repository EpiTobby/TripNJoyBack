package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.repository.UserRepository;

public class IdCheckerService {
    protected final UserRepository userRepository;

    public IdCheckerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void checkId(long userId, String email) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!userEntity.getEmail().equals(email))
            throw new ForbiddenOperationException("You cannot perform this operation");
    }
}
