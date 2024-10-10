package dev.coms4156.project.individualproject;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
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
    // test adding to database
    public void testAddDocument() {
        CompletableFuture<Void> future = firebaseService.addDocument("interactions", "testDocument",
                testInteraction);
        future.join();
    }

    @Test
    @Order(2)
    // Test pulling document from database
    public void testGetDocument() {
        CompletableFuture<Map<String, Object>> future = firebaseService.getDocument("interactions",
                "testDocument");
        Map<String, Object> result = future.join();
        assertEquals(testInteraction, result);
    }

    @Test
    @Order(3)
    // test adding to database
    public void testRemoveDocument() {
        CompletableFuture<Void> future = firebaseService.removeDocument("interactions", "testDocument");
        future.join();
    }

}
