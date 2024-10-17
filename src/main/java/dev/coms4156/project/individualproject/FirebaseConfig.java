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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        Logger logger = LoggerFactory.getLogger(getClass());

        FirebaseOptions options;
        String firebaseConfig = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfig != null && !firebaseConfig.isEmpty()) {
            logger.info("FIREBASE_CONFIG environment variable found. Using it for configuration.");
            logger.info("CONFIGCONFIG: "+firebaseConfig);
            byte[] decodedConfig = Base64.getDecoder().decode(firebaseConfig);
            InputStream serviceAccount = new ByteArrayInputStream(decodedConfig);

            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        } else {
            logger.info("FIREBASE_CONFIG environment variable not found. Attempting to use firebase_config.json file.");

            try (FileInputStream serviceAccount = new FileInputStream("./firebase_config.json")) {
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                logger.info("Successfully loaded configuration from firebase_config.json file.");
            } catch (IOException e) {
                logger.error("Failed to load Firebase configuration from file.", e);
                throw e;
            }
        }

        FirebaseApp app = FirebaseApp.initializeApp(options);
        logger.info("Firebase application initialized successfully.");
        return app;
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
        Logger logger = LoggerFactory.getLogger(getClass());
        try {
            Firestore db = com.google.firebase.cloud.FirestoreClient.getFirestore(firebaseApp);
            logger.info("Firestore instance created successfully");
            return db;
        } catch (Exception e) {
            logger.error("Failed to create Firestore instance", e);
            throw new RuntimeException("Could not initialize Firestore", e);
        }
    }
}
