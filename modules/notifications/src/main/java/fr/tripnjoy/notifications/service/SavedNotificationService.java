package fr.tripnjoy.notifications.service;

import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.notifications.dto.response.NotificationModel;
import fr.tripnjoy.notifications.entity.NotificationEntity;
import fr.tripnjoy.notifications.repository.NotificationRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.response.FirebaseTokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Service
public class SavedNotificationService {
    private final NotificationRepository repository;
    private final INotificationService notificationService;
    private final UserFeignClient userFeignClient;
    private final GroupFeignClient groupFeignClient;

    public SavedNotificationService(final NotificationRepository repository, final INotificationService notificationService,
                                    final UserFeignClient userFeignClient, final GroupFeignClient groupFeignClient)
    {
        this.repository = repository;
        this.notificationService = notificationService;
        this.userFeignClient = userFeignClient;
        this.groupFeignClient = groupFeignClient;
    }

    /**
     * Send a notification to a user, and save the notification in database
     *
     * @return the saved notification
     */
    @Transactional
    public NotificationModel sendToUser(long userId, String title, String body, Map<String, String> data)
    {
        FirebaseTokenResponse firebaseToken = userFeignClient.getFirebaseToken(userId);
        String id = notificationService.sendToToken(firebaseToken.getToken(), title, body, data);
        NotificationEntity created = repository.save(new NotificationEntity(title, body, userId, id));
        return NotificationModel.from(created);
    }

    /**
     * Send a notification to a group. Save a notification instance for each group member
     *
     * @return Id of the notification
     */
    @Transactional
    public String sendToGroup(long groupId, String title, String body, Map<String, String> data)
    {
        String id = notificationService.sendToGroup(groupId, title, body, data);

        groupFeignClient.getMembers(groupId)
                        .forEach(user -> repository.save(new NotificationEntity(title, body, user, id)));

        return id;
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

    public void delete(long id, long userId)
    {
        NotificationModel notif = findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No notification with id " + id));
        if (notif.getUserId() != userId)
            throw new ForbiddenOperationException();
        repository.deleteById(id);
    }
}
