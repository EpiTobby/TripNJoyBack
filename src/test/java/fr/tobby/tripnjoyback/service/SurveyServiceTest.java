package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.entity.messaging.ChannelEntity;
import fr.tobby.tripnjoyback.entity.messaging.MessageTypeEntity;
import fr.tobby.tripnjoyback.exception.ForbiddenOperationException;
import fr.tobby.tripnjoyback.exception.SurveyNotFoundException;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.SurveyModel;
import fr.tobby.tripnjoyback.model.request.VoteSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PossibleAnswerRequest;
import fr.tobby.tripnjoyback.model.request.messaging.PostSurveyRequest;
import fr.tobby.tripnjoyback.model.request.messaging.UpdateSurveyRequest;
import fr.tobby.tripnjoyback.repository.*;
import fr.tobby.tripnjoyback.repository.messaging.ChannelRepository;
import fr.tobby.tripnjoyback.repository.messaging.MessageRepository;
import fr.tobby.tripnjoyback.repository.messaging.MessageTypeRepository;
import io.prometheus.client.Gauge;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@DataJpaTest
public class SurveyServiceTest {

    private static GenderEntity maleGender;
    private static GenderRepository genderRepository;
    private static CityRepository cityRepository;
    private static LanguageRepository languageRepository;
    private static StateRepository stateRepository;
    private static GroupMemberRepository memberRepository;
    private static CityEntity cityEntity;
    private static LanguageEntity languageEntity;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
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
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired ApplicationContext context,
                          @Autowired CityRepository cityRepository, @Autowired LanguageRepository languageRepository,
                          @Autowired StateRepository stateRepository, @Autowired GroupMemberRepository memberRepository
    ) {
        maleGender = genderRepository.save(new GenderEntity("male"));
        SurveyServiceTest.genderRepository = genderRepository;
        SurveyServiceTest.cityRepository = cityRepository;
        SurveyServiceTest.languageRepository = languageRepository;
        SurveyServiceTest.stateRepository = stateRepository;
        SurveyServiceTest.memberRepository = memberRepository;
        stateRepository.save(new StateEntity("CLOSED"));
        stateRepository.save(new StateEntity("OPEN"));
        cityEntity = cityRepository.save(new CityEntity("Paris"));
        languageEntity = languageRepository.save(new LanguageEntity("French"));
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp() {
        messageTypeRepository.save(new MessageTypeEntity("SURVEY"));
        surveyService = new SurveyService(surveyRepository, surveyAnswerRepository, voteRepository, messageRepository, userRepository, channelRepository);
    }

    @AfterEach
    void tearDown()
    {
        messageRepository.deleteAll();
        voteRepository.deleteAll();
        surveyAnswerRepository.deleteAll();
        surveyRepository.deleteAll();
        channelRepository.deleteAll();
        memberRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        cityRepository.deleteAll();
        stateRepository.deleteAll();
        genderRepository.deleteAll();
    }

    @NotNull
    private UserEntity anyUser(String email) throws ParseException {
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email(email)
                .birthDate(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000").toInstant())
                .city(cityEntity)
                .confirmed(true)
                .language(languageEntity)
                .roles(List.of())
                .build());
    }

    private ChannelEntity anyChannel(GroupEntity group) {
        return channelRepository.save(new ChannelEntity(group, "general"));
    }

    private GroupEntity anyGroup() {
        GroupEntity group = groupRepository.save(new GroupEntity(null, "test", "description", State.OPEN.getEntity(), null, 10, new Date(), null, null,
                "", "", new ArrayList<>(), null,
                new ArrayList<>() {
                }));
        group.channels.add(anyChannel(group));
        return group;
    }

    @Test
    public void createSurveyTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        long surveyId = surveyService.createSurvey(channelId, postSurveyRequest).getId();

        SurveyModel surveyModel = surveyService.getSurveyById(surveyId);
        Assertions.assertEquals(surveyModel.getQuestion(), question);
        Assertions.assertEquals(surveyModel.getPossibleAnswers().size(), 2);
    }

    @Test
    public void createVoteTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
        surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(0).getId())
                .voterId(user2.getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(surveyModel.getVotes().size(), 1);
    }

    @Test
    public void createVotesWithoutMultipleAnswersTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
        surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(0).getId())
                .voterId(user2.getId()).build());
        surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(1).getId())
                .voterId(user2.getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(surveyModel.getVotes().size(), 1);
    }

    @Test
    public void createVotesWithMultipleAnswersTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, true);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
        surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(0).getId())
                .voterId(user2.getId()).build());
        surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(1).getId())
                .voterId(user2.getId()).build());
        SurveyModel surveyModel = surveyService.getSurveysByChannelId(channelId).get(0);
        Assertions.assertEquals(surveyModel.getVotes().size(), 2);
    }

    @Test
    public void deleteSurveyTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        long surveyId = surveyService.createSurvey(channelId, postSurveyRequest).getId();
        surveyService.deleteSurvey(surveyId, user1.getId());
        Assertions.assertThrows(SurveyNotFoundException.class, () -> surveyService.getSurveyById(surveyId));
    }

    @Test
    public void deleteVoteTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
        long voteId = surveyService.submitVote(survey.getId(), VoteSurveyRequest.builder()
                .answerId(survey.getPossibleAnswers().get(0).getId())
                .voterId(user2.getId()).build()).getVotes().stream().findFirst().get().getId();
        surveyService.deleteVote(voteId, user2.getId());
        Assertions.assertTrue(voteRepository.findById(voteId).isEmpty());
    }

    @Test
    public void updateSurveyTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
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
        survey = surveyService.updateSurvey(survey.getId(), user1.getId(), UpdateSurveyRequest.builder()
                .multipleChoiceSurvey(true)
                .possibleAnswers(possibleAnswerRequests).build());
        Assertions.assertTrue(survey.isMultipleChoiceSurvey());
        Assertions.assertEquals(survey.getPossibleAnswers().size(), 3);
    }

    @Test
    public void deleteSurveyNotBySubmitterTest() throws ParseException {
        String question = "Where would you like to go?";
        GroupEntity groupEntity = anyGroup();
        UserEntity user1 = anyUser("user1@gmail.com");
        UserEntity user2 = anyUser("user2@gmail.com");
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user1, null, false)));
        groupEntity.getMembers().add(memberRepository.save(new GroupMemberEntity(groupEntity, user2, null, false)));
        long channelId = groupEntity.channels.stream().findFirst().get().getId();
        ArrayList<PossibleAnswerRequest> possibleAnswerRequests = new ArrayList<>();
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Spain").build());
        possibleAnswerRequests.add(PossibleAnswerRequest.builder()
                .rightAnswer(false)
                .content("Portugal").build());
        PostSurveyRequest postSurveyRequest = new PostSurveyRequest(user1.getId(),
                question, false, possibleAnswerRequests, false);
        SurveyModel survey = surveyService.createSurvey(channelId, postSurveyRequest);
        List<SurveyModel> surveys = surveyService.getSurveysByChannelId(channelId);
        Assertions.assertThrows(ForbiddenOperationException.class, () -> surveyService.deleteSurvey(survey.getId(), user2.getId()));
        Assertions.assertEquals(surveys.size(), 1);
    }
}
