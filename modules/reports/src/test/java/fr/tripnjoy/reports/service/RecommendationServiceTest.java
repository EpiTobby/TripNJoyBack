package fr.tripnjoy.reports.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.reports.model.RecommendationModel;
import fr.tripnjoy.reports.repository.RecommendationRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.mockito.Mockito.*;

@DataJpaTest
class RecommendationServiceTest {
    @Autowired
    private RecommendationRepository recommendationRepository;

    private RecommendationService recommendationService;
    private UserFeignClient userFeignClient;

    @BeforeEach
    void setUp()
    {
        userFeignClient = mock(UserFeignClient.class);
        when(userFeignClient.exists(anyLong())).thenReturn(new BooleanResponse(false));
        recommendationService = new RecommendationService(recommendationRepository, userFeignClient);
    }

    @AfterEach
    void tearDown()
    {
        recommendationRepository.deleteAll();
    }

    private long userIdCounter = 1;

    private long anyUser()
    {
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    @Test
    void submitRecommendation()
    {
        long submitter = anyUser();
        long goodUser = anyUser();
        RecommendationModel recommendationModel = recommendationService.submitRecommendation(submitter, goodUser, "Il est très gentil");
        Assertions.assertEquals(submitter, recommendationModel.getReviewer());
        Assertions.assertEquals(goodUser, recommendationModel.getRecommendedUser());
        Assertions.assertFalse(recommendationService.getByReviewedUserId(goodUser).isEmpty());
    }

    @Test
    void deleteRecommendation()
    {
        long submitter = anyUser();
        long goodUser = anyUser();
        long reportId = recommendationService.submitRecommendation(submitter, goodUser, "Il est très gentil")
                                             .getId();
        recommendationService.deleteRecommendation(reportId);
        Assertions.assertTrue(recommendationService.getByReviewedUserId(goodUser).isEmpty());
    }

    @Test
    void getByReviewedUser()
    {
        int numberOfReports = 10;
        long submitter = anyUser();
        long goodUser = anyUser();
        for (int i = 0; i < numberOfReports; i++)
            recommendationService.submitRecommendation(submitter, goodUser, "Il est très gentil");
        Assertions.assertEquals(numberOfReports, recommendationService.getByReviewedUserId(goodUser).size());
    }
}
