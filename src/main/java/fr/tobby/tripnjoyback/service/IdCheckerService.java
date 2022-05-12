package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.UserModel;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class IdCheckerService {
    protected final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public IdCheckerService(UserRepository userRepository, final GroupRepository groupRepository)
    {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
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

    @NotNull
    public UserModel getCurrentUser() throws UserNotFoundException
    {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(name)
                             .map(UserModel::of)
                             .orElseThrow(UserNotFoundException::new);
    }

    public boolean isUserInGroup(long userId, long groupId)
    {
        return groupRepository.findById(groupId)
                              .map(group -> group.getMembers().stream()
                                                 .anyMatch(groupMember -> groupMember.getUser().getId().equals(userId)))
                              .orElseThrow(GroupNotFoundException::new);
    }
}
