package fr.tobby.tripnjoyback.service;

import fr.tobby.tripnjoyback.model.GroupModel;
import fr.tobby.tripnjoyback.notification.INotificationService;
import io.agora.media.RtcTokenBuilder;
import io.agora.media.RtcTokenBuilder.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CallService {
    private final GroupService groupService;
    private final INotificationService notificationService;
    String appId;
    String appCertificate;
    static int expirationTimeInSeconds = 3600;

    public CallService(final GroupService groupService, final INotificationService notificationService, @Value("${agora.app.id}") String appId, @Value("${agora.app.certificate}") String appCertificate) {
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.appId = appId;
        this.appCertificate = appCertificate;
    }

    public String generateToken(String channelName, int uid) {
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        return tokenBuilder.buildTokenWithUid(appId, appCertificate, channelName, uid, Role.Role_Attendee, timestamp);
    }

    public void sendGroupCallNotification(long groupId, String userName) {
        groupService.getGroup(groupId).map(GroupModel::getMembers).ifPresent(users ->
                users.forEach(user ->
                        notificationService.sendToToken(
                                user.getFirebaseToken(),
                                "tripnjoy_call",
                                userName + " a commencé un appel de groupe",
                                Map.of("groupId", String.valueOf(groupId),
                                        "user", String.valueOf(user.getId())))));
    }
}
