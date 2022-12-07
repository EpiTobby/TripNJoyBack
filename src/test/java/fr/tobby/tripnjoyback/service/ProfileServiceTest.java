package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.PromStats;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.ProfileNotFoundException;
import fr.tobby.tripnjoyback.model.request.ProfileCreationRequest;
import fr.tobby.tripnjoyback.model.request.ProfileUpdateRequest;
import fr.tobby.tripnjoyback.model.request.anwsers.*;
import fr.tobby.tripnjoyback.repository.*;
import io.prometheus.client.Gauge;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@DataJpaTest
class ProfileServiceTest {
    private static GenderEntity maleGender;
    private static GenderEntity femaleGender;
    private static GenderEntity otherGender;
    private ProfileService profileService;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ProfileRepository profileRepository;
    private ProfileAnswersRepository profileAnswersRepository = mock(ProfileAnswersRepository.class);
    @Autowired
    private UserRepository userRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository) {
        maleGender = genderRepository.save(new GenderEntity("male"));
        femaleGender = genderRepository.save(new GenderEntity("female"));
        otherGender = genderRepository.save(new GenderEntity("other"));
    }

    @BeforeEach
    void initProfileService(){
        PromStats promStats = mock(PromStats.class);
        when(promStats.getProfileCount()).thenReturn(mock(Gauge.class));
        profileService = new ProfileService(profileRepository, profileAnswersRepository, userRepository, promStats);
    }

    private ProfileEntity anyProfile(){
        ProfileEntity profileEntity =  profileRepository.save(ProfileEntity.builder()
                .active(true)
                .name("profile1")
                .createdDate(Instant.now())
                .build());
        return profileEntity;
    }

    @NotNull
    private UserEntity anyUserWithProfile() throws ParseException {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        LanguageEntity language = languageRepository.save(new LanguageEntity("French"));
        UserEntity userEntity = userRepository.save(UserEntity.builder()
                                             .firstname("Test")
                                             .lastname("1")
                                             .gender(maleGender)
                                             .email("test@1.com")
                                             .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                             .city(city)
                                             .confirmed(true)
                                             .language(language)
                                             .roles(List.of())
                                             .profiles(new ArrayList<>())
                                             .build());
        userEntity.getProfiles().add(anyProfile());
        return userEntity;
    }

    @Test
    void testUpdateProfileWithNullValues() throws ParseException {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder().build();
        UserEntity user = anyUserWithProfile();
        long profileId = user.getProfiles().iterator().next().getId();
        profileService.updateProfile(user.getId(), profileId, request);
        ProfileEntity profileEntity = profileRepository.findById(profileId).get();
        Assertions.assertEquals("profile1", profileEntity.getName());
    }

    @Test
    void testUpdateActiveWhenNewProfileAdded() throws ParseException {
        UserEntity user = anyUserWithProfile();
        when(profileAnswersRepository.save(any())).thenReturn(ProfileAnswersEntity.builder()
                .id("2")
                .availabilities(List.of(new AvailabiltyEntity("01-07-2023", "16-07-2023")))
                .durationMin(4)
                .durationMax(7)
                .groupSizeMin(2)
                .groupSizeMax(5)
                .budgetMin(1000)
                .budgetMax(2000)
                .destinationTypes(List.of("CITY","BEACH"))
                .ageMin(25)
                .ageMax(40)
                .travelWithPersonFromSameCity(true)
                .travelWithPersonFromSameCountry(true)
                .travelWithPersonSameLanguage(true)
                .gender("MALE")
                .aboutFood("RESTAURANT")
                .goOutAtNight(true)
                .chillOrVisit("CHILL")
                .sport(true)
                .build());
        profileService.createUserProfile(user.getId(), ProfileCreationRequest.builder()
                .name("profile2")
                .availabilities(List.of(new AvailabilityAnswerModel(dateFormat.parse("01-07-2023"), dateFormat.parse("07-07-2023"))))
                .duration(new RangeAnswerModel(2, 5))
                .budget(new RangeAnswerModel(1000, 2000))
                .destinationTypes(List.of(DestinationTypeAnswer.CITY, DestinationTypeAnswer.BEACH))
                .ages(new RangeAnswerModel(25, 40))
                .travelWithPersonFromSameCity(YesNoAnswer.YES)
                .travelWithPersonFromSameCountry(YesNoAnswer.YES)
                .travelWithPersonSameLanguage(YesNoAnswer.YES)
                .gender(GenderAnswer.MALE)
                .groupSize(new RangeAnswerModel(2, 5))
                .aboutFood(AboutFoodAnswer.RESTAURANT)
                .chillOrVisit(ChillOrVisitAnswer.CHILL)
                .goOutAtNight(YesNoAnswer.YES)
                .sport(YesNoAnswer.YES)
                .build());
        List<ProfileEntity> profileEntities = List.copyOf(user.getProfiles());
        Assertions.assertFalse(profileEntities.get(0).isActive());
        Assertions.assertTrue(profileEntities.get(1).isActive());
    }

    @Test
    void testDeleteProfile() throws ParseException {
        UserEntity user = anyUserWithProfile();
        ProfileAnswersEntity entity = ProfileAnswersEntity.builder()
                .id("2")
                .availabilities(List.of(new AvailabiltyEntity("01-07-2023", "16-07-2023")))
                .durationMin(4)
                .durationMax(7)
                .groupSizeMin(2)
                .groupSizeMax(5)
                .budgetMin(1000)
                .budgetMax(2000)
                .destinationTypes(List.of("CITY","BEACH"))
                .ageMin(25)
                .ageMax(40)
                .travelWithPersonFromSameCity(true)
                .travelWithPersonFromSameCountry(true)
                .travelWithPersonSameLanguage(true)
                .gender("MALE")
                .aboutFood("RESTAURANT")
                .goOutAtNight(true)
                .chillOrVisit("CHILL")
                .sport(true)
                .build();
        when(profileAnswersRepository.save(any())).thenReturn(entity);
        when(profileAnswersRepository.findByProfileId(anyLong())).thenReturn(entity);
        ProfileEntity profile = user.getProfiles().stream().findFirst().get();
        profile.setActive(false);

        profileService.deleteProfile(user.getId(), profile.getId());

        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(profile.getId()));
    }

    @Test
    void testDeleteActiveProfileShouldThrow() throws ParseException {
        UserEntity user = anyUserWithProfile();
        ProfileAnswersEntity entity = ProfileAnswersEntity.builder()
                .id("2")
                .availabilities(List.of(new AvailabiltyEntity("01-07-2023", "16-07-2023")))
                .durationMin(4)
                .durationMax(7)
                .groupSizeMin(2)
                .groupSizeMax(5)
                .budgetMin(1000)
                .budgetMax(2000)
                .destinationTypes(List.of("CITY","BEACH"))
                .ageMin(25)
                .ageMax(40)
                .travelWithPersonFromSameCity(true)
                .travelWithPersonFromSameCountry(true)
                .travelWithPersonSameLanguage(true)
                .gender("MALE")
                .aboutFood("RESTAURANT")
                .goOutAtNight(true)
                .chillOrVisit("CHILL")
                .sport(true)
                .build();
        when(profileAnswersRepository.save(any())).thenReturn(entity);
        when(profileAnswersRepository.findByProfileId(anyLong())).thenReturn(entity);
        ProfileEntity profile = user.getProfiles().stream().findFirst().get();
        profile.setActive(true);

        long userId = user.getId();
        long profileId = profile.getId();
        Assertions.assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(userId, profileId));
    }
}
