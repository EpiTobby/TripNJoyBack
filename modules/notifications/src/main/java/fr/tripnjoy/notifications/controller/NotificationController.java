package fr.tripnjoy.notifications.controller;

import fr.tripnjoy.common.exception.EntityNotFoundException;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.notifications.dto.response.NotificationModel;
import fr.tripnjoy.notifications.service.SavedNotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final SavedNotificationService service;

    public NotificationController(final SavedNotificationService service)
    {
        this.service = service;
    }

    @GetMapping("/")
    public Collection<NotificationModel> getForUser(@RequestHeader("userId") long userId)
    {
        return service.getAllForUser(userId);
    }

    @DeleteMapping("/{id}")
    public void discard(@PathVariable("id") long notificationId, @RequestHeader("userId") long userId)
    {
        service.delete(notificationId, userId);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String getError(EntityNotFoundException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String getError(ForbiddenOperationException exception) {
        return exception.getMessage();
    }
}
