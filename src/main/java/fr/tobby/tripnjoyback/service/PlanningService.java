package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.entity.ActivityEntity;
import fr.tobby.tripnjoyback.entity.GroupEntity;
import fr.tobby.tripnjoyback.exception.GroupNotFoundException;
import fr.tobby.tripnjoyback.model.request.CreateActivityRequest;
import fr.tobby.tripnjoyback.model.response.ActivityModel;
import fr.tobby.tripnjoyback.repository.ActivityRepository;
import fr.tobby.tripnjoyback.repository.GroupRepository;
import org.springframework.stereotype.Service;

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
        ActivityEntity activity = new ActivityEntity(group, request.name(), request.description(), request.startDate(), request.endDate());

        return ActivityModel.from(activityRepository.save(activity));
    }
}
