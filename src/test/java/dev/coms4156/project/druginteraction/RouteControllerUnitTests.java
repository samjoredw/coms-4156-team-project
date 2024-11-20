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

  private static final String AUTH_TOKEN = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IjkyODg2OGRjNDRlYTZhOThjODhiMzkzZDM2NDQ1MTM2NWViYjMwZDgiLCJ0eXAiOiJKV1QifQ.eyJuYW1lIjoiVG9tbXkgTGFtIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FDZzhvY0x4ZXE3WnlHbXNPOExLSkRpV0Y3NWFnb2hBSHd4TzM3c1o3ZjBsRGdFVjJHOWpYUT1zOTYtYyIsImlzcyI6Imh0dHBzOi8vc2VjdXJldG9rZW4uZ29vZ2xlLmNvbS9kcnVnLWludGVyYWN0aW9uLWFwaSIsImF1ZCI6ImRydWctaW50ZXJhY3Rpb24tYXBpIiwiYXV0aF90aW1lIjoxNzMyMDY3MjIzLCJ1c2VyX2lkIjoicThxeFZ6TGRLN2VtVjAwUHVoOWZGcmQyelhGMiIsInN1YiI6InE4cXhWekxkSzdlbVYwMFB1aDlmRnJkMnpYRjIiLCJpYXQiOjE3MzIwNjcyMjMsImV4cCI6MTczMjA3MDgyMywiZW1haWwiOiJ0c2wyMTM5QGNvbHVtYmlhLmVkdSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7Imdvb2dsZS5jb20iOlsiMTE3MDI2MzIzNDI0NTEyNzg5NTExIl0sImVtYWlsIjpbInRzbDIxMzlAY29sdW1iaWEuZWR1Il19LCJzaWduX2luX3Byb3ZpZGVyIjoiZ29vZ2xlLmNvbSJ9fQ.Qt0mYfc0q8mmyKOBV8OVY1E_648afFEA1XIU3hsDk0QVVip88fPBLzosqYgV4IrRPeyVLlocr4XBeQN_1mY9LvXBBLvjgC6P0-yLKJZZSYX1pZrXOa4XerTKGTBEdNknvCdCbQxwm_yM77lo6GUduveTDzOUMWrXf0doVyB183ZqbibE2erzLomiiVlcNhCaxDqYVkYk9fB-b3tc6x0iiE2UG4PX5ALCQJew5ohZK2uslG52_-tfXkLxCGAStHXtazJQ_MneCpZEh-3Q30uZJ5IC_FcdOSEMaJZcw6tmwj7QvFSdhHxnEP6rKNfHL6Q75rmg6BwuRKD5THOCXjvJBQ";

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

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Interaction added successfully", response.getBody());
  }

  @Test
    void testAddInteraction_AlreadyExists() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Existing interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("Existing interaction");

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect, AUTH_TOKEN);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Interaction already exists", response.getBody());
  }

}