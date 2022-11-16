package fr.tripnjoy.chat.service;

import fr.tripnjoy.chat.SpringContext;
import fr.tripnjoy.chat.dto.request.PossibleAnswerRequest;
import fr.tripnjoy.chat.dto.request.PostSurveyRequest;
import fr.tripnjoy.chat.dto.request.UpdateSurveyRequest;
import fr.tripnjoy.chat.dto.request.VoteSurveyRequest;
import fr.tripnjoy.chat.entity.ChannelEntity;
import fr.tripnjoy.chat.entity.MessageEntity;
import fr.tripnjoy.chat.entity.MessageTypeEntity;
import fr.tripnjoy.chat.exception.SurveyNotFoundException;
import fr.tripnjoy.chat.model.MessageType;
import fr.tripnjoy.chat.model.SurveyModel;
import fr.tripnjoy.chat.repository.*;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-tests.properties")
class SurveyServiceTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private SurveyAnswerRepository surveyAnswerRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageTypeRepository messageTypeRepository;

    private SurveyService surveyService;

    @BeforeAll
    static void beforeAll(@Autowired ApplicationContext context)
    {
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp()
    {
        messageTypeRepository.save(new MessageTypeEntity("TEXT"));
        messageTypeRepository.save(new MessageTypeEntity("IMAGE"));
        messageTypeRepository.save(new MessageTypeEntity("FILE"));
        messageTypeRepository.save(new MessageTypeEntity("SURVEY"));
        surveyService = new SurveyService(surveyRepository, surveyAnswerRepository, voteRepository, messageRepository, channelRepository);
    }

    @AfterEach
    void tearDown()
    {
        messageRepository.deleteAll();
        voteRepository.deleteAll();
        surveyAnswerRepository.deleteAll();
        surveyRepository.deleteAll();
        channelRepository.deleteAll();
    }

    private long userIdCounter = 1;

    private long anyUser()
    {
        return userIdCounter++;
    }

    private ChannelEntity anyChannel(long group)
    {
        return channelRepository.save(new ChannelEntity(group, "general"));
    }

    private long groupIdCounter = 1;

    private long anyGroup()
    {
        anyChannel(groupIdCounter);
        return groupIdCounter++;
    }

    private void userInGroup(long groupId, long userId)
    {

    }

    @Test
    void createSurveyTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);

        long surveyId = surveyService.createSurvey(channelId, user1, postSurveyRequest).getId();

        SurveyModel surveyModel = surveyService.getSurveyById(surveyId);
        Assertions.assertEquals(question, surveyModel.getQuestion());
        Assertions.assertEquals(2, surveyModel.getPossibleAnswers().size());
    }

    @Test
    void createVoteTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(0).getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(1, surveyModel.getVotes().size());
    }

    @Test
    void createVotesWithoutMultipleAnswersTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(0).getId()).build());
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(1).getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(1, surveyModel.getVotes().size());
    }

    @Test
    void createVotesWithMultipleAnswersTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, true);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(0).getId()).build());
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(1).getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(2, surveyModel.getVotes().size());
    }

    @Test
    void voteAnswerTwiceTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, true);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(0).getId()).build());
        surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                         .answerId(survey.getPossibleAnswers().get(0).getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(1, surveyModel.getVotes().size());
    }

    @Test
    void deleteSurveyTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        long surveyId = surveyService.createSurvey(channelId, user1, postSurveyRequest).getId();

        messageRepository.save(new MessageEntity(user1, channelRepository.findAllByGroup(groupId).get(0), String.valueOf(surveyId), MessageType.SURVEY.getEntity(), Date.from(Instant.now())));
        surveyService.deleteSurvey(surveyId, user1);
        Assertions.assertThrows(SurveyNotFoundException.class, () -> surveyService.getSurveyById(surveyId));
    }

    @Test
    void deleteVoteTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        long voteId = surveyService.submitVote(survey.getId(), user2, VoteSurveyRequest.builder()
                                                                                       .answerId(survey.getPossibleAnswers().get(0).getId()).build()).getVotes().stream().findFirst().get().getId();
        surveyService.deleteVote(voteId, user2);
        Assertions.assertTrue(voteRepository.findById(voteId).isEmpty());
    }

    @Test
    void updateSurveyTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        List<SurveyModel> surveys = surveyService.getSurveysByChannelId(channelId);
        possibleAnswerRequests.clear();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Monaco").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Rome").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Dublin").build());
        survey = surveyService.updateSurvey(survey.getId(), user1, UpdateSurveyRequest.builder()
                                                                                      .multipleChoiceSurvey(true)
                                                                                      .possibleAnswers(possibleAnswerRequests).build());
        Assertions.assertTrue(survey.isMultipleChoiceSurvey());
        Assertions.assertEquals(3, survey.getPossibleAnswers().size());
    }

    @Test
    void deleteSurveyNotBySubmitterTest()
    {
        String question = "Where would you like to go?";
        long groupId = anyGroup();
        long user1 = anyUser();
        long user2 = anyUser();
        userInGroup(groupId, user1);
        userInGroup(groupId, user2);
        long channelId = channelRepository.findAllByGroup(groupId).get(0).getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                                                        .rightAnswer(false)
                                                        .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1,
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, user1, postSurveyRequest);
        List<SurveyModel> surveys = surveyService.getSurveysByChannelId(channelId);
        Assertions.assertThrows(ForbiddenOperationException.class, () -> surveyService.deleteSurvey(survey.getId(), user2));
        Assertions.assertEquals(1, surveys.size());
    }
}
