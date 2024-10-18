package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

class RouteControllerTest {

    private RouteController routeController;
    private TestInteractionService interactionService;
    private TestDrugService drugService;

    @BeforeEach
    void setUp() {
        interactionService = new TestInteractionService();
        drugService = new TestDrugService();
        routeController = new RouteController(interactionService, drugService);
    }

    @Test
    void testGetDrug() {
        ResponseEntity<?> response = routeController.getDrug("Aspirin");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> drugInfo = (Map<String, Object>) response.getBody();
        assertEquals("Aspirin", drugInfo.get("name"));
    }

    @Test
    void testGetDrugNotFound() {
        ResponseEntity<?> response = routeController.getDrug("NonexistentDrug");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Drug not found", response.getBody());
    }

    @Test
    void testAddDrug() {
        Map<String, Object> newDrug = new HashMap<>();
        newDrug.put("name", "NewDrug");
        newDrug.put("description", "A new test drug");

        ResponseEntity<?> response = routeController.addDrug(newDrug);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Drug added successfully", response.getBody());
    }

    @Test
    void testAddExistingDrug() {
        Map<String, Object> existingDrug = new HashMap<>();
        existingDrug.put("name", "Aspirin");

        ResponseEntity<?> response = routeController.addDrug(existingDrug);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Drug already exists", response.getBody());
    }

    @Test
    void testUpdateDrug() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("description", "Updated description");

        ResponseEntity<?> response = routeController.updateDrug("Aspirin", updates);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Drug updated successfully", response.getBody());
    }

    @Test
    void testUpdateNonexistentDrug() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("description", "Updated description");

        ResponseEntity<?> response = routeController.updateDrug("NonexistentDrug", updates);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Drug not found", response.getBody());
    }

    @Test
    void testRemoveDrug() {
        ResponseEntity<?> response = routeController.removeDrug("Aspirin");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Drug removed successfully", response.getBody());
    }

    @Test
    void testRemoveNonexistentDrug() {
        ResponseEntity<?> response = routeController.removeDrug("NonexistentDrug");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Failed to remove drug", response.getBody());
    }

    @Test
    void testGetAllDrugs() {
        ResponseEntity<?> response = routeController.getAllDrugs();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<String> drugs = (List<String>) response.getBody();
        assertTrue(drugs.contains("Aspirin"));
        assertTrue(drugs.contains("Ibuprofen"));
    }

    @Test
    void testGetDrugInteractions() {
        ResponseEntity<?> response = routeController.getDrugInteractions("Aspirin");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<String> interactions = (List<String>) response.getBody();
        assertTrue(interactions.contains("Ibuprofen"));
    }

    @Test
    void testGetInteraction() {
        ResponseEntity<?> response = routeController.getInteraction("Aspirin", "Ibuprofen");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> interactionInfo = (Map<String, Object>) response.getBody();
        assertTrue(interactionInfo.containsKey("interactions"));
    }

    @Test
    void testGetMultipleInteractions() {
        ResponseEntity<?> response = routeController.getMultipleInteractions("Aspirin", "Ibuprofen", "Warfarin", null, null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        Map<String, List<Map<String, Object>>> interactionsInfo = (Map<String, List<Map<String, Object>>>) response.getBody();
        assertTrue(interactionsInfo.containsKey("interactions"));
    }

    @Test
    void testAddInteraction() {
        ResponseEntity<?> response = routeController.addInteraction("DrugA", "DrugB", "New interaction effect");
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Interaction added successfully", response.getBody());
    }

    @Test
    void testAddExistingInteraction() {
        ResponseEntity<?> response = routeController.addInteraction("Aspirin", "Ibuprofen", "Existing interaction");
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Interaction already exists", response.getBody());
    }

    @Test
    void testUpdateInteraction() {
        ResponseEntity<?> response = routeController.updateInteraction("doc123", "Aspirin", "Ibuprofen", "Updated interaction effect");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Interaction updated successfully", response.getBody());
    }

    @Test
    void testDeleteInteraction() {
        ResponseEntity<?> response = routeController.deleteInteraction("Aspirin", "Ibuprofen", "Interaction effect");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Interaction deleted successfully", response.getBody());
    }

    // Test double for Interaction service
    private static class TestInteractionService implements Interaction {
        private final Map<String, String> interactions = new HashMap<>();

        TestInteractionService() {
            interactions.put("Aspirin and Ibuprofen", "Increased risk of bleeding");
        }

        @Override
        public String getInteraction(String drugA, String drugB) {
            String key = drugA + " and " + drugB;
            return interactions.getOrDefault(key, "No known interaction between " + drugA + " and " + drugB);
        }

        @Override
        public List<String> getInteraction(List<String> drugs) {
            List<String> results = new ArrayList<>();
            for (int i = 0; i < drugs.size(); i++) {
                for (int j = i + 1; j < drugs.size(); j++) {
                    results.add(drugs.get(i) + " and " + drugs.get(j) + ": " + getInteraction(drugs.get(i), drugs.get(j)));
                }
            }
            return results;
        }

        @Override
        public boolean addInteraction(String drugA, String drugB, String interactionEffect) {
            String key = drugA + " and " + drugB;
            if (interactions.containsKey(key)) {
                return false;
            }
            interactions.put(key, interactionEffect);
            return true;
        }

        @Override
        public boolean updateInteraction(String documentId, String drugA, String drugB, String interactionEffect) {
            String key = drugA + " and " + drugB;
            interactions.put(key, interactionEffect);
            return true;
        }

        @Override
        public boolean removeInteraction(String drugA, String drugB, String interactionEffect) {
            String key = drugA + " and " + drugB;
            return interactions.remove(key) != null;
        }
    }

    // Test double for Drugs service
    private static class TestDrugService implements Drugs {
        private final Map<String, Map<String, Object>> drugs = new HashMap<>();

        TestDrugService() {
            Map<String, Object> aspirin = new HashMap<>();
            aspirin.put("name", "Aspirin");
            aspirin.put("description", "Pain reliever");
            drugs.put("Aspirin", aspirin);

            Map<String, Object> ibuprofen = new HashMap<>();
            ibuprofen.put("name", "Ibuprofen");
            ibuprofen.put("description", "Anti-inflammatory");
            drugs.put("Ibuprofen", ibuprofen);
        }

        @Override
        public Map<String, Object> getDrug(String name) {
            return drugs.get(name);
        }

        @Override
        public boolean addDrug(Map<String, Object> drugInfo) {
            String name = (String) drugInfo.get("name");
            if (drugs.containsKey(name)) {
                return false;
            }
            drugs.put(name, drugInfo);
            return true;
        }

        @Override
        public boolean updateDrug(String name, Map<String, Object> updates) {
            if (!drugs.containsKey(name)) {
                return false;
            }
            Map<String, Object> drug = drugs.get(name);
            drug.putAll(updates);
            return true;
        }

        @Override
        public boolean removeDrug(String name) {
            return drugs.remove(name) != null;
        }

        @Override
        public List<String> getAllDrugs() {
            return new ArrayList<>(drugs.keySet());
        }

        @Override
        public List<String> getInteraction(String drugName) {
            return Arrays.asList("Ibuprofen", "Warfarin");
        }
    }
}