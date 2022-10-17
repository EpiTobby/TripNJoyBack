package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.VoteEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.VoteSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.repository.SurveyAnswerRepository;
import fr.tobby.tripnjoyback.repository.SurveyRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.VoteRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public SurveyService(SurveyRepository surveyRepository, SurveyAnswerRepository surveyAnswerRepository, VoteRepository voteRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public List<SurveyModel> getByChannelId(long channelId){
        return surveyRepository.findByChannelId(channelId).stream().map(SurveyModel::of).toList();
    }

    @Transactional
    public SurveyModel createSurvey(long channelId, PostSurveyRequest postSurveyRequest){
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        UserEntity userEntity = userRepository.findById(postSurveyRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(postSurveyRequest.getUserId()));
        SurveyEntity surveyEntity = surveyRepository.save(
                new SurveyEntity(userEntity, channelEntity, postSurveyRequest.getContent(), postSurveyRequest.isQuizz(), Date.from(Instant.now()), null)
        );
        postSurveyRequest.getPossibleAnswers().forEach(possibleAnswer ->{
            SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.save(
                    new SurveyAnswerEntity(postSurveyRequest.getContent(), surveyEntity, possibleAnswer.isRightAnswer())
            );
            surveyEntity.getAnswers().add(surveyAnswerEntity);
        });
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public SurveyModel updateSurvey(long surveyId, UpdateSurveyRequest updateSurveyRequest){
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        surveyEntity.setQuestion(updateSurveyRequest.getQuestion());
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public SurveyModel submitVote(long surveyId, VoteSurveyRequest voteSurveyRequest){
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        UserEntity userEntity = userRepository.findById(voteSurveyRequest.getVoterId()).orElseThrow(() -> new UserNotFoundException(voteSurveyRequest.getVoterId()));
        SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.findById(voteSurveyRequest.getAnswerId()).orElseThrow(() -> new SurveyAnswerNotFoundException(surveyId));
        if (!surveyAnswerEntity.getSurvey().getId().equals(surveyEntity.getId()))
            throw new SurveyVoteException("Cannot submit this vote for the survey!");
        Optional<VoteEntity> voteEntity = voteRepository.findByVoterIdAndByAndSurveyId(userEntity.getId(), surveyId);
        if (voteEntity.isPresent()) {
            voteEntity.get().setAnswer(surveyAnswerEntity);
        }
        else {
            surveyEntity.getVotes().add(voteRepository.save(new VoteEntity(surveyEntity,surveyAnswerEntity,userEntity)));
        }
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public void deleteSurvey(long surveyId){
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        surveyRepository.delete(surveyEntity);
    }
}
