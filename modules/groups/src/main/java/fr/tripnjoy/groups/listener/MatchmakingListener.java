package fr.tripnjoy.groups.listener;

import fr.tripnjoy.groups.service.GroupService;
import fr.tripnjoy.profiles.dto.response.MatchMakingResult;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@RabbitListener(
        bindings = @QueueBinding(
                exchange = @Exchange("amq.topic"),
                key = "match",
                value = @Queue("match")))
@Component
public class MatchmakingListener {

    private final GroupService groupService;

    public MatchmakingListener(final GroupService groupService)
    {
        this.groupService = groupService;
    }

    @RabbitHandler
    public void onMatchmakingResult(MatchMakingResult matchMakingResult)
    {
        if (matchMakingResult.getType() == MatchMakingResult.Type.JOINED)
            groupService.addUserToPublicGroup(matchMakingResult.getGroupId(), matchMakingResult.getUserId(), matchMakingResult.getProfileId());
    }
}
