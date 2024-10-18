package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
  // private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd
  // HH:mm:ss");

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
    // testInteraction.addInteraction("Aspirin", "Warfarin", "Increased risk of
    // bleeding.");
    // testInteraction.addInteraction("Aspirin", "Ibuprofen", "Increased risk of
    // gastrointestinal " +
    // "bleeding.");
  }

  @Test
  @Order(1)
  // test getting an interaction based on 2 drugs
  public void addDrugTest() {
    // Add a drug
    boolean isAdded1 = testDrugs.addDrug("Aspirin");
    boolean isAdded2 = testDrugs.addDrug("Warfarin");
    boolean isAdded3 = testDrugs.addDrug("Ibuprofen");

    // Assert that the drug was added successfully
    assertTrue(isAdded1, "Drug should be added successfully");
    assertTrue(isAdded2, "Drug should be added successfully");
    assertTrue(isAdded3, "Drug should be added successfully");
  }
}
