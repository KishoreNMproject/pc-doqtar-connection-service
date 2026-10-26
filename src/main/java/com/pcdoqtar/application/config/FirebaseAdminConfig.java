package com.pcdoqtar.application.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initializes the Firebase Admin SDK using deployment-provided credentials.
 *
 * Local development can use GOOGLE_APPLICATION_CREDENTIALS or Google ADC.
 * Hosted environments may provide FIREBASE_SERVICE_ACCOUNT_JSON as a secret.
 * Never commit a service-account JSON file or private key to source control.
 */
@Configuration
public class FirebaseAdminConfig {

    @Bean
    public FirebaseApp firebaseApp(
            @Value("${firebase.project-id:pc-doqtar}") String projectId)
            throws IOException {

        String serviceAccountJson = System.getenv("FIREBASE_SERVICE_ACCOUNT_JSON");
        GoogleCredentials credentials;

        if (serviceAccountJson != null && !serviceAccountJson.isBlank()) {
            credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)));
        } else {
            credentials = GoogleCredentials.getApplicationDefault();
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();

        return FirebaseApp.initializeApp(options, "pc-doqtar-server");
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
