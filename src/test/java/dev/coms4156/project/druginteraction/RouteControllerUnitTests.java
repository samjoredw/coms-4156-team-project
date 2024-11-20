package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RouteControllerUnitTests {

  private static final String AUTH_TOKEN = "Bearer "
      + "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFlNTIxYmY1ZjdhNDAwOGMzYmQ3MjFmMzk2OTcwOWI1MzY0MzA5NjEiLCJ0"
      + "eXAiOiJKV1QifQ.eyJuYW1lIjoiQ2hhcmxpZSBab3UiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcm"
      + "NvbnRlbnQuY29tL2EvQUNnOG9jTExnN1JmOE5DNzYxbnhTWEhBbFQwalhwWHg3Vmo4bk1MQ3VuZHNDX0NJRWJTVmF"
      + "nPXM5Ni1jIiwiaXNzIjoiaHR0cHM6Ly9zZWN1cmV0b2tlbi5nb29nbGUuY29tL2RydWctaW50ZXJhY3Rpb24tYXBp"
      + "IiwiYXVkIjoiZHJ1Zy1pbnRlcmFjdGlvbi1hcGkiLCJhdXRoX3RpbWUiOjE3MzIxNDIyMjMsInVzZXJfaWQiOiJsV"
      + "Vhla3p6cFBnZUZQOFR5Z3F3dXVrMEd1ZUozIiwic3ViIjoibFVYZWt6enBQZ2VGUDhUeWdxd3V1azBHdWVKMyIsIm"
      + "lhdCI6MTczMjE0MjIyMywiZXhwIjoxNzMyMTQ1ODIzLCJlbWFpbCI6Imp6MzMzMUBjb2x1bWJpYS5lZHUiLCJlbWF"
      + "pbF92ZXJpZmllZCI6dHJ1ZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJnb29nbGUuY29tIjpbIjEwMjA2OTE0"
      + "MjQxMzMwNjAzMzM5OSJdLCJlbWFpbCI6WyJqejMzMzFAY29sdW1iaWEuZWR1Il19LCJzaWduX2luX3Byb3ZpZGVyI"
      + "joiZ29vZ2xlLmNvbSJ9fQ.IkIxquN_GKapDn2Oqfaa00sTmYpmiDhsmvEfA_SXb8oBJNy9kmeAef3c73DvscZ8sIE"
      + "QwvFzmD-pTkNYEBt_BC0Grkeo-oCbfhcf0AkuANkI1wwqp2GAm-u6W0mTk7yeD4hDYRcOzcKa6nOhIXorjAfqqX_8"
      + "h8hT9yDqU3kL1E9MnapurMAV1swjLp3P8oMkiyuF3HZL4FNc2EokwgEwCyp04glWecrYOjBnFgNDGngaob3cAmUgu"
      + "djem42fvfo8_ftQNxX1i1Xj5f2qAS4JDxyjFzl4H0Bv8WJfTP33auG769139dZ-RNdWS2wK-55pTaI_FIfsMRWTSv"
      + "IixO1Riw";

  private static final String userId = "lUXekzzpPgeFP8Tygqwuuk0GueJ3";

  @Mock
  private Interaction interactionService;

  @Mock
  private Drugs drugService;

  @InjectMocks
  private RouteController routeController;

  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    headers = new HttpHeaders();
    headers.add(HttpHeaders.AUTHORIZATION, AUTH_TOKEN);
  }

  @Test
  void testGetDrug_Success() {
    String drugName = "Aspirin";
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", drugName);
    when(drugService.getDrug(drugName)).thenReturn(drugInfo);

    ResponseEntity<?> response = routeController.getDrug(drugName, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugInfo, response.getBody());
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
  void testAddDrug_Success() {
    Map<String, Object> newDrug = new HashMap<>();
    newDrug.put("name", "NewDrug");
    when(drugService.getDrug("NewDrug")).thenReturn(null);
    when(drugService.addDrug(newDrug)).thenReturn(true);

    ResponseEntity<?> response = routeController.addDrug(newDrug, AUTH_TOKEN);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Drug added successfully", response.getBody());
  }

  @Test
  void testAddDrug_AlreadyExists() {
    Map<String, Object> existingDrug = new HashMap<>();
    existingDrug.put("name", "ExistingDrug");
    when(drugService.getDrug("ExistingDrug")).thenReturn(new HashMap<>());

    ResponseEntity<?> response = routeController.addDrug(existingDrug, AUTH_TOKEN);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Drug already exists", response.getBody());
  }

  @Test
  void testUpdateDrug_Success() {
    String drugName = "ExistingDrug";
    Map<String, Object> updates = new HashMap<>();
    updates.put("description", "Updated description");
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
  void testRemoveDrug_Success() {
    String drugName = "ExistingDrug";
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
  void testGetAllDrugs() {
    List<String> drugs = Arrays.asList("Drug1", "Drug2", "Drug3");
    when(drugService.getAllDrugs()).thenReturn(drugs);

    ResponseEntity<?> response = routeController.getAllDrugs(AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugs, response.getBody());
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
  void testGetInteraction_Found() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn(interactionEffect);

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB, AUTH_TOKEN);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    final Map<String, Object> expectedResponse = new HashMap<>();
    final Map<String, Object> interactionData = new HashMap<>();
    interactionData.put("drugPair", drugA + " and " + drugB);
    interactionData.put("interactionBool", true);
    interactionData.put("interactionEffect", interactionEffect);
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
    final Map<String, Object> expectedResponse = new HashMap<>();
    final Map<String, Object> interactionData = new HashMap<>();
    interactionData.put("drugPair", drugA + " and " + drugB);
    interactionData.put("interactionBool", false);
    interactionData.put("interactionEffect", noInteraction);
    expectedResponse.put("noInteractions", interactionData);
    assertEquals(expectedResponse, response.getBody());
  }

  @Test
  void testAddInteraction_Success() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "New interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("No known interaction");
    when(interactionService.addInteraction(drugA, drugB, interactionEffect)).thenReturn(true);

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect,
        AUTH_TOKEN);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Interaction added successfully", response.getBody());
  }

  @Test
  void testAddInteraction_AlreadyExists() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Existing interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("Existing interaction");

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect,
        AUTH_TOKEN);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Interaction already exists", response.getBody());
  }

}