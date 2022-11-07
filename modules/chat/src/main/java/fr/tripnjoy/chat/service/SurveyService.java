package fr.tripnjoy.chat.service;

import fr.tripnjoy.chat.dto.request.PostSurveyRequest;
import fr.tripnjoy.chat.dto.request.UpdateSurveyRequest;
import fr.tripnjoy.chat.dto.request.VoteSurveyRequest;
import fr.tripnjoy.chat.entity.*;
import fr.tripnjoy.chat.exception.*;
import fr.tripnjoy.chat.model.MessageType;
import fr.tripnjoy.chat.model.SurveyModel;
import fr.tripnjoy.chat.repository.*;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
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
    private final ChannelRepository channelRepository;

    public SurveyService(SurveyRepository surveyRepository, SurveyAnswerRepository surveyAnswerRepository, VoteRepository voteRepository,
                         MessageRepository messageRepository, ChannelRepository channelRepository)
    {
        this.surveyRepository = surveyRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
        this.voteRepository = voteRepository;
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }

    public SurveyModel getSurveyById(long id)
    {
        SurveyEntity surveyEntity = surveyRepository.findById(id).orElseThrow(() -> new SurveyNotFoundException(id));
        return SurveyModel.of(surveyEntity);
    }

    public List<SurveyModel> getSurveysByChannelId(long channelId)
    {
        return surveyRepository.findByChannelId(channelId).stream().filter(s -> !s.isQuizz()).map(SurveyModel::of).toList();
    }

    public List<SurveyModel> getQuizz(long channelId, long userId)
    {
        return surveyRepository.findByChannelId(channelId).stream()
                               .filter(s -> s.isQuizz() && s.getSubmitter() != userId).map(SurveyModel::of).toList();
    }

    @Transactional
    public SurveyModel createSurvey(long channelId, long userId, PostSurveyRequest postSurveyRequest)
    {
        ChannelEntity channelEntity = channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        SurveyEntity surveyEntity = surveyRepository.save(
                new SurveyEntity(userId, channelEntity, postSurveyRequest.getContent(), postSurveyRequest.isQuizz(), Date.from(Instant.now()),
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
    public SurveyModel updateSurvey(long surveyId, long userId, UpdateSurveyRequest updateSurveyRequest)
    {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (surveyEntity.getSubmitter() != userId)
            throw new ForbiddenOperationException("You cannot update this survey!");
        if (updateSurveyRequest.getQuestion() != null)
            surveyEntity.setQuestion(updateSurveyRequest.getQuestion());
        surveyEntity.setMultipleChoiceSurvey(updateSurveyRequest.isMultipleChoiceSurvey());
        voteRepository.findBySurveyId(surveyId).forEach(voteRepository::delete);
        surveyEntity.getAnswers().clear();
        if (!updateSurveyRequest.getPossibleAnswers().isEmpty())
        {
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
    public SurveyModel submitVote(long surveyId, long voterId, VoteSurveyRequest voteSurveyRequest)
    {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        SurveyAnswerEntity surveyAnswerEntity = surveyAnswerRepository.findById(voteSurveyRequest.getAnswerId()).orElseThrow(() -> new SurveyAnswerNotFoundException(surveyId));
        if (!surveyAnswerEntity.getSurvey().getId().equals(surveyEntity.getId()))
            throw new SurveyVoteException("Cannot submit this vote for the survey!");
        Optional<VoteEntity> voteEntity = voteRepository.findByVoterAndSurveyId(voterId, surveyId);
        if (voteEntity.isPresent() && !surveyEntity.isMultipleChoiceSurvey())
        {
            voteEntity.get().setAnswer(surveyAnswerEntity);
        }
        else if (voteRepository.findByVoterAndSurveyIdAndAnswerId(voterId, surveyId, voteSurveyRequest.getAnswerId()).isEmpty())
            surveyEntity.getVotes().add(voteRepository.save(new VoteEntity(surveyEntity, surveyAnswerEntity, voterId)));
        return SurveyModel.of(surveyEntity);
    }

    @Transactional
    public void deleteVote(long voteId, long userId)
    {
        VoteEntity voteEntity = voteRepository.findById(voteId).orElseThrow(() -> new VoteNotFoundException(voteId));
        if (userId != voteEntity.getVoter())
            throw new ForbiddenOperationException("You cannot delete this survey!");
        voteRepository.delete(voteEntity);
    }

    @Transactional
    public void deleteSurvey(long surveyId, long userId)
    {
        SurveyEntity surveyEntity = surveyRepository.findById(surveyId).orElseThrow(() -> new SurveyNotFoundException(surveyId));
        if (surveyEntity.getSubmitter() != userId)
            throw new ForbiddenOperationException("You cannot delete this survey!");
        MessageEntity messageEntity = messageRepository.findByTypeAndContent(MessageType.SURVEY.getEntity(), String.valueOf(surveyId)).orElseThrow(MessageNotFoundException::new);
        messageRepository.delete(messageEntity);
        surveyRepository.delete(surveyEntity);
    }
}
