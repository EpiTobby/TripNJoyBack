package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@DataJpaTest
class MatchMakerTest {

    private static GenderEntity maleGender;
    private static GenderEntity femaleGender;
    private static GenderEntity otherGender;
    private static StateEntity closedState;
    private static StateEntity openState;
    private MatchMaker matchMaker;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private ProfileService profileService;
    private GroupService groupService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        femaleGender = genderRepository.save(new GenderEntity("female"));
        otherGender = genderRepository.save(new GenderEntity("other"));

        closedState = stateRepository.save(new StateEntity("CLOSED"));
        openState = stateRepository.save(new StateEntity("OPEN"));
    }

    @BeforeEach
    void setUp()
    {
        profileService = mock(ProfileService.class);
        groupService = mock(GroupService.class);
        matchMaker = new MatchMaker(profileRepository, userRepository, new MatchMakerScoreComputer(),
                groupService,
                groupRepository,
                profileService);
    }

    /**
     * Just to test the testing framework
     */
    @Test
    void addCityTest()
    {
        cityRepository.save(new CityEntity("Paris"));
        Optional<CityEntity> paris = cityRepository.findByName("Paris");

        Assertions.assertTrue(paris.isPresent());
    }

    @NotNull
    private UserEntity anyUser() throws ParseException
    {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        return userRepository.save(UserEntity.builder()
                                             .firstname("Test")
                                             .lastname("ament")
                                             .gender(maleGender)
                                             .email("test@osterone.com")
                                             .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                             .city(city)
                                             .build());
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
        UserEntity user = anyUser();

        ProfileModel profile = anyProfile().build();

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel model = MatchMakingUserModel.from(user, profile, now);

        Assertions.assertEquals(21, model.getAge());
        Assertions.assertFalse(user.isWaitingForGroup());

        matchMaker.match(model).get();

        Assertions.assertTrue(user.isWaitingForGroup());
    }

    @Test
    void noGroupOneMatchingUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserEntity userA = anyUser();
        UserEntity userB = anyUser();
        userB.setWaitingForGroup(true);

        ProfileModel profileA = anyProfile().id(1).build();
        ProfileModel profileB = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileB);
        when(profileService.getActiveProfileModel(userB.getId())).thenReturn(Optional.of(profileB));
        when(groupService.createPublicGroup(any(), any(), any(), any(), anyInt(), any())).thenReturn(mock(GroupModel.class));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, profileA, now);

        matchMaker.match(modelA).get();

        verify(groupService).createPublicGroup(any(), any(), any(), any(), anyInt(), any());
        Assertions.assertFalse(userB.isWaitingForGroup());
    }

    @Test
    void noGroupOneNonMatchingUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserEntity userA = anyUser();
        UserEntity userB = anyUser();
        userB.setWaitingForGroup(true);

        ProfileModel profileA = anyProfile().id(1)
                                            .ages(new RangeAnswerModel(30, 35)) // Should not match
                                            .build();
        ProfileModel profileB = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileB);
        when(profileService.getActiveProfileModel(userB.getId())).thenReturn(Optional.of(profileB));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, profileA, now);

        matchMaker.match(modelA).get();

        verify(groupService, times(0)).createPublicGroup(any(), any(), any(), any(), anyInt(), any());
        Assertions.assertTrue(userA.isWaitingForGroup());
        Assertions.assertTrue(userB.isWaitingForGroup());
    }

    @Test
    void matchingGroupNoUserTest() throws ParseException, ExecutionException, InterruptedException
    {
        UserEntity userA = anyUser();
        UserEntity userB = anyUser();

        ProfileModel profileA = anyProfile().id(1).build();
        ProfileModel profileGroup = anyProfile().id(2).build();

        when(profileService.getProfile(1)).thenReturn(profileA);
        when(profileService.getProfile(2)).thenReturn(profileGroup);

        ProfileEntity groupProfileEntity = profileRepository.save(new ProfileEntity(1L, "test", true));
        when(profileService.getProfile(groupProfileEntity)).thenReturn(profileGroup);

        ProfileEntity profileEntity = mock(ProfileEntity.class);
        when(profileEntity.getId()).thenReturn(2L);

        GroupEntity group = groupRepository.save(new GroupEntity(null,
                "test",
                openState,
                userB,
                3,
                new Date(),
                null,
                null,
                "",
                List.of(mock(GroupMemberEntity.class)),
                groupProfileEntity,
                List.of(mock(ChannelEntity.class))));

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel modelA = MatchMakingUserModel.from(userA, profileA, now);

        matchMaker.match(modelA).get();

        verify(groupService).addUserToPublicGroup(group.getId(), userA.getId(), 1);
        Assertions.assertFalse(userA.isWaitingForGroup());
    }
}
