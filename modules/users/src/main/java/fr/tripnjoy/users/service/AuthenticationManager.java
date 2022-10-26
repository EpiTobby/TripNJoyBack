package fr.tripnjoy.users.service;

import fr.tripnjoy.users.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationManager {

    private final UserRepository userRepository;

    public AuthenticationManager(final UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.findByEmail(username)
                             .map(user -> encoder.matches(password, user.getPassword()))
                             .orElse(false);
    }
}
