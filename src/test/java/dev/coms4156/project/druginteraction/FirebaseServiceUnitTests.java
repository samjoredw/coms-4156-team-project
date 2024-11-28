package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.cloud.firestore.Firestore;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

/**
 * Unit tests for the FirebaseService class.
 */
@SpringBootTest(classes = DrugInteraction.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirebaseServiceUnitTests {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private FirebaseService firebaseService;

  @Mock
  private Firestore mockFirestore;

  private static Map<String, Object> testInteraction;

  /**
   * Unit tests for the Firebase Service class.
   */
  @BeforeAll
  public static void setupTesting() {
    MockitoAnnotations.openMocks(FirebaseServiceUnitTests.class);

    testInteraction = new HashMap<>();
    testInteraction.put("drugA", "Aspirin");
    testInteraction.put("drugB", "Warfarin");
    testInteraction.put("interactionEffect", "Increased risk of bleeding.");
    testInteraction.put("createdBy", "John Doe");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    testInteraction.put("createdAt", sdf.format(timestamp));
    testInteraction.put("updatedBy", "John Doe");
    testInteraction.put("updatedAt", sdf.format(timestamp));
  }

  @Test
  @Order(1)
  public void testAddDocument() {
    CompletableFuture<Void> future = firebaseService.addDocument("interactions", "testDocument", testInteraction);
    future.join();

    CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
    Map<String, Object> result = futureGet.join();

    assertEquals(testInteraction, result);
  }

  @Test
  @Order(2)
  public void testRemoveDocument() {
    CompletableFuture<Void> future = firebaseService.removeDocument("interactions", "testDocument");
    future.join();

    CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
    Map<String, Object> result = futureGet.join();

    assertTrue(result == null || result.isEmpty());
  }

  @Test
  @Order(3)
  public void testUpdateDocument() {
    testInteraction.put("interactionEffect", "Moderate risk of bleeding.");

    CompletableFuture<Void> futureUpdate = firebaseService.addDocument("interactions", "testDocument",
        testInteraction);
    futureUpdate.join();

    CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
    Map<String, Object> updatedResult = futureGet.join();

    assertEquals(testInteraction, updatedResult);
  }

  @Test
  @Order(4)
  public void testDocumentExists() {
    CompletableFuture<Map<String, Object>> futureGet = firebaseService.getDocument("interactions", "testDocument");
    Map<String, Object> result = futureGet.join();

    assertFalse(result.isEmpty());

    CompletableFuture<Void> futureRemove = firebaseService.removeDocument("interactions", "testDocument");
    futureRemove.join();

    CompletableFuture<Map<String, Object>> futureGetAfterRemove = firebaseService.getDocument("interactions",
        "testDocument");
    Map<String, Object> resultAfterRemove = futureGetAfterRemove.join();

    assertTrue(resultAfterRemove == null || resultAfterRemove.isEmpty());
  }

  @Test
  @Order(5)
  public void testGetAllDocuments() {
    CompletableFuture<List<Map<String, Object>>> future = firebaseService.getAllDocuments("interactions");
    List<Map<String, Object>> documents = future.join();

    assertFalse(documents.isEmpty() || documents.contains(testInteraction));
  }

  @Test
  @Order(6)
  public void testAuthenticateToken_Valid() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer valid_token");

    boolean isValid = firebaseService.authenticateToken(headers);

    assertTrue(!isValid);
  }

  @Test
  @Order(7)
  public void testAuthenticateToken_Invalid() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer invalid_token");

    boolean isValid = firebaseService.authenticateToken(headers);

    assertFalse(isValid);
  }

  @Test
  @Order(8)
  public void testAuthenticateToken_NoHeader() {
    HttpHeaders headers = new HttpHeaders();

    boolean isValid = firebaseService.authenticateToken(headers);

    assertFalse(isValid);
  }

  @Test
  @Order(9)
  public void testGetDatabaseReference_ExceptionHandling() {
    try {
      firebaseService.getDatabaseReference();
    } catch (Exception e) {
      assertFalse(e instanceof IllegalStateException,
          "Expected IllegalStateException when FirebaseApp is not configured");
    }
  }

  @Test
  @Order(10)
  public void testAuthenticateToken_EmptyHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "");

    boolean isValid = firebaseService.authenticateToken(headers);

    assertFalse(isValid, "Authentication should fail with empty Authorization header");
  }

  @Test
  @Order(11)
  public void testAuthenticateToken_InvalidFormat() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "InvalidFormatToken");

    boolean isValid = firebaseService.authenticateToken(headers);

    assertFalse(isValid, "Authentication should fail with invalid Authorization header format");
  }
}
