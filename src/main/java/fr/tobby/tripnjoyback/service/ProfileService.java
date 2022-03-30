package fr.tobby.tripnjoyback.service;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }
}
