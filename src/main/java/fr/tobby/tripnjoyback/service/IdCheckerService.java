package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

public class IdCheckerService {
    protected final UserRepository userRepository;

    public IdCheckerService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void checkId(long userId)
    {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!userEntity.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            throw new ForbiddenOperationException("You cannot perform this operation");
    }

    public long getCurrentUserId()
    {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(name)
                             .map(UserEntity::getId)
                             .orElseThrow(UserNotFoundException::new);
    }
}
