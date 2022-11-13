package fr.tobby.tripnjoyback.service;

import io.agora.media.RtcTokenBuilder;
import io.agora.media.RtcTokenBuilder.Role;
import org.springframework.stereotype.Service;

@Service
public class CallService {
    static String appId = "6cbc8e0499d64486aa09738b9c326c81";
    static String appCertificate = "472bedcb37df47a5b98ec1feccb8d01d";
    static int expirationTimeInSeconds = 3600; // The time after which the token expires

    public String generateToken(String channelName, int uid) {
        RtcTokenBuilder tokenBuilder = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = tokenBuilder.buildTokenWithUid(appId, appCertificate,
                channelName, uid, Role.Role_Attendee, timestamp);
        System.out.println("UID token:");
        System.out.println(result);
        return result;
    }
}
