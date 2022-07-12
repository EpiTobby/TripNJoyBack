package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.SpringContext;
import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdatePrivateGroupRequest;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@DataJpaTest
public class GroupServiceTest {
    private static GenderEntity maleGender;
    private static GenderEntity femaleGender;
    private static GenderEntity otherGender;
    private static GenderRepository genderRepository;
    private static StateEntity closedState;
    private static StateEntity openState;
    private static StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private ActivityRepository activityRepository;
    private ChannelService channelService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private GroupService groupService;

    @Autowired
    private ApplicationContext context;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository) {
        maleGender = genderRepository.save(new GenderEntity("male"));
        femaleGender = genderRepository.save(new GenderEntity("female"));
        otherGender = genderRepository.save(new GenderEntity("other"));
        GroupServiceTest.genderRepository = genderRepository;

        closedState = stateRepository.save(new StateEntity("CLOSED"));
        openState = stateRepository.save(new StateEntity("OPEN"));
        GroupServiceTest.stateRepository = stateRepository;
    }

    @AfterEach
    void deleteData(){
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        stateRepository.deleteAll();
        genderRepository.deleteAll();
    }

    @BeforeEach
    void initGroupService(){
        channelService = mock(ChannelService.class);
        groupService = new GroupService(groupRepository, userRepository, groupMemberRepository, profileRepository, channelService, activityRepository, mock(ProfileService.class));
        SpringContext.setContext(context);
    }

    @NotNull
    private UserEntity anyUser() throws ParseException {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        LanguageEntity language = languageRepository.save(new LanguageEntity("French"));
        return userRepository.save(UserEntity.builder()
                                             .firstname("Test")
                                             .lastname("1")
                                             .gender(maleGender)
                                             .email("test@1.com")
                                             .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                             .city(city)
                                             .confirmed(true)
                                             .language(language)
                                             .roles(List.of())
                                             .build());
    }

    @NotNull
    private UserEntity anyUserWithEmail(String email) throws ParseException {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        LanguageEntity language = languageRepository.save(new LanguageEntity("French"));
        return userRepository.save(UserEntity.builder()
                                             .firstname("Test")
                                             .lastname("1")
                                             .gender(maleGender)
                                             .email(email)
                                             .birthDate(dateFormat.parse("01-01-2000").toInstant())
                                             .city(city)
                                             .confirmed(true)
                                             .language(language)
                                             .roles(List.of())
                                             .build());
    }

    @NotNull
    private ProfileEntity anyProfile(long userId) {
        ProfileEntity profileEntity = ProfileEntity.builder()
                .name("prof")
                .active(true).build();
        userRepository.findById(userId).orElseThrow(UserNotConfirmedException::new)
                .getProfiles().add(profileEntity);
        return profileRepository.save(profileEntity);
    }

    @Test
    void testOwner() throws ParseException {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), request);
        Assertions.assertEquals(model.getOwner().getId(), (long) owner.getId());
    }

    @Test
    void testUpdateGroupNull() throws ParseException{
        UpdatePrivateGroupRequest updatePrivateGroupRequest = new UpdatePrivateGroupRequest();
        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), createPrivateGroupRequest);
        groupService.updatePrivateGroup(model.getId(), updatePrivateGroupRequest);
        Assertions.assertEquals("grouptest", model.getName());
        Assertions.assertEquals(3, model.getMaxSize());
    }

    @Test
    void testUpdateGroupManyFields() throws ParseException{
        Date newStartDate = dateFormat.parse("01-07-2025");
        Date newEndDate = dateFormat.parse("06-07-2025");
        UpdatePrivateGroupRequest updatePrivateGroupRequest = new UpdatePrivateGroupRequest().builder()
                                                                                             .name("new name")
                                                                                             .maxSize(5)
                                                                                             .startOfTrip(newStartDate)
                                                                                             .endOfTrip(newEndDate)
                                                                                             .picture("group.png")
                                                                                             .destination("Madrid")
                                                                                             .build();
        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), createPrivateGroupRequest);
        groupService.updatePrivateGroup(model.getId(), updatePrivateGroupRequest);
        GroupEntity entity = groupRepository.findById(model.getId()).get();
        Assertions.assertEquals("new name", entity.getName());
        Assertions.assertEquals(5, entity.getMaxSize());
        Assertions.assertEquals("group.png", entity.getPicture());
        Assertions.assertEquals("Madrid", entity.getDestination());
        Assertions.assertEquals(0, entity.getStartOfTrip().compareTo(newStartDate));
        Assertions.assertEquals(0, entity.getEndOfTrip().compareTo(newEndDate));
    }

    @Test
    void testDeletePendingInvites() throws ParseException {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), request);
        UserEntity user1 = anyUserWithEmail("usermaxsize1@gmail.com");
        UserEntity user2 = anyUserWithEmail("usermaxsize2@gmail.com");
        UserEntity user3 = anyUserWithEmail("usermaxsize3@gmail.com");
        groupService.inviteUserInPrivateGroup(model.getId(), user1.getEmail());
        groupService.inviteUserInPrivateGroup(model.getId(), user2.getEmail());
        groupService.inviteUserInPrivateGroup(model.getId(), user3.getEmail());
        groupService.joinGroup(model.getId(), user1.getId());
        groupService.joinGroup(model.getId(), user2.getId());
        Assertions.assertEquals(model.getState(),State.CLOSED);
        Assertions.assertThrows(UserNotFoundException.class, () -> groupService.joinGroup(model.getId(), user3.getId()));
    }

    @Test
    void testLeaveGroup() throws ParseException {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), request);
        UserEntity user1 = anyUserWithEmail("userwillleave@gmail.com");
        groupService.inviteUserInPrivateGroup(model.getId(), user1.getEmail());
        groupService.joinGroup(model.getId(), user1.getId());
        groupService.removeUserFromGroup(model.getId(), user1.getId());
        GroupEntity entity = groupRepository.findById(model.getId()).get();
        Assertions.assertFalse(entity.members.stream().anyMatch(m -> m.getUser().getId() == user1.getId()));
    }

    @Test
    void testGroupIsDeletedIfEmpty() throws ParseException {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), request);
        groupService.removeUserFromGroup(model.getId(), owner.getId());
        Optional<GroupEntity> entity = groupRepository.findById(model.getId());
        Assertions.assertTrue(entity.isEmpty());
    }

}
