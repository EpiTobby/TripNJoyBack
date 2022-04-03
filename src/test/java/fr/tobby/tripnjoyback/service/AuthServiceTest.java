package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.auth.TokenManager;
import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.UserCreationException;
import fr.tobby.tripnjoyback.mail.UserMailUtils;
import fr.tobby.tripnjoyback.repository.ConfirmationCodeRepository;
import fr.tobby.tripnjoyback.repository.GenderRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.UserRoleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AuthServiceTest {

    UserRepository userRepository;
    UserMailUtils userMailUtils;
    PasswordEncoder encoder;
    GenderRepository genderRepository;
    ConfirmationCodeRepository confirmationCodeRepository;
    AuthenticationManager authenticationManager;
    TokenManager tokenManager;
    UserDetailsService userDetailsService;
    UserService userService;
    UserRoleRepository userRoleRepository;

    AuthService service;

    @BeforeEach
    public void setup()
    {
        userRepository = mock(UserRepository.class);
        userMailUtils = mock(UserMailUtils.class);
        encoder = mock(PasswordEncoder.class);
        genderRepository = mock(GenderRepository.class);
        confirmationCodeRepository = mock(ConfirmationCodeRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        tokenManager = mock(TokenManager.class);
        userDetailsService = mock(UserDetailsService.class);
        userService = mock(UserService.class);
        userRoleRepository = mock(UserRoleRepository.class);

        service = new AuthService(userRepository,
                userMailUtils,
                encoder,
                genderRepository,
                confirmationCodeRepository,
                authenticationManager,
                tokenManager,
                userDetailsService,
                userService,
                userRoleRepository);
    }

    @Test
    public void createUserTest()
    {
        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(true);
        UserEntity userEntity = new UserEntity("firstname",
                "lastname",
                "pswd",
                "test@mail.co",
                Instant.now(),
                new GenderEntity("male"),
                "",
                new CityEntity(""),
                Instant.now(),
                "0100000000");

        service.createUser(userEntity);

        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    public void createUserInvalidEmailTest()
    {
        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(false);
        UserEntity userEntity = new UserEntity("firstname",
                "lastname",
                "pswd",
                "test@mail.co",
                Instant.now(),
                new GenderEntity("male"),
                "",
                new CityEntity(""),
                Instant.now(),
                "0100000000");

        Assertions.assertThrows(UserCreationException.class, () -> service.createUser(userEntity));

        verify(userRepository, times(0)).save(userEntity);
    }

    @Test
    public void createUserAlreadyExistsTest()
    {
        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(UserEntity.class)));
        UserEntity userEntity = new UserEntity("firstname",
                "lastname",
                "pswd",
                "test@mail.co",
                Instant.now(),
                new GenderEntity("male"),
                "",
                new CityEntity(""),
                Instant.now(),
                "0100000000");

        Assertions.assertThrows(UserCreationException.class, () -> service.createUser(userEntity));

        verify(userRepository, times(0)).save(userEntity);
    }
}