package dev.coms4156.project.individualproject;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.FirestoreOptions;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handles the configuration details of the
 * database.
 */
@Configuration
public class FirebaseConfig {

    /**
     * Configures the firebase DB.
     *
     * @return A FirebaseApp object
     * @throws IOException when the config file does not exist.
     */
    @Bean
    public FirebaseApp firebaseApp() throws IOException {

        try (FileInputStream serviceAccount = new FileInputStream("./firebase_config.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        return FirestoreOptions.getDefaultInstance().getService();
    }
}
