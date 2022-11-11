package fr.tripnjoy.planning.service;

import fr.tripnjoy.common.dto.BooleanResponse;
import fr.tripnjoy.groups.api.client.GroupFeignClient;
import fr.tripnjoy.groups.exception.GroupNotFoundException;
import fr.tripnjoy.planning.dto.request.CreateActivityRequest;
import fr.tripnjoy.planning.dto.request.UpdateActivityRequest;
import fr.tripnjoy.planning.dto.response.ActivityResponse;
import fr.tripnjoy.planning.entity.ActivityEntity;
import fr.tripnjoy.planning.entity.ActivityInfoEntity;
import fr.tripnjoy.planning.entity.ActivityMemberEntity;
import fr.tripnjoy.planning.exception.ActivityNotFoundException;
import fr.tripnjoy.planning.repository.ActivityMemberRepository;
import fr.tripnjoy.planning.repository.ActivityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class PlanningServiceTest {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityMemberRepository activityMemberRepository;

    private PlanningService planningService;
    private GroupFeignClient groupFeignClient;

    @BeforeEach
    void setUp()
    {
        groupFeignClient = mock(GroupFeignClient.class);
        when(groupFeignClient.exists(anyLong())).thenReturn(new BooleanResponse(false));
        when(groupFeignClient.isUserInGroup(anyLong(), anyLong())).thenReturn(new BooleanResponse(false));
        planningService = new PlanningService(activityRepository, activityMemberRepository, groupFeignClient);
    }

    @AfterEach
    void tearDown()
    {
        activityRepository.deleteAll();
    }

    private int groupIdCounter = 1;

    private long anyGroup()
    {
        when(groupFeignClient.exists(groupIdCounter)).thenReturn(new BooleanResponse(true));
        return groupIdCounter++;
    }

    private int userIdCounter = 1;

    private long anyUser()
    {
        return userIdCounter++;
    }

    private void userInGroup(long groupId, long userId)
    {
        when(groupFeignClient.isUserInGroup(groupId, userId)).thenReturn(new BooleanResponse(true));
    }

    private ActivityEntity anyActivity(long group)
    {
        ActivityEntity activity = new ActivityEntity(group, "foo", "bar", new Date(), new Date(), "#ff0000", "", "");
        return activityRepository.save(activity);
    }

    private void addUserToActivity(final long user, final ActivityEntity activity)
    {
        activity.getParticipants().add(activityMemberRepository.save(new ActivityMemberEntity(new ActivityMemberEntity.Ids(activity, user))));
    }

    @Test
    void createActivity()
    {
        long groupId = anyGroup();

        CreateActivityRequest req = new CreateActivityRequest("Test", "foo", new Date(), new Date(), new ArrayList<>(), "#ff0000", "", "");

        ActivityResponse res = planningService.createActivity(groupId, req);

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
    void joinActivity()
    {
        long groupId = anyGroup();
        ActivityEntity activity = anyActivity(groupId);
        long userId = anyUser();
        userInGroup(groupId, userId);

        planningService.joinActivity(activity.getId(), userId);

        assertEquals(1, activity.getParticipants().size());
        assertEquals(userId, new ArrayList<>(activity.getParticipants()).get(0).getIds().getParticipantId());
    }

    @Test
    void joinActivityUserNotInGroup()
    {
        long group = anyGroup();
        long user = anyUser();
        ActivityEntity activity = anyActivity(group);

        assertThrows(IllegalArgumentException.class, () -> planningService.joinActivity(activity.getId(), user));

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void joinActivityInvalidActivity()
    {
        long group = anyGroup();
        long user = anyUser();
        userInGroup(group, user);
        ActivityEntity activity = anyActivity(group);

        assertThrows(ActivityNotFoundException.class, () -> planningService.joinActivity(activity.getId() + 1, user));

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void leaveActivity()
    {
        long group = anyGroup();
        long user = anyUser();
        userInGroup(group, user);
        ActivityEntity activity = anyActivity(group);
        addUserToActivity(user, activity);

        planningService.leaveActivity(activity.getId(), user);

        assertEquals(0, activity.getParticipants().size());
    }

    @Test
    void leaveActivityInvalidActivity()
    {
        long group = anyGroup();
        long user = anyUser();
        userInGroup(group, user);
        ActivityEntity activity = anyActivity(group);
        addUserToActivity(user, activity);

        assertThrows(ActivityNotFoundException.class, () -> planningService.leaveActivity(activity.getId() + 1, user));

        assertEquals(1, activity.getParticipants().size());
    }

    @Test
    void updateActivityNameTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setName("Hello world!")
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals("Hello world!", result.name());
        assertEquals("Hello world!", activity.getName());
    }

    @Test
    void updateOtherFieldsTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setDescription("Hello world!")
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals("foo", result.name());
        assertEquals("foo", activity.getName());
    }

    @Test
    void updateActivityAddInfoTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setInfos(List.of("test"))
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.infos().size());
        assertEquals("test", result.infos().get(0));
    }

    @Test
    void updateActivityRemoveInfoTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        activity.getInfos().add(new ActivityInfoEntity("test"));

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setInfos(List.of())
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(0, result.infos().size());
    }

    @Test
    void updateActivityDoNotUpdateInfoTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        activity.getInfos().add(new ActivityInfoEntity("test"));

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.infos().size());
    }

    @Test
    void updateActivityAddParticipantsFromEmptyTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        long user = anyUser();
        userInGroup(user, group);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setParticipants(List.of(user))
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.participants().size());
    }

    @Test
    void updateActivityAddParticipantsFromNonEmptyTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        long userA = anyUser();
        userInGroup(group, userA);
        addUserToActivity(userA, activity);
        long userB = anyUser();
        userInGroup(group, userB);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setParticipants(List.of(userA, userB))
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(2, result.participants().size());
    }

    @Test
    void updateActivityRemoveParticipantTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        long userA = anyUser();
        userInGroup(group, userA);
        addUserToActivity(userA, activity);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setParticipants(List.of())
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(0, result.participants().size());
    }

    @Test
    void updateActivityAddParticipantsAndRemoveFromNonEmptyTest()
    {
        long group = anyGroup();
        ActivityEntity activity = anyActivity(group);
        long userA = anyUser();
        userInGroup(group, userA);
        addUserToActivity(userA, activity);
        long userB = anyUser();
        userInGroup(group, userB);

        UpdateActivityRequest request = UpdateActivityRequest.builder()
                                                             .setParticipants(List.of(userB))
                                                             .build();
        ActivityResponse result = planningService.updateActivity(activity.getId(), request);

        assertEquals(1, result.participants().size());
        assertEquals(userB, new ArrayList<>(activity.getParticipants()).get(0).getIds().getParticipantId());
    }
}