package fr.tobby.tripnjoyback.notification;

import fr.tobby.tripnjoyback.entity.NotificationEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.response.NotificationModel;
import fr.tobby.tripnjoyback.repository.NotificationRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class SavedNotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final IdCheckerService idCheckerService;

    public SavedNotificationService(final NotificationRepository repository, final UserRepository userRepository,
                                    final IdCheckerService idCheckerService)
    {
        this.repository = repository;
        this.userRepository = userRepository;
        this.idCheckerService = idCheckerService;
    }

    @Transactional
    public NotificationModel save(long userId, String title, String body)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        NotificationEntity created = repository.save(new NotificationEntity(title, body, user));
        return NotificationModel.from(created);
    }

    public Collection<NotificationModel> getAllForUser(long userId)
    {
        return repository.getAllByUserId(userId)
                         .stream()
                         .map(NotificationModel::from)
                         .toList();
    }

    public Optional<NotificationModel> findById(long id)
    {
        return repository.findById(id).map(NotificationModel::from);
    }

    public void delete(long id)
    {
        NotificationModel notif = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No notification with id " + id));
        if (notif.getUserId() != idCheckerService.getCurrentUserId())
            throw new ForbiddenOperationException();
        repository.deleteById(id);
    }
}
