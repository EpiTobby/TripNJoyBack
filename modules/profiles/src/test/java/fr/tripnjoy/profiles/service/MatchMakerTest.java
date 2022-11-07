package fr.tripnjoy.profiles.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.groups.dto.response.GroupResponse;
import fr.tripnjoy.groups.model.State;
import fr.tripnjoy.profiles.entity.GroupProfileEntity;
import fr.tripnjoy.profiles.entity.ProfileEntity;
import fr.tripnjoy.profiles.model.MatchMakingUserModel;
import fr.tripnjoy.profiles.model.ProfileModel;
import fr.tripnjoy.profiles.model.answer.*;
import fr.tripnjoy.profiles.repository.GroupProfileRepository;
import fr.tripnjoy.profiles.repository.ProfileRepository;
import fr.tripnjoy.profiles.repository.UserMatchTaskRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.model.Gender;
import fr.tripnjoy.users.api.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@DataJpaTest
class MatchMakerTest {

    private MatchMaker matchMaker;

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserMatchTaskRepository userMatchTaskRepository;
    @Autowired
    private GroupProfileRepository groupProfileRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private ProfileService profileService;
    private UserFeignClient userFeignClient;
    private GroupFeignClient groupFeignClient;
    private RabbitTemplate rabbitTemplate;

    private long userIdCounter;

    private long anyUser()
    {
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    @BeforeEach
    void setUp()
    {
        profileService = mock(ProfileService.class);
        userFeignClient = mock(UserFeignClient.class);
        groupFeignClient = mock(GroupFeignClient.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        matchMaker = new MatchMaker(profileRepository,
                new MatchMakerScoreComputer(),
                profileService,
                userFeignClient,
                groupFeignClient,
                rabbitTemplate,
                userMatchTaskRepository,
                groupProfileRepository);
    }

    @AfterEach
    void tearDown()
    {
        profileRepository.deleteAll();
    }

    private ProfileModel.ProfileModelBuilder anyProfile() throws ParseException
    {
        return ProfileModel.builder()
                           .id(1)
                           .name("test")
                           .availabilities(List.of(new AvailabilityAnswerModel(
                                   dateFormat.parse("01-01-2022"),
                                   dateFormat.parse("05-01-2022")
                           )))
                           .aboutFood(AboutFoodAnswer.COOKING)
                           .ages(new RangeAnswerModel(18, 22))
                           .budget(new RangeAnswerModel(200, 400))
                           .chillOrVisit(ChillOrVisitAnswer.VISIT)
                           .destinationTypes(List.of(DestinationTypeAnswer.MOUNTAIN))
                           .duration(new RangeAnswerModel(2, 4))
                           .goOutAtNight(YesNoAnswer.NO_PREFERENCE)
                           .groupSize(new RangeAnswerModel(2, 4))
                           .sport(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonFromSameCity(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonFromSameCountry(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonSameLanguage(YesNoAnswer.NO_PREFERENCE)
                           .gender(GenderAnswer.MALE)
                           .isActive(true);
    }

    @Test
    void noGroupNoUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserResponse user = UserResponse.builder()
                                        .id(userIdCounter++)
                                        .gender(Gender.MALE)
                                        .city("Paris")
                                        .build();

        ProfileModel profile = anyProfile().build();

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel model = MatchMakingUserModel.from(user, now, profile);

        Assertions.assertFalse(matchMaker.isUserWaitingForGroup(user.getId()));

        matchMaker.match(model).get();

        Assertions.assertTrue(matchMaker.isUserWaitingForGroup(user.getId()));
    }

    @Test
    void testUserWaitingForGroups()
    {
        long user = anyUser();
        matchMaker.setUserAsWaiting(user, true);
        List<Long> ids = userMatchTaskRepository.findAllWaitingUserIds();
        Assertions.assertEquals(1, ids.size());
        Assertions.assertEquals(user, ids.get(0));
    }

    @Test
    void noGroupOneMatchingUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserResponse userA = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        UserResponse userB = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        matchMaker.setUserAsWaiting(userB.getId(), true);
        when(userFeignClient.getUserById(List.of("admin"), userA.getId())).thenReturn(userA);
        when(userFeignClient.getUserById(List.of("admin"), userB.getId())).thenReturn(userB);

        ProfileModel profileA = anyProfile().id(1).build();
        ProfileModel profileB = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileB);
        when(profileService.getActiveProfileModel(userB.getId())).thenReturn(Optional.of(profileB));

        when(groupFeignClient.createPublicGroup(any(), any())).thenReturn(new GroupResponse(1, null, null, State.OPEN, null, 0, null, null, null, null, null, null));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, now, profileA);

        matchMaker.match(modelA).get();

        verify(groupFeignClient).createPublicGroup(any(), any());
        Assertions.assertFalse(matchMaker.isUserWaitingForGroup(userB.getId()));
    }

    @Test
    void noGroupOneNonMatchingUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserResponse userA = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        UserResponse userB = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        when(userFeignClient.getUserById(List.of("admin"), userA.getId())).thenReturn(userA);
        when(userFeignClient.getUserById(List.of("admin"), userB.getId())).thenReturn(userB);
        matchMaker.setUserAsWaiting(userB.getId(), true);

        ProfileModel profileA = anyProfile().id(1)
                                            .ages(new RangeAnswerModel(30, 35)) // Should not match
                                            .build();
        ProfileModel profileB = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileB);
        when(profileService.getActiveProfileModel(userB.getId())).thenReturn(Optional.of(profileB));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, now, profileA);

        matchMaker.match(modelA).get();

        verify(groupFeignClient, times(0)).createPublicGroup(any(), any());
        Assertions.assertTrue(matchMaker.isUserWaitingForGroup(userA.getId()));
        Assertions.assertTrue(matchMaker.isUserWaitingForGroup(userB.getId()));
    }

    @Test
    void matchingGroupNoUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserResponse userA = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        UserResponse userB = UserResponse.builder()
                                         .id(anyUser())
                                         .city("")
                                         .gender(Gender.MALE)
                                         .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                         .build();
        when(userFeignClient.getUserById(List.of("admin"), userA.getId())).thenReturn(userA);
        when(userFeignClient.getUserById(List.of("admin"), userB.getId())).thenReturn(userB);

        ProfileModel profileA = anyProfile().id(1).build();
        ProfileModel profileGroup = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileGroup);

        ProfileEntity groupProfileEntity = profileRepository.save(new ProfileEntity(1L, "test", true, Instant.now()));
        groupProfileRepository.save(new GroupProfileEntity(new GroupProfileEntity.Ids(1L, groupProfileEntity)));
        when(profileService.getProfile(groupProfileEntity)).thenReturn(profileGroup);

        ProfileEntity profileEntity = mock(ProfileEntity.class);
        when(profileEntity.getId()).thenReturn(2L);

        when(groupFeignClient.getOpenGroups()).thenReturn(List.of(1L));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, now, profileA);

        matchMaker.match(modelA).get();

        Assertions.assertFalse(matchMaker.isUserWaitingForGroup(userA.getId()));
    }
}
