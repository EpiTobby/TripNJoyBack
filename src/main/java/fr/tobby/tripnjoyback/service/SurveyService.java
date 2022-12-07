package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.SurveyAnswerEntity;
import fr.tobby.tripnjoyback.entity.UserEntity;
import fr.tobby.tripnjoyback.entity.VoteEntity;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.MessageEntity;
import fr.tobby.tripnjoyback.entity.messaging.SurveyEntity;
import fr.tobby.tripnjoyback.exception.*;
import fr.tobby.tripnjoyback.model.MessageType;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.VoteSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.repository.SurveyAnswerRepository;
import fr.tobby.tripnjoyback.repository.SurveyRepository;
import fr.tobby.tripnjoyback.repository.UserRepository;
import fr.tobby.tripnjoyback.repository.VoteRepository;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import fr.tobby.tripnjoyback.repository.messaging.MessageRepository;
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
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    public SurveyService(SurveyRepository surveyRepository, SurveyAnswerRepository surveyAnswerRepository, VoteRepository voteRepository, MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
        this.voteRepository = voteRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    public SurveyModel getSurveyById(long id) {
        SurveyEntity surveyEntity = surveyRepository.findById(id).orElseThrow(() -> new SurveyNotFoundException(id));
        return SurveyModel.of(surveyEntity);
    }

    public List<SurveyModel> getSurveysByChannelId(long channelId) {
        return surveyRepository.findByChannelId(channelId).stream().filter(s -> !s.isQuizz()).map(SurveyModel::of).toList();
    }

    public List<SurveyModel> getQuizz(long channelId, long userId) {
        return surveyRepository.findByChannelId(channelId).stream()
                .filter(s -> s.isQuizz() && s.getSubmitter().getId() != userId).map(SurveyModel::of).toList();
    }

    @Transactional
    public SurveyModel createSurvey(long channelId, PostSurveyRequest postSurveyRequest) {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        UserEntity userEntity = userRepository.findById(postSurveyRequest.getUserId()).orElseThrow(() -> new UserNotFoundException(postSurveyRequest.getUserId()));
        SurveyEntity surveyEntity = surveyRepository.save(
                new SurveyEntity(userEntity, channelEntity, postSurveyRequest.getContent(), postSurveyRequest.isQuizz(), Date.from(Instant.now()),
                        null, postSurveyRequest.isMultipleChoiceSurvey())
        );
        postSurveyRequest.getPossibleAnswers().forEach(possibleAnswer -> {
            SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.save(
                    new SurveyAnswerEntity(possibleAnswer.getContent(), surveyEntity, possibleAnswer.isRightAnswer())
            );
            surveyEntity.getAnswers().add(surveyAnswerEntity);
        });
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public SurveyModel updateSurvey(long surveyId, long userId, UpdateSurveyRequest updateSurveyRequest) {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (surveyEntity.getSubmitter().getId() != userId)
            throw new ForbiddenOperationException("You cannot update this survey!");
        if (updateSurveyRequest.getQuestion() != null)
            surveyEntity.setQuestion(updateSurveyRequest.getQuestion());
        surveyEntity.setMultipleChoiceSurvey(updateSurveyRequest.isMultipleChoiceSurvey());
        voteRepository.findBySurveyId(surveyId).forEach(voteRepository::delete);
        surveyEntity.getAnswers().clear();
        if (!updateSurveyRequest.getPossibleAnswers().isEmpty()) {
            surveyAnswerRepository.findBySurveyId(surveyId).forEach(surveyAnswerRepository::delete);
            updateSurveyRequest.getPossibleAnswers().forEach(possibleAnswer -> {
                SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.save(
                        new SurveyAnswerEntity(possibleAnswer.getContent(), surveyEntity, possibleAnswer.isRightAnswer())
                );
                surveyEntity.getAnswers().add(surveyAnswerEntity);
            });
        }
        surveyEntity.setModifiedDate(Date.from(Instant.now()));
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public SurveyModel submitVote(long surveyId, long voterId, VoteSurveyRequest voteSurveyRequest) {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        UserEntity userEntity = userRepository.findById(voterId).orElseThrow(() -> new UserNotFoundException(voterId));
        SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.findById(voteSurveyRequest.getAnswerId()).orElseThrow(() -> new SurveyAnswerNotFoundException(surveyId));
        if (!surveyAnswerEntity.getSurvey().getId().equals(surveyEntity.getId()))
            throw new SurveyVoteException("Cannot submit this vote for the survey!");
        Optional<VoteEntity> voteEntity = voteRepository.findByVoterIdAndSurveyId(userEntity.getId(), surveyId);
        if (voteEntity.isPresent() && !surveyEntity.isMultipleChoiceSurvey()) {
            voteEntity.get().setAnswer(surveyAnswerEntity);
        } else if (voteRepository.findByVoterIdAndSurveyIdAndAnswerId(userEntity.getId(), surveyId, voteSurveyRequest.getAnswerId()).isEmpty())
                surveyEntity.getVotes().add(voteRepository.save(new VoteEntity(surveyEntity, surveyAnswerEntity, userEntity)));
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public void deleteVote(long voteId, long userId) {
        VoteEntity voteEntity = voteRepository.findById(voteId).orElseThrow(() -> new VoteNotFoundException(voteId));
        if (userId != voteEntity.getVoter().getId())
            throw new ForbiddenOperationException("You cannot delete this survey!");
        voteRepository.delete(voteEntity);
    }

    @Transactional
    public void deleteSurvey(long surveyId, long userId) {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (surveyEntity.getSubmitter().getId() != userId)
            throw new ForbiddenOperationException("You cannot delete this survey!");
        MessageEntity messageEntity = messageRepository.findByTypeAndContent(MessageType.SURVEY.getEntity(), String.valueOf(surveyId)).orElseThrow(MessageNotFoundException::new);
        messageRepository.delete(messageEntity);
        surveyRepository.delete(surveyEntity);
    }
}
