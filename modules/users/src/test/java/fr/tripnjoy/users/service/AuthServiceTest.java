package fr.tripnjoy.users.service;

import fr.tripnjoy.users.auth.TokenManager;
import fr.tripnjoy.users.entity.CityEntity;
import fr.tripnjoy.users.entity.GenderEntity;
import fr.tripnjoy.users.entity.LanguageEntity;
import fr.tripnjoy.users.entity.UserEntity;
import fr.tripnjoy.users.exception.UserCreationException;
import fr.tripnjoy.users.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;

class AuthServiceTest {

    UserRepository userRepository;
    PasswordEncoder encoder;
    GenderRepository genderRepository;
    CityService cityService;
    ConfirmationCodeRepository confirmationCodeRepository;
    AuthenticationManager authenticationManager;
    TokenManager tokenManager;
    UserService userService;
    UserRoleRepository userRoleRepository;
    LanguageRepository languageRepository;

    AuthService service;
    private AuthenticationManager authenticationManager1;

    @BeforeEach
    public void setup()
    {
        userRepository = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        genderRepository = mock(GenderRepository.class);
        confirmationCodeRepository = mock(ConfirmationCodeRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        tokenManager = mock(TokenManager.class);
        userService = mock(UserService.class);
        userRoleRepository = mock(UserRoleRepository.class);
        languageRepository = mock(LanguageRepository.class);
        cityService = mock(CityService.class);

//        PromStats promStats = mock(PromStats.class);
//        when(promStats.getUserCount()).thenReturn(mock(Gauge.class));

        service = new AuthService(userRepository,
                encoder,
                genderRepository,
                cityService,
                confirmationCodeRepository,
                userRoleRepository,
                languageRepository,
                userService,
                tokenManager,
                authenticationManager);
    }

    @Test
    void createUserTest()
    {
//        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(true);
        UserEntity userEntity = new UserEntity("firstname",
                "lastname",
                "pswd",
                "test@mail.co",
                Instant.now(),
                new GenderEntity("male"),
                "",
                new CityEntity(""),
                Instant.now(),
                "0100000000",
                new LanguageEntity("French"));

        service.createUser(userEntity);

        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void createUserInvalidEmailTest()
    {
//        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(false);
        UserEntity userEntity = new UserEntity("firstname",
                "lastname",
                "pswd",
                "test@mail.co",
                Instant.now(),
                new GenderEntity("male"),
                "",
                new CityEntity(""),
                Instant.now(),
                "0100000000",
                new LanguageEntity("French"));

        Assertions.assertThrows(UserCreationException.class, () -> service.createUser(userEntity));

        verify(userRepository, times(0)).save(userEntity);
    }

    @Test
    void createUserAlreadyExistsTest()
    {
//        when(userMailUtils.userEmailIsValid(anyString())).thenReturn(true);
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
                "0100000000",
                new LanguageEntity("French"));

        Assertions.assertThrows(UserCreationException.class, () -> service.createUser(userEntity));

        verify(userRepository, times(0)).save(userEntity);
    }
}
