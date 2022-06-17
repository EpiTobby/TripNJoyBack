package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.RecommendationEntity;
import fr.tobby.tripnjoyback.entity.RecommendationEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.RecommendationNotFoundException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.RecommendationModel;
import fr.tobby.tripnjoyback.model.RecommendationModel;
import fr.tobby.tripnjoyback.model.request.SubmitRecommendationRequest;
import fr.tobby.tripnjoyback.repository.RecommendationRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    public RecommendationService(RecommendationRepository recommendationRepository, UserRepository userRepository) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RecommendationModel submitRecommendation(String submitterEmail, SubmitRecommendationRequest submitRecommendationRequest){
        UserEntity submitter = userRepository.findByEmail(submitterEmail).orElseThrow(() -> new UserNotFoundException("No user found with email:" + submitterEmail));
        UserEntity recommendedUser =  userRepository.findById(submitRecommendationRequest.getReviewedUserId()).orElseThrow(() -> new UserNotFoundException("No user found with id:" + submitRecommendationRequest.getReviewedUserId()));
        if (recommendedUser.getId().equals(submitter.getId()))
            throw new ForbiddenOperationException("You cannot recommand yourself");
        RecommendationEntity recommendationEntity = recommendationRepository.save(new RecommendationEntity(submitter, recommendedUser, submitRecommendationRequest.getComment()));
        return RecommendationModel.of(recommendationEntity);
    }

    public List<RecommendationModel> getByReviewedUserId(long reviewedUserId){
        return recommendationRepository.findByRecommendedUserId(reviewedUserId).stream().map(RecommendationModel::of).toList();
    }

    @Transactional
    public void deleteRecommendation(long recommendationId){
        RecommendationEntity recommendationEntity = recommendationRepository.findById(recommendationId).orElseThrow(() -> new RecommendationNotFoundException("No recommendation found with id: " + recommendationId));
        recommendationRepository.delete(recommendationEntity);
    }
}
