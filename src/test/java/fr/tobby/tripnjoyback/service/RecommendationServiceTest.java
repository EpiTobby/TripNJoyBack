package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.CityEntity;
import fr.tobby.tripnjoyback.entity.GenderEntity;
import fr.tobby.tripnjoyback.entity.LanguageEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.model.RecommendationModel;
import fr.tobby.tripnjoyback.model.request.SubmitRecommendationRequest;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@DataJpaTest
public class RecommendationServiceTest {
    private static GenderEntity maleGender;
    private static GenderRepository genderRepository;
    private static CityRepository cityRepository;
    private static LanguageRepository languageRepository;
    private static CityEntity cityEntity;
    private static LanguageEntity languageEntity;
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private UserRepository userRepository;

    private RecommendationService recommendationService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository,
                          @Autowired ApplicationContext context,
                          @Autowired CityRepository cityRepository, @Autowired LanguageRepository languageRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        RecommendationServiceTest.genderRepository = genderRepository;
        RecommendationServiceTest.cityRepository = cityRepository;
        RecommendationServiceTest.languageRepository = languageRepository;
        cityEntity = cityRepository.save(new CityEntity("Paris"));
        languageEntity = languageRepository.save(new LanguageEntity("French"));
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp()
    {
        recommendationService = new RecommendationService(recommendationRepository, userRepository);
    }

    @NotNull
    private UserEntity anyUser() throws ParseException
    {
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email("test@1.com")
                .birthDate(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000").toInstant())
                .city(cityEntity)
                .confirmed(true)
                .language(languageEntity)
                .roles(List.of())
                .build());
    }

    @Test
    void submitRecommendation() throws ParseException{
        UserEntity submitter = anyUser();
        UserEntity goodUser = anyUser();
        RecommendationModel recommendationModel = recommendationService.submitRecommendation(submitter.getId(), SubmitRecommendationRequest.builder()
                .reviewedUserId(goodUser.getId())
                .comment("Il est très gentil")
                .build());
        Assertions.assertEquals(submitter.getId(), recommendationModel.getReviewer().userId());
        Assertions.assertEquals(goodUser.getId(), recommendationModel.getRecommendedUser().userId());
        Assertions.assertFalse(recommendationService.getByReviewedUserId(goodUser.getId()).isEmpty());
    }

    @Test
    void deleteRecommendation() throws ParseException{
        UserEntity submitter = anyUser();
        UserEntity goodUser = anyUser();
        long reportId = recommendationService.submitRecommendation(submitter.getId(), SubmitRecommendationRequest.builder()
                .reviewedUserId(goodUser.getId())
                .comment("Il est très gentil")
                .build()).getId();
        recommendationService.deleteRecommendation(reportId);
        Assertions.assertTrue(recommendationService.getByReviewedUserId(goodUser.getId()).isEmpty());
    }

    @Test
    void getByReviewedUser() throws ParseException{
        int numberOfReports = 10;
        UserEntity submitter = anyUser();
        UserEntity goodUser = anyUser();
        for (int i = 0; i < numberOfReports; i++) {
            recommendationService.submitRecommendation(submitter.getId(), SubmitRecommendationRequest.builder()
                    .reviewedUserId(goodUser.getId())
                    .comment("Il est très gentil")
                    .build()).getId();
        }
        Assertions.assertEquals(numberOfReports, recommendationService.getByReviewedUserId(goodUser.getId()).size());
    }
}