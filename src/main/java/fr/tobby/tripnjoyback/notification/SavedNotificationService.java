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
import java.util.Map;
import java.util.Optional;

@Service
public class SavedNotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final IdCheckerService idCheckerService;
    private final INotificationService notificationService;

    public SavedNotificationService(final NotificationRepository repository, final UserRepository userRepository,
                                    final IdCheckerService idCheckerService,
                                    final INotificationService notificationService)
    {
        this.repository = repository;
        this.userRepository = userRepository;
        this.idCheckerService = idCheckerService;
        this.notificationService = notificationService;
    }

    @Transactional
    public NotificationModel send(long userId, String title, String body, Map<String, String> data)
    {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        String id = notificationService.sendToToken(user.getFirebaseToken(), title, body, data);
        NotificationEntity created = repository.save(new NotificationEntity(title, body, user, id));
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
