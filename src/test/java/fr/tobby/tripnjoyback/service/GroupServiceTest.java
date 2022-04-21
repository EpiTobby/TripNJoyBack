package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.*;
import fr.tobby.tripnjoyback.exception.UserNotConfirmedException;
import fr.tobby.tripnjoyback.exception.UserNotFoundException;
import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.model.State;
import fr.tobby.tripnjoyback.model.request.CreatePrivateGroupRequest;
import fr.tobby.tripnjoyback.model.request.UpdateGroupRequest;
import fr.tobby.tripnjoyback.repository.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@DataJpaTest
public class GroupServiceTest {
    private static GenderEntity maleGender;
    private static GenderEntity femaleGender;
    private static GenderEntity otherGender;
    private static StateEntity closedState;
    private static StateEntity openState;
    private static RoleEntity defaultRole;

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private StateRepository stateRepository;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private GroupService groupService;

    @BeforeAll
    static void beforeAll(@Autowired GenderRepository genderRepository, @Autowired StateRepository stateRepository, @Autowired UserRoleRepository userRoleRepository) {
        maleGender = genderRepository.save(new GenderEntity("male"));
        femaleGender = genderRepository.save(new GenderEntity("female"));
        otherGender = genderRepository.save(new GenderEntity("other"));

        closedState = stateRepository.save(new StateEntity("CLOSED"));
        openState = stateRepository.save(new StateEntity("OPEN"));

        defaultRole = userRoleRepository.save(new RoleEntity("default"));
    }

    @BeforeEach
    void initGroupService(){
        groupService = new GroupService(groupRepository, userRepository, groupMemberRepository, profileRepository);
    }

    @NotNull
    private UserEntity anyUser() throws ParseException {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email("test@1.com")
                .birthDate(dateFormat.parse("01-01-2000").toInstant())
                .city(city)
                .confirmed(true)
                .roles(List.of(defaultRole))
                .build());
    }

    @NotNull
    private UserEntity anyUserWithEmail(String email) throws ParseException {
        CityEntity city = cityRepository.save(new CityEntity("Paris"));
        return userRepository.save(UserEntity.builder()
                .firstname("Test")
                .lastname("1")
                .gender(maleGender)
                .email(email)
                .birthDate(dateFormat.parse("01-01-2000").toInstant())
                .city(city)
                .confirmed(true)
                .roles(List.of(defaultRole))
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
    void checkOwner() throws ParseException {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), request);
        Assertions.assertTrue(model.getOwner().getId() == owner.getId());
    }

    @Test
    void checkUpdateGroupNull() throws ParseException{
        UpdateGroupRequest updateGroupRequest = new UpdateGroupRequest();
        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), createPrivateGroupRequest);
        groupService.updatePrivateGroup(model.getId(), updateGroupRequest);
        Assertions.assertTrue(model.getName().equals("grouptest"));
        Assertions.assertTrue(model.getMaxSize() == 3);
    }

    @Test
    void checkUpdateGroupManyFields() throws ParseException{
        Date newStartDate = dateFormat.parse("01-07-2025");
        Date newEndDate = dateFormat.parse("06-07-2025");
        UpdateGroupRequest updateGroupRequest = new UpdateGroupRequest().builder()
                                                .name("new name")
                                                .maxSize(5)
                                                .startOfTrip(newStartDate)
                                                .endOfTrip(newEndDate)
                                                .picture("group.png")
                                                .build();
        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        UserEntity owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner.getId(), createPrivateGroupRequest);
        groupService.updatePrivateGroup(model.getId(), updateGroupRequest);
        GroupEntity entity = groupRepository.findById(model.getId()).get();
        Assertions.assertTrue(entity.getName().equals("new name"));
        Assertions.assertTrue(entity.getMaxSize() == 5);
        Assertions.assertTrue(entity.getPicture().equals("group.png"));
        Assertions.assertTrue(entity.getStartOfTrip().compareTo(newStartDate) == 0);
        Assertions.assertTrue(entity.getEndOfTrip().compareTo(newEndDate) == 0);
    }

    @Test
    @Disabled
    void checkMaxSize() throws ParseException {
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
        Assertions.assertTrue(model.getState() == State.CLOSED);
        Assertions.assertThrows(UserNotFoundException.class, () -> groupService.joinGroup(model.getId(), user3.getId()));
    }

    @AfterEach
    void deleteData(){
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }

}
