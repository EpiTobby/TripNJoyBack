package fr.tripnjoy.planning.service;

import fr.tripnjoy.planning.dto.request.CreateActivityRequest;
import fr.tripnjoy.planning.dto.request.UpdateActivityRequest;
import fr.tripnjoy.planning.dto.response.ActivityResponse;
import fr.tripnjoy.planning.entity.ActivityEntity;
import fr.tripnjoy.planning.entity.ActivityInfoEntity;
import fr.tripnjoy.planning.entity.ActivityMemberEntity;
import fr.tripnjoy.planning.exception.ActivityNotFoundException;
import fr.tripnjoy.planning.repository.ActivityMemberRepository;
import fr.tripnjoy.planning.repository.ActivityRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PlanningService {

    private final ActivityRepository activityRepository;
    private final ActivityMemberRepository activityMemberRepository;

    public PlanningService(final ActivityRepository activityRepository,
                           final ActivityMemberRepository activityMemberRepository)
    {
        this.activityRepository = activityRepository;
        this.activityMemberRepository = activityMemberRepository;
    }

    public ActivityResponse createActivity(final long groupId, final CreateActivityRequest request)
    {
        ActivityEntity activity = new ActivityEntity(groupId,
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getColor(),
                request.getLocation(),
                request.getIcon());

        return activityRepository.save(activity).toDtoResponse();
    }

    public List<ActivityResponse> getGroupActivities(final long groupId)
    {
        return activityRepository.findAllByGroupIdOrderByStartDate(groupId)
                                 .stream()
                                 .map(ActivityEntity::toDtoResponse)
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
        ActivityMemberEntity member = new ActivityMemberEntity(new ActivityMemberEntity.Ids(activity, userId));
        member = activityMemberRepository.save(member);
        activity.getParticipants().add(member);
    }

    @Transactional
    public void leaveActivity(final long activityId, final long userId)
    {
        ActivityEntity activity = activityRepository.findById(activityId).orElseThrow(ActivityNotFoundException::new);
        activity.getParticipants().removeIf(member -> member.getIds().getParticipantId() == userId);
    }

    @NotNull
    @Transactional
    public ActivityResponse updateActivity(final long activityId, @NotNull final UpdateActivityRequest updateRequest)
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
        if (updateRequest.getParticipants() != null)
        {
            activity.getParticipants().removeIf(participant -> !updateRequest.getParticipants().contains(participant.getIds().getParticipantId()));
            updateRequest.getParticipants().stream()
                         .filter(participant -> activity.getParticipants().stream().noneMatch(entity -> entity.getIds().getParticipantId() == participant))
                         .forEach(participantId -> joinActivity(activityId, participantId));
        }

        return activity.toDtoResponse();
    }
}
