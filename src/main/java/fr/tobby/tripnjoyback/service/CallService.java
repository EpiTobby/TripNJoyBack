package fr.tobby.tripnjoyback.service;

import io.agora.media.RtcTokenBuilder;
import io.agora.media.RtcTokenBuilder.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CallService {
    String appId;
    String appCertificate;
    static int expirationTimeInSeconds = 3600;

    public CallService(@Value("${agora.app.id}") String appId, @Value("${agora.app.certificate}") String appCertificate) {
        this.appId = appId;
        this.appCertificate = appCertificate;
    }

    public String generateToken(String channelName, int uid) {
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        return tokenBuilder.buildTokenWithUid(appId, appCertificate, channelName, uid, Role.Role_Attendee, timestamp);
    }
}
