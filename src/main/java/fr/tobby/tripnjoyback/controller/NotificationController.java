package fr.tobby.tripnjoyback.controller;

import fr.tobby.tripnjoyback.exception.EntityNotFoundException;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.model.response.NotificationModel;
import fr.tobby.tripnjoyback.notification.SavedNotificationService;
import fr.tobby.tripnjoyback.service.IdCheckerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final SavedNotificationService service;
    private final IdCheckerService idCheckerService;

    public NotificationController(final SavedNotificationService service, final IdCheckerService idCheckerService)
    {
        this.service = service;
        this.idCheckerService = idCheckerService;
    }

    @GetMapping("/")
    public Collection<NotificationModel> getForUser()
    {
        return service.getAllForUser(idCheckerService.getCurrentUserId());
    }

    @DeleteMapping("/{id}")
    public void discard(@PathVariable("id") long notificationId)
    {
        service.delete(notificationId);
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
