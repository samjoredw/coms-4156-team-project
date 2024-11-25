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
    // Valid drug info
    Map<String, Object> drugInfo1 = new HashMap<>();
    drugInfo1.put("name", "Aspirin");
    drugInfo1.put("dosageForm", "Tablet");
    drugInfo1.put("indications", "Pain relief, fever reduction");

    boolean isAdded1 = testDrugs.addDrug(drugInfo1);
    assertTrue(isAdded1, "Aspirin should be added successfully");

    // Empty name field
    Map<String, Object> invalidDrugInfo = new HashMap<>();
    invalidDrugInfo.put("name", "");
    boolean isAddedInvalid = testDrugs.addDrug(invalidDrugInfo);
    assertFalse(isAddedInvalid, "Drug with empty name should not be added");

    // Null name field
    Map<String, Object> nullNameDrugInfo = new HashMap<>();
    nullNameDrugInfo.put("dosageForm", "Tablet");
    boolean isAddedNullName = testDrugs.addDrug(nullNameDrugInfo);
    assertFalse(isAddedNullName, "Drug with null name should not be added");
  }

  @Test
  @Order(2)
  public void getDrugTest() {
    // Normal retrieval case
    Map<String, Object> drug = testDrugs.getDrug("Aspirin");
    assertNotNull(drug, "Aspirin should be retrieved");
    assertEquals("Tablet", drug.get("dosageForm"), "Dosage form should match");
    assertEquals("Pain relief, fever reduction",
        drug.get("indications"), "Indications should match");

    // Non-existent drug retrieval
    Map<String, Object> nonExistentDrug = testDrugs.getDrug("NonExistentDrug");
    assertNull(nonExistentDrug, "Non-existent drug should return null");

    // Null drug name case
    Map<String, Object> nullDrug = testDrugs.getDrug(null);
    assertNull(nullDrug, "Null drug name should return null");
  }

  @Test
  @Order(3)
  public void updateDrugTest() {
    // Valid update case
    Map<String, Object> updates = new HashMap<>();
    updates.put("dosageForm", "Capsule");
    updates.put("indications", "Pain relief, fever reduction, anti-inflammatory");

    boolean isUpdated = testDrugs.updateDrug("Aspirin", updates);
    assertTrue(isUpdated, "Aspirin should be updated successfully");

    Map<String, Object> updatedDrug = testDrugs.getDrug("Aspirin");
    assertEquals("Capsule", updatedDrug.get("dosageForm"),
        "Updated dosage form should match");
    assertEquals("Pain relief, fever reduction, anti-inflammatory",
        updatedDrug.get("indications"), "Updated indications should match");

    // Non-existent drug update
    boolean nonExistentUpdate = testDrugs.updateDrug("NonExistentDrug", updates);
    assertFalse(nonExistentUpdate, "Updating non-existent drug should return false");

    // Null updates map
    boolean nullUpdate = testDrugs.updateDrug("Aspirin", null);
    assertFalse(nullUpdate, "Updating with null updates should return false");

    // Null drug name for update
    boolean nullNameUpdate = testDrugs.updateDrug(null, updates);
    assertFalse(nullNameUpdate, "Updating with null drug name should return false");
  }

  @Test
  @Order(4)
  public void getAllDrugsTest() {
    // Normal case with added drugs
    List<String> allDrugs = testDrugs.getAllDrugs();
    assertNotNull(allDrugs, "All drugs list should not be null");
    assertTrue(allDrugs.contains("Aspirin"), "All drugs should contain Aspirin");
  }

  @Test
  @Order(5)
  public void getInteractionTest() {
    // Check interactions for an added drug
    List<String> interactions = testDrugs.getInteraction("Aspirin");
    assertNotNull(interactions, "Interactions list should not be null");

    // Non-existent drug interactions
    List<String> noInteraction = testDrugs.getInteraction("NonExistentDrug");
    assertTrue(noInteraction.isEmpty(), "Non-existent drug should have no interactions");

    // Null drug name interactions
    List<String> nullInteraction = testDrugs.getInteraction(null);
    assertTrue(nullInteraction.isEmpty(), "Null drug name should return empty interactions list");
  }

  @Test
  @Order(6)
  public void removeDrugTest() {
    // Remove existing drug
    boolean isRemoved = testDrugs.removeDrug("Aspirin");
    assertTrue(isRemoved, "Aspirin should be removed successfully");

    Map<String, Object> removedDrug = testDrugs.getDrug("Aspirin");
    assertNull(removedDrug, "Removed drug should return null when queried");

    // Attempt to remove a non-existent drug
    boolean nonExistentRemove = testDrugs.removeDrug("NonExistentDrug");
    assertFalse(nonExistentRemove, "Removing non-existent drug should return false");

    // Null drug name case
    boolean nullRemove = testDrugs.removeDrug(null);
    assertFalse(nullRemove, "Removing drug with null name should return false");
  }
}
