package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the Drugs class.
 */
@SpringBootTest(classes = DrugInteraction.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DrugsUnitTests {

  @Autowired
  private Drugs testDrugs;

  // @Autowired
  // private Interaction testInteraction;

  /**
   * Setup testing by adding interactions to the testInteraction object.
   */
  @BeforeEach
  public void setupTesting() {
    System.out.println("Running Drug.java unit test...\n");
  }

  @Test
  @Order(1)
  public void addDrugTest() {
    Map<String, Object> drugInfo1 = new HashMap<>();
    drugInfo1.put("name", "Aspirin");
    drugInfo1.put("dosageForm", "Tablet");
    drugInfo1.put("indications", "Pain relief, fever reduction");

    Map<String, Object> drugInfo2 = new HashMap<>();
    drugInfo2.put("name", "Warfarin");
    drugInfo2.put("dosageForm", "Tablet");
    drugInfo2.put("indications", "Blood thinner");

    boolean isAdded1 = testDrugs.addDrug(drugInfo1);
    boolean isAdded2 = testDrugs.addDrug(drugInfo2);

    assertTrue(isAdded1, "Aspirin should be added successfully");
    assertTrue(isAdded2, "Warfarin should be added successfully");
  }

  @Test
  @Order(2)
    public void getDrugTest() {
    Map<String, Object> drug = testDrugs.getDrug("Aspirin");
    assertNotNull(drug, "Aspirin should be retrieved");
    assertEquals("Tablet", drug.get("dosageForm"), "Dosage form should match");
    assertEquals("Pain relief, fever reduction", 
        drug.get("indications"), "Indications should match");
    Map<String, Object> nonExistentDrug = testDrugs.getDrug("NonExistentDrug");
    assertNull(nonExistentDrug, "Non-existent drug should return null");
  }

  @Test
  @Order(3)
    public void updateDrugTest() {
    Map<String, Object> updates = new HashMap<>();
    updates.put("dosageForm", "Capsule");
    updates.put("indications", "Pain relief, fever reduction, anti-inflammatory");

    boolean isUpdated = testDrugs.updateDrug("Aspirin", updates);
    assertTrue(isUpdated, "Aspirin should be updated successfully");

    Map<String, Object> updatedDrug = testDrugs.getDrug("Aspirin");
    assertEquals("Capsule", updatedDrug.get("dosageForm"), "Updated dosage form should match");
    assertEquals("Pain relief, fever reduction, anti-inflammatory", 
        updatedDrug.get("indications"), "Updated indications should match");

    boolean nonExistentUpdate = testDrugs.updateDrug("NonExistentDrug", updates);
    assertFalse(nonExistentUpdate, "Updating non-existent drug should return false");
  }

  @Test
  @Order(4)
    public void getAllDrugsTest() {
    List<String> allDrugs = testDrugs.getAllDrugs();
    assertNotNull(allDrugs, "All drugs list should not be null");
    assertTrue(allDrugs.contains("Aspirin"), "All drugs should contain Aspirin");
    assertTrue(allDrugs.contains("Warfarin"), "All drugs should contain Warfarin");
  }

  @Test
  @Order(5)
    public void getInteractionTest() {
    // Assuming interactions are added separately
    List<String> interactions = testDrugs.getInteraction("Aspirin");
    assertNotNull(interactions, "Interactions list should not be null");
    // Add more specific assertions based on expected interactions
  }

  @Test
  @Order(6)
    public void removeDrugTest() {
    boolean isRemoved = testDrugs.removeDrug("Warfarin");
    assertTrue(isRemoved, "Warfarin should be removed successfully");

    Map<String, Object> removedDrug = testDrugs.getDrug("Warfarin");
    assertNull(removedDrug, "Removed drug should return null when queried");
  }
}