package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ActivityEntity;
import fr.tobby.tripnjoyback.entity.ActivityInfoEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.entity.GroupMemberEntity;
import fr.tobby.tripnjoyback.exception.ActivityNotFoundException;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.request.UpdateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.repository.ActivityRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
                request.getColor(),
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

    @Transactional
    public void joinActivity(final long activityId, final long userId)
    {
        ActivityEntity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        GroupMemberEntity member = activity.getGroup().findMember(userId).orElseThrow(IllegalArgumentException::new);
        activity.getParticipants().add(member);
    }

    @Transactional
    public void leaveActivity(final long activityId, final long userId)
    {
        ActivityEntity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        activity.getParticipants().removeIf(member -> member.getUser().getId().equals(userId));
    }

    @NotNull
    @Transactional
    public ActivityModel updateActivity(final long activityId, @NotNull final UpdateActivityRequest updateRequest)
    {
        ActivityEntity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        if (updateRequest.getName() != null)
            activity.setName(updateRequest.getName());
        if (updateRequest.getDescription() != null)
            activity.setDescription(updateRequest.getDescription());
        if (updateRequest.getStartDate() != null)
            activity.setStartDate(updateRequest.getStartDate());
        if (updateRequest.getEndDate() != null)
            activity.setEndDate(updateRequest.getEndDate());
        if (updateRequest.getColor() != null)
            activity.setColor(updateRequest.getColor());
        if (updateRequest.getLocation() != null)
            activity.setLocation(updateRequest.getLocation());
        if (updateRequest.getIcon() != null)
            activity.setIcon(updateRequest.getIcon());

        if (updateRequest.getInfos() != null)
        {
            activity.getInfos().removeIf(info -> !updateRequest.getInfos().contains(info.getContent()));

            for (final String info : updateRequest.getInfos())
            {
                if (activity.getInfos().stream().anyMatch(entity -> entity.getContent().equals(info)))
                    continue;
                activity.getInfos().add(new ActivityInfoEntity(info));
            }
        }

        return ActivityModel.from(activity);
    }
}
