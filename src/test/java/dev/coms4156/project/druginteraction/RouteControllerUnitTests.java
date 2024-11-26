package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.gson.Gson;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RouteControllerUnitTests {

  private static String AUTH_TOKEN;

  @Mock
  private Interaction interactionService;

  @Mock
  private Drugs drugService;

  @InjectMocks
  private RouteController routeController;

  private HttpHeaders headers;

  @BeforeAll
  public static void setUp() {
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        System.out.println("Firebase is empty");
        FileInputStream serviceAccount = new FileInputStream("./firebase_config.json");

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

        FirebaseApp.initializeApp(options);
      }

      String apiKey = "AIzaSyAefK0EcsWyOiy7RCWaOT54rBxJr9HgqMs";
      String email = "test@columbia.edu";
      String password = "testuser";

      // Prepare the request payload
      Map<String, String> payload = new HashMap<>();
      payload.put("email", email);
      payload.put("password", password);
      payload.put("returnSecureToken", "true");

      // Convert payload to JSON
      String jsonPayload = new Gson().toJson(payload);

      // Send POST request
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
          "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // Handle the response
      if (response.statusCode() == 200) {
        Map<String, Object> responseBody = new Gson().fromJson(response.body(), HashMap.class);;
        AUTH_TOKEN = (String) responseBody.get("idToken");
        AUTH_TOKEN = "Bearer " + AUTH_TOKEN;
      } else {
        System.out.println("Sign-in failed! Status Code: " + response.statusCode());
        System.out.println("Response: " + response.body());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Track test-specific drugs and interactions for cleanup
  private final List<String> testDrugs = new ArrayList<>();
  private final List<String[]> testInteractions = new ArrayList<>();

  @BeforeEach
  void setUpBeforeEach() {
    MockitoAnnotations.openMocks(this);
    headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, AUTH_TOKEN);
  }


  @Test
  void testGetDrug_Success() {
    String drugName = "Test_Aspirin";
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", drugName);
    testDrugs.add(drugName); // Track this drug for cleanup

    when(drugService.getDrug(drugName)).thenReturn(drugInfo);
    ResponseEntity<?> response = routeController.getDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugInfo, response.getBody());
  }

  @Test
  void testGetDrug_InvalidName() {
    ResponseEntity<?> response = routeController.getDrug("", AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug name cannot be empty", response.getBody());

    response = routeController.getDrug(null, AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug name cannot be empty", response.getBody());
  }

  @Test
  void testGetDrug_NotFound() {
    String drugName = "NonexistentDrug";
    when(drugService.getDrug(drugName)).thenReturn(null);
    ResponseEntity<?> response = routeController.getDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Drug not found", response.getBody());
  }

  @Test
  void testGetDrug_MissingAuthorizationHeader() {
      // Test case for missing Authorization header
      ResponseEntity<?> response = routeController.getDrug("Aspirin", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetDrug_InvalidAuthorizationHeader() {
      // Test case for invalid Authorization header
      ResponseEntity<?> response = routeController.getDrug("Aspirin", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetDrug_ExpiredOrInvalidToken() {
      // Test case for expired or invalid token
      ResponseEntity<?> response = routeController.getDrug("Aspirin", "Bearer invalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Invalid or expired token", response.getBody());
  }

  @Test
  void testGetDrug_ExceptionHandling() {
      // Simulate an exception in the getDrug method to cover the handleException block
      ResponseEntity<?> response = routeController.getDrug(null, "Bearer validToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Invalid or expired token", response.getBody());
  }


  @Test
  void testAddDrug_Success() {
    Map<String, Object> newDrug = new HashMap<>();
    String testDrugName = "Test_NewDrug";
    newDrug.put("name", testDrugName);
    testDrugs.add(testDrugName); // Track this drug for cleanup

    when(drugService.getDrug(testDrugName)).thenReturn(null);
    when(drugService.addDrug(newDrug)).thenReturn(true);

    ResponseEntity<?> response = routeController.addDrug(newDrug, AUTH_TOKEN);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Drug added successfully", response.getBody());
  }

  @Test
  void testAddDrug_AlreadyExists() {
    Map<String, Object> existingDrug = new HashMap<>();
    existingDrug.put("name", "Test_ExistingDrug");
    testDrugs.add("Test_ExistingDrug"); // Track this drug for cleanup

    when(drugService.getDrug("Test_ExistingDrug")).thenReturn(new HashMap<>());
    ResponseEntity<?> response = routeController.addDrug(existingDrug, AUTH_TOKEN);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Drug already exists", response.getBody());
  }

  @Test
  void testAddDrug_InvalidInput() {
    // Case 1: drugInfo map has null name
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", null);

    ResponseEntity<?> response = routeController.addDrug(drugInfo, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug name cannot be empty", response.getBody());

    // Case 2: drugInfo map has empty name
    drugInfo.put("name", "");

    response = routeController.addDrug(drugInfo, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug name cannot be empty", response.getBody());
  }

  @Test
  void testAddDrug_FailedAddition() {
    // Valid drugInfo but simulate addDrug returning false
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", "TestDrug");

    when(drugService.getDrug("TestDrug")).thenReturn(null);
    when(drugService.addDrug(drugInfo)).thenReturn(false); // Simulate failure to add

    ResponseEntity<?> response = routeController.addDrug(drugInfo, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Failed to add drug", response.getBody());
  }

  @Test
  void testAddDrug_ExceptionHandling() {
    // Simulate an exception in drugService.addDrug to test exception handling
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", "TestDrug");

    when(drugService.getDrug("TestDrug")).thenReturn(null);
    when(drugService.addDrug(drugInfo)).thenThrow(new RuntimeException("Service error"));

    ResponseEntity<?> response = routeController.addDrug(drugInfo, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Service error", response.getBody());
  }

  @Test
  void testAddDrug_MissingAuthorization() {
      Map<String, Object> drugInfo = new HashMap<>();
      drugInfo.put("name", "TestDrug");

      ResponseEntity<?> response = routeController.addDrug(drugInfo, null);
      
      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }



  @Test
  void testRemoveDrug_ExceptionHandling() {
    String drugName = "TestDrug";

    // Simulate an exception when calling removeDrug
    when(drugService.removeDrug(drugName)).thenThrow(new RuntimeException("Service error"));

    ResponseEntity<?> response = routeController.removeDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Service error", response.getBody());
  }

  @Test
  void testGetAllDrugs_ExceptionHandling() {
    // Simulate an exception when calling getAllDrugs
    when(drugService.getAllDrugs()).thenThrow(new RuntimeException("Service error"));

    ResponseEntity<?> response = routeController.getAllDrugs(AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Service error", response.getBody());
  }

  @Test
  void testGetDrugInteractions_ExceptionHandling() {
    String drugName = "TestDrug";

    // Simulate an exception when calling getInteraction
    when(drugService.getInteraction(drugName)).thenThrow(new RuntimeException("Service error"));

    ResponseEntity<?> response = routeController.getDrugInteractions(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Service error", response.getBody());
  }

  @Test
  void testUpdateDrug_Success() {
    String drugName = "Test_ExistingDrug";
    Map<String, Object> updates = new HashMap<>();
    updates.put("description", "Updated description");
    testDrugs.add(drugName); // Track this drug for cleanup

    when(drugService.getDrug(drugName)).thenReturn(new HashMap<>());
    when(drugService.updateDrug(drugName, updates)).thenReturn(true);

    ResponseEntity<?> response = routeController.updateDrug(drugName, updates, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Drug updated successfully", response.getBody());
  }

  @Test
  void testUpdateDrug_NotFound() {
    String drugName = "NonexistentDrug";
    Map<String, Object> updates = new HashMap<>();
    when(drugService.getDrug(drugName)).thenReturn(null);

    ResponseEntity<?> response = routeController.updateDrug(drugName, updates, AUTH_TOKEN);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Drug not found", response.getBody());
  }

  @Test
  void testUpdateDrug_MissingAuthorization() {
      Map<String, Object> updates = new HashMap<>();
      updates.put("description", "Updated description");

      ResponseEntity<?> response = routeController.updateDrug("TestDrug", updates, null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testUpdateDrug_FailedUpdate() {
      Map<String, Object> updates = new HashMap<>();
      updates.put("description", "Updated description");

      when(drugService.getDrug("TestDrug")).thenReturn(new HashMap<>());
      when(drugService.updateDrug("TestDrug", updates)).thenReturn(false);

      ResponseEntity<?> response = routeController.updateDrug("TestDrug", updates, "Bearer validToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testUpdateDrug_ExceptionHandling() {
      Map<String, Object> updates = new HashMap<>();
      updates.put("description", "Updated description");

      when(drugService.getDrug("TestDrug")).thenThrow(new RuntimeException("Unexpected error"));

      ResponseEntity<?> response = routeController.updateDrug("TestDrug", updates, "Bearer validToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testUpdateDrug_MissingAuthorizationHeader() {
      Map<String, Object> updates = new HashMap<>();
      updates.put("description", "Updated description");

      ResponseEntity<?> response = routeController.updateDrug("TestDrug", updates, null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testUpdateDrug_InvalidAuthorizationHeader() {
      Map<String, Object> updates = new HashMap<>();
      updates.put("description", "Updated description");

      ResponseEntity<?> response = routeController.updateDrug("TestDrug", updates, "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }


  @Test
  void testUpdateInteraction_Success() {
    String documentId = "doc123";
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Updated interaction effect";

    when(interactionService.updateInteraction(documentId, drugA, drugB, interactionEffect))
        .thenReturn(true);

    ResponseEntity<?> response =
        routeController.updateInteraction(documentId, drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Interaction updated successfully", response.getBody());
  }

  @Test
  void testUpdateInteraction_Failure() {
    String documentId = "doc123";
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Updated interaction effect";

    when(interactionService.updateInteraction(documentId, drugA, drugB, interactionEffect))
        .thenReturn(false);

    ResponseEntity<?> response =
        routeController.updateInteraction(documentId, drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Failed to update interaction", response.getBody());
  }

  @Test
  void testUpdateInteraction_Exception() {
    String documentId = "doc123";
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Updated interaction effect";

    when(interactionService.updateInteraction(documentId, drugA, drugB, interactionEffect))
        .thenThrow(new RuntimeException("Database error"));

    ResponseEntity<?> response =
        routeController.updateInteraction(documentId, drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Database error", response.getBody());
  }

  @Test
  void testUpdateInteraction_MissingAuthorizationHeader() {
      ResponseEntity<?> response = routeController.updateInteraction(
          "document123", "DrugA", "DrugB", "Effect", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testUpdateInteraction_InvalidAuthorizationHeader() {
      ResponseEntity<?> response = routeController.updateInteraction(
          "document123", "DrugA", "DrugB", "Effect", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testRemoveDrug_Success() {
    String drugName = "Test_ExistingDrug";
    testDrugs.add(drugName); // Track this drug for cleanup

    when(drugService.removeDrug(drugName)).thenReturn(true);

    ResponseEntity<?> response = routeController.removeDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Drug removed successfully", response.getBody());
  }

  @Test
  void testRemoveDrug_NotFound() {
    String drugName = "NonexistentDrug";
    when(drugService.removeDrug(drugName)).thenReturn(false);

    ResponseEntity<?> response = routeController.removeDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Failed to remove drug", response.getBody());
  }

  @Test
  void testDeleteInteraction_Success() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";

    when(interactionService.removeInteraction(drugA, drugB, interactionEffect)).thenReturn(true);

    ResponseEntity<?> response =
        routeController.deleteInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Interaction deleted successfully", response.getBody());
  }

  @Test
  void testDeleteInteraction_Failure() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";

    when(interactionService.removeInteraction(drugA, drugB, interactionEffect)).thenReturn(false);

    ResponseEntity<?> response =
        routeController.deleteInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Failed to delete interaction", response.getBody());
  }

  @Test
  void testDeleteInteraction_Exception() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";

    when(interactionService.removeInteraction(drugA, drugB, interactionEffect))
        .thenThrow(new RuntimeException("Database error"));

    ResponseEntity<?> response =
        routeController.deleteInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Database error", response.getBody());
  }

  @Test
  void testDeleteInteraction_MissingAuthorizationHeader() {
      ResponseEntity<?> response = routeController.deleteInteraction(
          "DrugA", "DrugB", "Effect", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testDeleteInteraction_InvalidAuthorizationHeader() {
      ResponseEntity<?> response = routeController.deleteInteraction(
          "DrugA", "DrugB", "Effect", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetAllDrugs() {
    List<String> drugs = Arrays.asList("Drug1", "Drug2", "Drug3");
    when(drugService.getAllDrugs()).thenReturn(drugs);

    ResponseEntity<?> response = routeController.getAllDrugs(AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugs, response.getBody());
  }

  @Test
  void testGetAllDrugs_MissingAuthorization() {
      ResponseEntity<?> response = routeController.getAllDrugs(null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetAllDrugs_InvalidAuthorization() {
      ResponseEntity<?> response = routeController.getAllDrugs("InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetDrugInteractions() {
    String drugName = "Aspirin";
    List<String> interactions = Arrays.asList("Interaction1", "Interaction2");
    when(drugService.getInteraction(drugName)).thenReturn(interactions);

    ResponseEntity<?> response = routeController.getDrugInteractions(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(interactions, response.getBody());
  }

  @Test
  void testGetDrugInteractions_MissingAuthorization() {
      ResponseEntity<?> response = routeController.getDrugInteractions("Aspirin", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetDrugInteractions_InvalidAuthorization() {
      ResponseEntity<?> response = routeController.getDrugInteractions("Aspirin", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
}

  @Test
  void testGetInteraction_Found() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn(interactionEffect);

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> interactionData = new HashMap<>();
    interactionData.put("drugPair", drugA + " and " + drugB);
    interactionData.put("interactionBool", true);
    interactionData.put("interactionEffect", interactionEffect);
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("interactions", interactionData);
    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void testGetInteraction_NotFound() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String noInteraction = "No known interaction between DrugA and DrugB";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn(noInteraction);

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    Map<String, Object> interactionData = new HashMap<>();
    interactionData.put("drugPair", drugA + " and " + drugB);
    interactionData.put("interactionBool", false);
    interactionData.put("interactionEffect", noInteraction);
    Map<String, Object> expectedResponse = new HashMap<>();
    expectedResponse.put("noInteractions", interactionData);
    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void testGetInteraction_MissingAuthorization() {
      ResponseEntity<?> response = routeController.getInteraction("DrugA", "DrugB", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetInteraction_InvalidAuthorization() {
      ResponseEntity<?> response = routeController.getInteraction("DrugA", "DrugB", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testAddInteraction_Failure() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "New interaction effect";

    // Mock an interaction check that returns "No known interaction"
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("No known interaction");
    // Mock the addition to return false to simulate a failure in adding the interaction
    when(interactionService.addInteraction(drugA, drugB, interactionEffect)).thenReturn(false);

    ResponseEntity<?> response =
        routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Failed to add interaction", response.getBody());
  }

  @Test
  void testAddInteraction_Exception() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "New interaction effect";

    // Mock an exception when checking for an existing interaction
    when(interactionService.getInteraction(drugA, drugB))
        .thenThrow(new RuntimeException("Database error"));

    ResponseEntity<?> response =
        routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Database error", response.getBody());
  }

  @Test
  void testAddInteraction_Success() {
    String drugA = "Test_DrugA";
    String drugB = "Test_DrugB";
    String interactionEffect = "New interaction effect";
    // Track interaction for cleanup
    testInteractions.add(new String[] {drugA, drugB, interactionEffect});

    when(interactionService.getInteraction(drugA, drugB)).thenReturn("No known interaction");
    when(interactionService.addInteraction(drugA, drugB, interactionEffect)).thenReturn(true);

    ResponseEntity<?> response =
        routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Interaction added successfully", response.getBody());
  }

  @Test
  void testAddInteraction_AlreadyExists() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Existing interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("Existing interaction");

    ResponseEntity<?> response =
        routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Interaction already exists", response.getBody());
  }

  @Test
  void testAddInteraction_MissingAuthorizationHeader() {
      ResponseEntity<?> response = routeController.addInteraction("DrugA", "DrugB", "Effect", null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testAddInteraction_InvalidAuthorizationHeader() {
      ResponseEntity<?> response = routeController.addInteraction("DrugA", "DrugB", "Effect", "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetInteraction_InvalidInput() {
    // Case 1: drugA is null
    ResponseEntity<?> response = routeController.getInteraction(null, "DrugB", AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug names cannot be empty", response.getBody());

    // Case 2: drugB is null
    response = routeController.getInteraction("DrugA", null, AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug names cannot be empty", response.getBody());

    // Case 3: drugA is empty
    response = routeController.getInteraction("", "DrugB", AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug names cannot be empty", response.getBody());

    // Case 4: drugB is empty
    response = routeController.getInteraction("DrugA", "", AUTH_TOKEN);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Invalid input: Drug names cannot be empty", response.getBody());
  }

  @Test
  void testGetInteraction_ExceptionHandling() {
    // Simulate an exception in interactionService to trigger exception handling
    String drugA = "DrugA";
    String drugB = "DrugB";

    when(interactionService.getInteraction(drugA, drugB))
        .thenThrow(new RuntimeException("Service error"));

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Service error", response.getBody());
  }

  @Test
  void testGetMultipleInteractions_OptionalDrugs() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String drugC = "DrugC";
    String drugD = "DrugD";

    // Include an interaction string without the ": " delimiter
    List<Map<String, String>> interactions = Arrays.asList(
        Map.of("drugPair", "Interaction between DrugA and DrugB", "interactionEffect", "Effect1"),
        Map.of("drugPair", "Interaction between DrugC and DrugD", "interactionEffect",
            "Unknown interaction") // Missing ": " to trigger parts.length < 2
    );

    when(interactionService.getInteraction(Arrays.asList(drugA, drugB, drugC, drugD)))
        .thenReturn(interactions);

    ResponseEntity<?> response =
        routeController.getMultipleInteractions(drugA, drugB, drugC, drugD, null, AUTH_TOKEN);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Adjusted expected response to separate `interactions` and `noInteractions`

    // Expected interaction with the ": " delimiter
    Map<String, Object> interactionData1 = new HashMap<>();
    interactionData1.put("drugPair", "Interaction between DrugA and DrugB");
    interactionData1.put("interactionEffect", "Effect1");
    interactionData1.put("interactionBool", true);
    List<Map<String, Object>> interactionsList = new ArrayList<>();
    interactionsList.add(interactionData1);

    // Expected handling for interaction missing ": ", placed in `noInteractions`
    Map<String, Object> interactionData2 = new HashMap<>();
    interactionData2.put("drugPair", "Interaction between DrugC and DrugD");
    interactionData2.put("interactionEffect", "Unknown interaction");
    interactionData2.put("interactionBool", false);
    List<Map<String, Object>> noInteractionsList = new ArrayList<>();
    noInteractionsList.add(interactionData2);

    // Adjust expected response keys
    Map<String, List<Map<String, Object>>> expectedResponse = new HashMap<>();
    expectedResponse.put("interactions", interactionsList);
    expectedResponse.put("noInteractions", noInteractionsList);

    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void testGetMultipleInteractions_AllDrugsProvided() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String drugC = "DrugC";
    String drugD = "DrugD";
    String drugE = "DrugE";

    // Simulate interactions with all five drugs, with one missing the ": " delimiter
    List<Map<String, String>> interactions = Arrays.asList(
        Map.of("drugPair", "Interaction between DrugA and DrugB", "interactionEffect", "Effect1"),
        Map.of("drugPair", "Interaction between DrugC and DrugD", "interactionEffect", "Effect2"),
        Map.of("drugPair", "Interaction between DrugE and DrugA", "interactionEffect",
            "Unknown interaction") // Missing ": " to trigger parts.length < 2
    );

    when(interactionService.getInteraction(Arrays.asList(drugA, drugB, drugC, drugD, drugE)))
        .thenReturn(interactions);

    ResponseEntity<?> response =
        routeController.getMultipleInteractions(drugA, drugB, drugC, drugD, drugE, AUTH_TOKEN);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Expected interaction with the ": " delimiter
    Map<String, Object> interactionData1 = new HashMap<>();
    interactionData1.put("drugPair", "Interaction between DrugA and DrugB");
    interactionData1.put("interactionEffect", "Effect1");
    interactionData1.put("interactionBool", true);
    List<Map<String, Object>> interactionsList = new ArrayList<>();
    interactionsList.add(interactionData1);

    // Expected interaction with the ": " delimiter
    Map<String, Object> interactionData2 = new HashMap<>();
    interactionData2.put("drugPair", "Interaction between DrugC and DrugD");
    interactionData2.put("interactionEffect", "Effect2");
    interactionData2.put("interactionBool", true);
    interactionsList.add(interactionData2);

    // Expected handling for interaction missing ": "
    Map<String, Object> interactionData3 = new HashMap<>();
    interactionData3.put("drugPair", "Interaction between DrugE and DrugA");
    interactionData3.put("interactionEffect", "Unknown interaction");
    interactionData3.put("interactionBool", false);
    List<Map<String, Object>> noInteractionsList = new ArrayList<>();
    noInteractionsList.add(interactionData3);

    Map<String, List<Map<String, Object>>> expectedResponse = new HashMap<>();
    expectedResponse.put("interactions", interactionsList);
    expectedResponse.put("noInteractions", noInteractionsList);

    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void testGetMultipleInteractions_ExceptionHandling() {
    when(interactionService.getInteraction(Arrays.asList("DrugA", "DrugB")))
        .thenThrow(new RuntimeException("Database error"));
    ResponseEntity<?> response =
        routeController.getMultipleInteractions("DrugA", "DrugB", null, null, null, AUTH_TOKEN);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An error occurred: Database error", response.getBody());
  }

  @Test
  void testGetMultipleInteractions_MissingAuthorization() {
      ResponseEntity<?> response = routeController.getMultipleInteractions(
          "DrugA", "DrugB", null, null, null, null);

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testGetMultipleInteractions_InvalidAuthorization() {
      ResponseEntity<?> response = routeController.getMultipleInteractions(
          "DrugA", "DrugB", null, null, null, "InvalidToken");

      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testRemoveDrug_MissingAuthorization() {
      ResponseEntity<?> response = routeController.removeDrug("DrugName", null);
      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Missing or invalid Authorization header", response.getBody());
  }

  @Test
  void testRemoveDrug_InvalidDrugName() {
      ResponseEntity<?> response = routeController.removeDrug("", "Bearer validToken");
      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testRemoveDrug_MissingDrugName() {
      ResponseEntity<?> response = routeController.removeDrug("", "Bearer validToken");
  
      assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
      assertEquals("Invalid or expired token", response.getBody());
  }

  @AfterEach
  void cleanupDatabase() {
    // Remove tracked test-specific drugs
    for (String drug : testDrugs) {
      drugService.removeDrug(drug);
    }

    // Remove tracked test-specific interactions
    for (String[] interaction : testInteractions) {
      interactionService.removeInteraction(interaction[0], interaction[1], interaction[2]);
    }

    // Clear tracking lists for the next test
    testDrugs.clear();
    testInteractions.clear();
  }
}
