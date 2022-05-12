package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ActivityEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.GroupMemberEntity;
import fr.tobby.tripnjoyback.exception.ActivityNotFoundException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.repository.ActivityRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanningService {

    private final ActivityRepository activityRepository;
    private final GroupRepository groupRepository;

    public PlanningService(final ActivityRepository activityRepository, final GroupRepository groupRepository)
    {
        this.activityRepository = activityRepository;
        this.groupRepository = groupRepository;
    }

    public ActivityModel createActivity(final long groupId, final CreateActivityRequest request)
    {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        ActivityEntity activity = new ActivityEntity(group,
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                String.format("#%02x%02x%02x", request.getColor().getRed(), request.getColor().getGreen(), request.getColor().getBlue()),
                request.getLocation(),
                request.getIcon());

        return ActivityModel.from(activityRepository.save(activity));
    }

    public List<ActivityModel> getGroupActivities(final long groupId) throws GroupNotFoundException
    {
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);

        return activityRepository.findAllByGroupOrderByStartDate(group)
                                 .stream()
                                 .map(ActivityModel::from)
                                 .toList();
    }

    public void deleteActivity(final long activityId)
    {
        activityRepository.deleteById(activityId);
    }

    public void joinActivity(final long activityId, final long userId)
    {
        ActivityEntity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        GroupMemberEntity member = activity.getGroup().findMember(userId).orElseThrow(IllegalArgumentException::new);
        activity.getParticipants().add(member);
    }
}
