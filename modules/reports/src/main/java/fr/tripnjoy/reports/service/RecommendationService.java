package fr.tripnjoy.reports.service;

import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.profiles.exception.RecommendationNotFoundException;
import fr.tripnjoy.reports.entity.RecommendationEntity;
import fr.tripnjoy.reports.model.RecommendationModel;
import fr.tripnjoy.reports.repository.RecommendationRepository;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserFeignClient userFeignClient;

    public RecommendationService(RecommendationRepository recommendationRepository, final UserFeignClient userFeignClient)
    {
        this.recommendationRepository = recommendationRepository;
        this.userFeignClient = userFeignClient;
    }

    @Transactional
    public RecommendationModel submitRecommendation(long submitterId, long userIdToReview, String review)
    {
        if (!userFeignClient.exists(userIdToReview).value())
            throw new UserNotFoundException("No user found with id:" + userIdToReview);

        if (submitterId == userIdToReview)
            throw new ForbiddenOperationException("You cannot recommend yourself");
        RecommendationEntity recommendationEntity = recommendationRepository.save(new RecommendationEntity(submitterId, userIdToReview, review));
        return RecommendationModel.of(recommendationEntity);
    }

    public List<RecommendationModel> getByReviewedUserId(long reviewedUserId)
    {
        return recommendationRepository.findByRecommendedUser(reviewedUserId).stream().map(RecommendationModel::of).toList();
    }

    @Transactional
    public void deleteRecommendation(long recommendationId)
    {
        RecommendationEntity recommendationEntity = recommendationRepository.findById(recommendationId).orElseThrow(() -> new RecommendationNotFoundException("No recommendation found with id: " + recommendationId));
        recommendationRepository.delete(recommendationEntity);
    }
}
