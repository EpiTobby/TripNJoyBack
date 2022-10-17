package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import fr.tobby.tripnjoyback.exception.ChannelNotFoundException;
import fr.tobby.tripnjoyback.exception.SurveyNotFoundException;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.repository.SurveyRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public SurveyService(SurveyRepository surveyRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.surveyRepository = surveyRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public List<SurveyModel> getByChannelId(long channelId){
        return surveyRepository.findByChannelId(channelId).stream().map(SurveyModel::of).toList();
    }

    @Transactional
    public SurveyModel createSurvey(long channelId, PostSurveyRequest postSurveyRequest){
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        UserEntity userEntity = userRepository.findById(postSurveyRequest.getUserId()).orElseThrow(() -> new ChannelNotFoundException(postSurveyRequest.getUserId()));
        SurveyEntity surveyEntity = surveyRepository.save(
                new SurveyEntity(userEntity, channelEntity, postSurveyRequest.getContent(), postSurveyRequest.isQuizz(), Date.from(Instant.now()), null)
        );
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public SurveyModel updateSurvey(long surveyId, UpdateSurveyRequest updateSurveyRequest){
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        surveyEntity.setQuestion(updateSurveyRequest.getQuestion());
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public void deleteSurvey(long surveyId){
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        surveyRepository.delete(surveyEntity);
    }
}
