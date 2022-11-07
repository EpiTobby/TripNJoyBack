package fr.tripnjoy.groups.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.common.exception.ForbiddenOperationException;
import fr.tripnjoy.groups.QRCodeGenerator;
import fr.tripnjoy.groups.SpringContext;
import fr.tripnjoy.groups.dto.request.CreatePrivateGroupRequest;
import fr.tripnjoy.groups.dto.request.JoinGroupWithoutInviteModel;
import fr.tripnjoy.groups.dto.request.UpdatePrivateGroupRequest;
import fr.tripnjoy.groups.entity.GroupEntity;
import fr.tripnjoy.groups.entity.StateEntity;
import fr.tripnjoy.groups.model.GroupModel;
import fr.tripnjoy.groups.model.State;
import fr.tripnjoy.groups.repository.GroupMemberRepository;
import fr.tripnjoy.groups.repository.GroupMemoryRepository;
import fr.tripnjoy.groups.repository.GroupRepository;
import fr.tripnjoy.groups.repository.StateRepository;
import fr.tripnjoy.profiles.api.client.ProfileFeignClient;
import fr.tripnjoy.users.api.client.UserFeignClient;
import fr.tripnjoy.users.api.exception.UserNotFoundException;
import fr.tripnjoy.users.api.response.UserResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
class GroupServiceTest {
    private static StateEntity closedState;
    private static StateEntity openState;
    private static StateRepository stateRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupMemoryRepository groupMemoryRepository;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private GroupService groupService;

    @Autowired
    private ApplicationContext context;
    private UserFeignClient userFeignClient;

    private long userIdCounter = 1;

    private long anyUser()
    {
        when(userFeignClient.exists(userIdCounter)).thenReturn(new BooleanResponse(true));
        return userIdCounter++;
    }

    @BeforeAll
    static void beforeAll(@Autowired StateRepository stateRepository)
    {
        closedState = stateRepository.save(new StateEntity("CLOSED"));
        openState = stateRepository.save(new StateEntity("OPEN"));
        GroupServiceTest.stateRepository = stateRepository;
    }

    @AfterEach
    void deleteData()
    {
        groupRepository.deleteAll();
    }

    @AfterAll
    static void afterAll()
    {
        stateRepository.deleteAll();
    }

    @BeforeEach
    void initGroupService()
    {
        QRCodeGenerator qrCodeGenerator = mock(QRCodeGenerator.class);
        userFeignClient = mock(UserFeignClient.class);
        ProfileFeignClient profileFeignClient = mock(ProfileFeignClient.class);
        groupService = new GroupService(groupRepository,
                groupMemberRepository,
                groupMemoryRepository,
                qrCodeGenerator,
                "",
                userFeignClient,
                profileFeignClient);

        SpringContext.setContext(context);
    }

