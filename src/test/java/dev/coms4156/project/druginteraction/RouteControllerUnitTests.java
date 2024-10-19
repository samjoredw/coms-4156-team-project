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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RouteControllerUnitTests {

  @Mock
    private Interaction interactionService;

  @Mock
    private Drugs drugService;

  @InjectMocks
    private RouteController routeController;

  @BeforeEach
    void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
    void testGetDrug_Success() {
    String drugName = "Aspirin";
    Map<String, Object> drugInfo = new HashMap<>();
    drugInfo.put("name", drugName);
    when(drugService.getDrug(drugName)).thenReturn(drugInfo);

    ResponseEntity<?> response = routeController.getDrug(drugName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugInfo, response.getBody());
  }

  @Test
    void testGetDrug_NotFound() {
    String drugName = "NonexistentDrug";
    when(drugService.getDrug(drugName)).thenReturn(null);

    ResponseEntity<?> response = routeController.getDrug(drugName);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Drug not found", response.getBody());
  }

  @Test
    void testAddDrug_Success() {
    Map<String, Object> newDrug = new HashMap<>();
    newDrug.put("name", "NewDrug");
    when(drugService.getDrug("NewDrug")).thenReturn(null);
    when(drugService.addDrug(newDrug)).thenReturn(true);

    ResponseEntity<?> response = routeController.addDrug(newDrug);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Drug added successfully", response.getBody());
  }

  @Test
    void testAddDrug_AlreadyExists() {
    Map<String, Object> existingDrug = new HashMap<>();
    existingDrug.put("name", "ExistingDrug");
    when(drugService.getDrug("ExistingDrug")).thenReturn(new HashMap<>());

    ResponseEntity<?> response = routeController.addDrug(existingDrug);

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

    ResponseEntity<?> response = routeController.updateDrug(drugName, updates);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Drug updated successfully", response.getBody());
  }

  @Test
    void testUpdateDrug_NotFound() {
    String drugName = "NonexistentDrug";
    Map<String, Object> updates = new HashMap<>();
    when(drugService.getDrug(drugName)).thenReturn(null);

    ResponseEntity<?> response = routeController.updateDrug(drugName, updates);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Drug not found", response.getBody());
  }

  @Test
    void testRemoveDrug_Success() {
    String drugName = "ExistingDrug";
    when(drugService.removeDrug(drugName)).thenReturn(true);

    ResponseEntity<?> response = routeController.removeDrug(drugName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Drug removed successfully", response.getBody());
  }

  @Test
    void testRemoveDrug_NotFound() {
    String drugName = "NonexistentDrug";
    when(drugService.removeDrug(drugName)).thenReturn(false);

    ResponseEntity<?> response = routeController.removeDrug(drugName);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Failed to remove drug", response.getBody());
  }

  @Test
    void testGetAllDrugs() {
    List<String> drugs = Arrays.asList("Drug1", "Drug2", "Drug3");
    when(drugService.getAllDrugs()).thenReturn(drugs);

    ResponseEntity<?> response = routeController.getAllDrugs();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(drugs, response.getBody());
  }

  @Test
    void testGetDrugInteractions() {
    String drugName = "Aspirin";
    List<String> interactions = Arrays.asList("Interaction1", "Interaction2");
    when(drugService.getInteraction(drugName)).thenReturn(interactions);

    ResponseEntity<?> response = routeController.getDrugInteractions(drugName);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(interactions, response.getBody());
  }

  @Test
    void testGetInteraction_Found() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn(interactionEffect);

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB);

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

    ResponseEntity<?> response = routeController.getInteraction(drugA, drugB);

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

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals("Interaction added successfully", response.getBody());
  }

  @Test
    void testAddInteraction_AlreadyExists() {
    String drugA = "DrugA";
    String drugB = "DrugB";
    String interactionEffect = "Existing interaction effect";
    when(interactionService.getInteraction(drugA, drugB)).thenReturn("Existing interaction");

    ResponseEntity<?> response = routeController.addInteraction(drugA, drugB, interactionEffect);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertEquals("Interaction already exists", response.getBody());
  }

}