package dev.coms4156.project.individualproject;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the FirebaseService and FirebaseConfig classes.
 */
@SpringBootTest(classes = MainApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirebaseServiceUnitTests {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private FirebaseService firebaseService;

    private static Map<String, Object> testInteraction;

    @BeforeAll
    public static void setupTesting() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        testInteraction = new HashMap<String, Object>();
        testInteraction.put("drugA", "Aspirin");
        testInteraction.put("drugB", "Warfarin");
        testInteraction.put("interactionEffect", "Increased risk of bleeding.");
        testInteraction.put("createdBy", "John Doe");
        testInteraction.put("createdAt", sdf.format(timestamp));
        testInteraction.put("updatedBy", "John Doe");
        testInteraction.put("updatedAt", sdf.format(timestamp));
    }

    @Test
    @Order(1)
    // test adding and pulling to database
    public void testAddDocument() {
        // Add the document to Firestore
        CompletableFuture<Void> future = firebaseService.addDocument("interactions", "testDocument", testInteraction);
        future.join();

        // Retrieve the document to verify it was added
        CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
        Map<String, Object> result = futureGet.join();

        // Assert that the document was added successfully
        assertEquals(testInteraction, result);
    }

    @Test
    @Order(2)
    // test adding to database
    public void testRemoveDocument() {
        // Remove the document from Firestore
        CompletableFuture<Void> future = firebaseService.removeDocument("interactions", "testDocument");
        future.join();

        // Attempt to retrieve the document to verify it was removed
        CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
        Map<String, Object> result = futureGet.join();

        // Assert that the document was removed successfully
        assertTrue(result == null || result.isEmpty());
    }

    @Test
    @Order(3)
    // Test updating a document in the database
    public void testUpdateDocument() {
        // Modify the interaction effect
        testInteraction.put("interactionEffect", "Moderate risk of bleeding.");

        // Update the document in Firestore
        CompletableFuture<Void> futureUpdate = firebaseService.addDocument("interactions", "testDocument",
                testInteraction);
        futureUpdate.join();

        // Retrieve the updated document
        CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
        Map<String, Object> updatedResult = futureGet.join();

        // Verify that the document was updated
        assertEquals(testInteraction, updatedResult);
    }

    @Test
    @Order(4)
    // Test checking if a document exists in the database
    public void testDocumentExists() {
        // Check if the document exists
        CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
        Map<String, Object> result = futureGet.join();

        // Verify that the document exists
        assertFalse(result.isEmpty());

        // Remove the document
        CompletableFuture<Void> futureRemove = firebaseService.removeDocument("interactions", "testDocument");
        futureRemove.join();

        // Check if the document still exists
        CompletableFuture<Map<String, Object>> futureGetAfterRemove = firebaseService.getDocument("interactions",
                "testDocument");
        Map<String, Object> resultAfterRemove = futureGetAfterRemove.join();

        // Verify that the document no longer exists
        assertTrue(resultAfterRemove == null || resultAfterRemove.isEmpty());
    }
}
