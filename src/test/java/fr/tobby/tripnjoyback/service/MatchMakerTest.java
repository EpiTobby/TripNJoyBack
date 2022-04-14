package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.model.MatchMakingUserModel;
import fr.tobby.tripnjoyback.model.ProfileModel;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class MatchMakerTest {

    private static GenderEntity maleGender;
    private static GenderEntity femaleGender;
    private static GenderEntity otherGender;
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

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        femaleGender = genderRepository.save(new GenderEntity("female"));
        otherGender = genderRepository.save(new GenderEntity("other"));
    }

    @BeforeEach
    void setUp()
    {
        matchMaker = new MatchMaker(profileRepository, userRepository, Mockito.mock(AnswersRepository.class), new MatchMakerScoreComputer(),
                Mockito.mock(GroupService.class),
                groupRepository,
                Mockito.mock(ProfileService.class));
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
                           .groupeSize(new RangeAnswerModel(2, 4))
                           .sport(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonFromSameCity(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonFromSameCountry(YesNoAnswer.NO_PREFERENCE)
                           .travelWithPersonSameLanguage(YesNoAnswer.NO_PREFERENCE)
                           .gender(GenderAnswer.MALE)
                           .isActive(true);
    }

    @Test
    void noGroupNoUserTest() throws ParseException
    {
        UserEntity user = anyUser();

        ProfileModel profile = anyProfile().build();

        Instant now = dateFormat.parse("01-01-2021").toInstant();
        MatchMakingUserModel model = MatchMakingUserModel.from(user, profile, now);

        Assertions.assertEquals(21, model.getAge());
        Assertions.assertFalse(user.isWaitingForGroup());

        matchMaker.match(model);

        Assertions.assertTrue(user.isWaitingForGroup());
    }

    @Test
    void noGroupOneUserTest() throws ParseException
    {
        UserEntity userA = anyUser();
        UserEntity userB = anyUser();


    }
}