    @Test
    void testOwner()
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        Assertions.assertEquals(model.getOwnerId(), owner);
    }

    @Test
    void testUpdateGroupNull()
    {
        UpdatePrivateGroupRequest updatePrivateGroupRequest = new UpdatePrivateGroupRequest();

        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, createPrivateGroupRequest);
        groupService.updatePrivateGroup(model.getId(), updatePrivateGroupRequest);
        Assertions.assertEquals("grouptest", model.getName());
        Assertions.assertEquals(3, model.getMaxSize());
    }

    @Test
    void testUpdateGroupManyFields() throws ParseException
    {
        Date newStartDate = dateFormat.parse("01-07-2025");
        Date newEndDate = dateFormat.parse("06-07-2025");
        UpdatePrivateGroupRequest updatePrivateGroupRequest = UpdatePrivateGroupRequest.builder()
                                                                                       .name("new name")
                                                                                       .maxSize(5)
                                                                                       .startOfTrip(newStartDate)
                                                                                       .endOfTrip(newEndDate)
                                                                                       .picture("group.png")
                                                                                       .destination("Madrid")
                                                                                       .build();
        CreatePrivateGroupRequest createPrivateGroupRequest = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, createPrivateGroupRequest);
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
    void testDeletePendingInvites()
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        long user1 = anyUser();
        long user2 = anyUser();
        long user3 = anyUser();
        when(userFeignClient.getCurrentUser("usermaxsize1@gmail.com")).thenReturn(UserResponse.builder().id(user1).confirmed(true).build());
        when(userFeignClient.getCurrentUser("usermaxsize2@gmail.com")).thenReturn(UserResponse.builder().id(user2).confirmed(true).build());
        when(userFeignClient.getCurrentUser("usermaxsize3@gmail.com")).thenReturn(UserResponse.builder().id(user3).confirmed(true).build());
        groupService.inviteUserInPrivateGroup(model.getId(), "usermaxsize1@gmail.com");
        groupService.inviteUserInPrivateGroup(model.getId(), "usermaxsize2@gmail.com");
        groupService.inviteUserInPrivateGroup(model.getId(), "usermaxsize3@gmail.com");
        groupService.joinGroup(model.getId(), user1);
        groupService.joinGroup(model.getId(), user2);
        Assertions.assertEquals(State.CLOSED, model.getState());
        Assertions.assertThrows(UserNotFoundException.class, () -> groupService.joinGroup(model.getId(), user3));
    }

    @Test
    void addUserWithoutInviteTest() throws NoSuchAlgorithmException
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        String stringToHash = String.format("tripnjoy-group-qr:%o;", model.getId());
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        long user1 = anyUser();
        long user2 = anyUser();
        when(userFeignClient.getCurrentUser("usermaxsize1@gmail.com")).thenReturn(UserResponse.builder().id(user1).confirmed(true).build());
        when(userFeignClient.getCurrentUser("usermaxsize2@gmail.com")).thenReturn(UserResponse.builder().id(user2).confirmed(true).build());
        groupService.joinGroupWithoutInvite(model.getId(), user1,
                new JoinGroupWithoutInviteModel(Arrays.toString(digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8)))));
        groupService.joinGroupWithoutInvite(model.getId(), user2,
                new JoinGroupWithoutInviteModel(Arrays.toString(digest.digest(stringToHash.getBytes(StandardCharsets.UTF_8)))));
        Optional<GroupModel> updatedModel = groupService.getGroup(model.getId());
        updatedModel.ifPresent(groupModel -> Assertions.assertEquals(3, groupModel.getMembers().size()));
    }

    @Test
    void addUserWithoutInviteFailureTest()
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        long user1 = anyUser();
        when(userFeignClient.getCurrentUser("usermaxsize1@gmail.com")).thenReturn(UserResponse.builder().id(user1).confirmed(true).build());
        Assertions.assertThrows(ForbiddenOperationException.class, () -> groupService.joinGroupWithoutInvite(model.getId(), user1,
                new JoinGroupWithoutInviteModel("a")));
    }

    @Test
    void testLeaveGroup()
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        long user1 = anyUser();
        when(userFeignClient.getCurrentUser("usermaxsize1@gmail.com")).thenReturn(UserResponse.builder().id(user1).confirmed(true).build());
        groupService.inviteUserInPrivateGroup(model.getId(), "usermaxsize1@gmail.com");
        groupService.joinGroup(model.getId(), user1);
        groupService.removeUserFromGroup(model.getId(), user1);
        GroupEntity entity = groupRepository.findById(model.getId()).get();
        Assertions.assertFalse(entity.members.stream().anyMatch(m -> m.getUserId() == user1));
    }

    @Test
    void testGroupIsDeletedIfEmpty()
    {
        CreatePrivateGroupRequest request = CreatePrivateGroupRequest.builder().name("grouptest").maxSize(3).build();
        long owner = anyUser();
        GroupModel model = groupService.createPrivateGroup(owner, request);
        groupService.removeUserFromGroup(model.getId(), owner);
        Optional<GroupEntity> entity = groupRepository.findById(model.getId());
        Assertions.assertTrue(entity.isEmpty());
    }
}
