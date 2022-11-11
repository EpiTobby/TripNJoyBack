package fr.tripnjoy.profiles.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.profiles.dto.request.ProfileCreationRequest;
import fr.tripnjoy.profiles.entity.AnswersEntity;
import fr.tripnjoy.profiles.entity.AvailabiltyEntity;
import fr.tripnjoy.profiles.entity.ProfileEntity;
import fr.tripnjoy.profiles.entity.UserProfileEntity;
import fr.tripnjoy.profiles.exception.ProfileNotFoundException;
import fr.tripnjoy.profiles.model.answer.*;
import fr.tripnjoy.profiles.model.request.ProfileUpdateRequest;
import fr.tripnjoy.profiles.repository.AnswersRepository;
import fr.tripnjoy.profiles.repository.GroupProfileRepository;
import fr.tripnjoy.profiles.repository.ProfileRepository;
import fr.tripnjoy.profiles.repository.UserProfileRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@DataJpaTest
class ProfileServiceTest {
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private GroupProfileRepository groupProfileRepository;
    private AnswersRepository profileAnswersRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private UserFeignClient userFeignClient;

    @BeforeEach
    void setup()
    {
        profileAnswersRepository = mock(AnswersRepository.class);
        userFeignClient = mock(UserFeignClient.class);
        when(userFeignClient.exists(anyLong())).thenReturn(new BooleanResponse(false));
        profileService = new ProfileService(profileRepository, profileAnswersRepository, userProfileRepository, groupProfileRepository, userFeignClient);
    }

    private ProfileEntity anyProfile()
    {
        return profileRepository.save(ProfileEntity.builder()
                                                   .active(true)
                                                   .name("profile1")
                                                   .createdDate(Instant.now())
                                                   .build());
    }

    private long userIdCounter = 1;

    private long anyUser()
    {
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    private ProfileEntity anyProfile(long userId)
    {
        ProfileEntity profileEntity = anyProfile();
        userProfileRepository.save(new UserProfileEntity(userId, profileEntity));
        return profileEntity;
    }

    @Test
    void testUpdateProfileWithNullValues()
    {
        ProfileUpdateRequest request = ProfileUpdateRequest.builder().build();
        long user = anyUser();
        ProfileEntity profile = anyProfile(user);
        profileService.updateProfile(user, profile.getId(), request);
        ProfileEntity profileEntity = profileRepository.findById(profile.getId()).get();
        Assertions.assertEquals("profile1", profileEntity.getName());
    }

    @Test
    void testUpdateActiveWhenNewProfileAdded() throws ParseException
    {
        long user = anyUser();
        ProfileEntity profile = anyProfile(user);
        when(profileAnswersRepository.save(any())).thenReturn(AnswersEntity
                .builder()
                .id("2")
                .availabilities(List.of(new AvailabiltyEntity("01-07-2023", "16-07-2023")))
                .durationMin(4)
                .durationMax(7)
                .groupSizeMin(2)
                .groupSizeMax(5)
                .budgetMin(1000)
                .budgetMax(2000)
                .destinationTypes(List.of("CITY", "BEACH"))
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
        profileService.createUserProfile(user, ProfileCreationRequest.builder()
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
        List<ProfileEntity> profileEntities = profileRepository.findByUserId(user);
        Assertions.assertFalse(profileEntities.get(0).isActive());
        Assertions.assertTrue(profileEntities.get(1).isActive());
    }

    @Test
    void testDeleteProfile() {
        long user = anyUser();
        anyProfile(user);
        AnswersEntity entity = AnswersEntity.builder()
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
        ProfileEntity profile = profileRepository.findByUserId(user).get(0);
        profile.setActive(false);

        profileService.deleteProfile(user, profile.getId());

        Assertions.assertThrows(ProfileNotFoundException.class, () -> profileService.getProfile(profile.getId()));
    }

    @Test
    void testDeleteActiveProfileShouldThrow() {
        long user = anyUser();
        anyProfile(user);
        AnswersEntity entity = AnswersEntity.builder()
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
        ProfileEntity profile = profileRepository.findByUserId(user).get(0);
        profile.setActive(true);

        long profileId = profile.getId();
        Assertions.assertThrows(IllegalArgumentException.class, () -> profileService.deleteProfile(user, profileId));
    }
}
