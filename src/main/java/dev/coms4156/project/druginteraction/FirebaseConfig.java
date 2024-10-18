package dev.coms4156.project.druginteraction;

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
        FirebaseOptions options;
        String firebaseConfig = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfig != null && !firebaseConfig.isEmpty()) {
            byte[] decodedConfig = Base64.getDecoder().decode(firebaseConfig);
            InputStream serviceAccount = new ByteArrayInputStream(decodedConfig);
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } else {
            try (FileInputStream serviceAccount = new FileInputStream("./firebase_config.json")) {
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }
        }

        return FirebaseApp.initializeApp(options);
    }

    /**
     * Creates a firestore instance.
     *
     * @param firebaseApp a FirebaseApp object
     *
     * @return a Firestore object
     */
    @Bean
    public Firestore firestore(FirebaseApp firebaseApp) {
        try {
            if (System.getenv("CI") != null) {
                return com.google.firebase.cloud.FirestoreClient.getFirestore(firebaseApp);
            } else {
                return FirestoreOptions.getDefaultInstance().getService();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize Firestore", e);
        }
    }
}