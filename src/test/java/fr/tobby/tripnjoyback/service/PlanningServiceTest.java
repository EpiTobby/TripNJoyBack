package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.ActivityNotFoundException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.request.UpdateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlanningServiceTest {

    private static GenderEntity maleGender;
    private static GenderRepository genderRepository;
    private static CityRepository cityRepository;
    private static LanguageRepository languageRepository;
    private static StateRepository stateRepository;
    private static CityEntity cityEntity;
    private static LanguageEntity languageEntity;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;

    private PlanningService planningService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository,
                          @Autowired ApplicationContext context,
                          @Autowired CityRepository cityRepository, @Autowired LanguageRepository languageRepository)
    {
        maleGender = genderRepository.save(new GenderEntity("male"));
        PlanningServiceTest.genderRepository = genderRepository;
        PlanningServiceTest.cityRepository = cityRepository;
        PlanningServiceTest.languageRepository = languageRepository;

        stateRepository.save(new StateEntity("CLOSED"));
        stateRepository.save(new StateEntity("OPEN"));
        cityEntity = cityRepository.save(new CityEntity("Paris"));
        languageEntity = languageRepository.save(new LanguageEntity("French"));
        PlanningServiceTest.stateRepository = stateRepository;
        SpringContext.setContext(context);
    }

    @BeforeEach
    void setUp()
    {
        planningService = new PlanningService(activityRepository, groupRepository);
    }

    @AfterEach
    void tearDown()
    {
        activityRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        genderRepository.deleteAll();
        stateRepository.deleteAll();
        cityRepository.deleteAll();
        languageRepository.deleteAll();
    }

    private GroupEntity anyGroup()
    {
        GroupEntity group = new GroupEntity(null, "test","description", State.OPEN.getEntity(), null, 10, new Date(), null, null,
                "", new ArrayList<>(), null, new ArrayList<>());
        return groupRepository.save(group);
    }

    private ActivityEntity anyActivity(GroupEntity group)
    {
        ActivityEntity activity = new ActivityEntity(group, "foo", "bar", new Date(), new Date(), "#ff0000", "", "");
        return activityRepository.save(activity);
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
    void createActivity()
    {
        GroupEntity groupEntity = anyGroup();

        CreateActivityRequest req = new CreateActivityRequest("Test", "foo", new Date(), new Date(), new ArrayList<>(), "#ff0000", "", "");

        ActivityModel res = planningService.createActivity(groupEntity.getId(), req);

        assertEquals("Test", res.name());
        assertTrue(activityRepository.existsById(res.id()));
    }

    @Test
    void createActivityInvalidGroup()
    {
        CreateActivityRequest req = new CreateActivityRequest("Test", "foo", new Date(), new Date(), new ArrayList<>(), "#ff0000", "", "");

        assertThrows(GroupNotFoundException.class, () -> planningService.createActivity(1, req));
    }

    @Test
    void joinActivity() throws ParseException
    {
        GroupEntity group = anyGroup();
        UserEntity user = anyUser();
        group.getMembers().add(new GroupMemberEntity(group, user, null, false));
        ActivityEntity activity = anyActivity(group);

        planningService.joinActivity(activity.getId(), user.getId());

        assertEquals(1, activity.getParticipants().size());
        assertEquals(user, new ArrayList<>(activity.getParticipants()).get(0).getUser());
    }

    @Test
    void joinActivityUserNotInGroup() throws ParseException
    {
        GroupEntity group = anyGroup();
        UserEntity user = anyUser();
        ActivityEntity activity = anyActivity(group);

        assertThrows(IllegalArgumentException.class, () -> planningService.joinActivity(activity.getId(), user.getId()));

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void joinActivityInvalidActivity() throws ParseException
    {
        GroupEntity group = anyGroup();
        UserEntity user = anyUser();
        group.getMembers().add(new GroupMemberEntity(group, user, null, false));
        ActivityEntity activity = anyActivity(group);

        assertThrows(ActivityNotFoundException.class, () -> planningService.joinActivity(activity.getId() + 1, user.getId()));

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void leaveActivity() throws ParseException
    {
        GroupEntity group = anyGroup();
        UserEntity user = anyUser();
        GroupMemberEntity member = new GroupMemberEntity(group, user, null, false);
        group.getMembers().add(member);
        ActivityEntity activity = anyActivity(group);
        activity.getParticipants().add(member);

        planningService.leaveActivity(activity.getId(), user.getId());

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void leaveActivityInvalidActivity() throws ParseException
    {
        GroupEntity group = anyGroup();
        UserEntity user = anyUser();
        GroupMemberEntity member = new GroupMemberEntity(group, user, null, false);
        group.getMembers().add(member);
        ActivityEntity activity = anyActivity(group);
        activity.getParticipants().add(member);

        assertThrows(ActivityNotFoundException.class, () -> planningService.leaveActivity(activity.getId() + 1, user.getId()));

        assertEquals(1, activity.getParticipants().size());
    }

    @Test
    void updateActivityNameTest()
    {
        GroupEntity group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setName("Hello world!")
                                                             .build();
        ActivityModel result = planningService.updateActivity(activity.getId(), request);

        assertEquals("Hello world!", result.name());
        assertEquals("Hello world!", activity.getName());
    }

    @Test
    void updateOtherFieldsTest()
    {
        GroupEntity group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setDescription("Hello world!")
                                                             .build();
        ActivityModel result = planningService.updateActivity(activity.getId(), request);

        assertEquals("foo", result.name());
        assertEquals("foo", activity.getName());
    }

    @Test
    void updateActivityAddInfoTest()
    {
        GroupEntity group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setInfos(List.of("test"))
                                                             .build();
        ActivityModel result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.infos().size());
        assertEquals("test", result.infos().get(0));
    }

    @Test
    void updateActivityRemoveInfoTest()
    {
        GroupEntity group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        activity.getInfos().add(new ActivityInfoEntity("test"));

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setInfos(List.of())
                                                             .build();
        ActivityModel result = planningService.updateActivity(activity.getId(), request);

        assertEquals(0, result.infos().size());
    }

    @Test
    void updateActivityDoNotUpdateInfoTest()
    {
        GroupEntity group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        activity.getInfos().add(new ActivityInfoEntity("test"));

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .build();
        ActivityModel result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.infos().size());
    }
}