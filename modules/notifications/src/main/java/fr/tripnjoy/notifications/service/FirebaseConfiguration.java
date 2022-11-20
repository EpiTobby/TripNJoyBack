package fr.tripnjoy.notifications.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "tripnjoy.notification.enable", havingValue = "true")
public class FirebaseConfiguration {

    @Bean
    FirebaseMessaging firebaseMessaging(@Value("${firebase.account.path}") String accountPath) throws IOException
    {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream(accountPath));
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "tripnjoy");

        return FirebaseMessaging.getInstance(app);
    }
}