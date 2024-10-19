package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests for the Interaction class.
 */
@SpringBootTest(classes = DrugInteraction.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InteractionUnitTests {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private Interaction testInteraction;

  @BeforeEach
  public void setupTesting() {
    testInteraction.addInteraction("Aspirin", "Warfarin", "Increased risk of bleeding.");
  }

  @Test
  @Order(1)
  // test getting an interaction based on 2 drugs
  public void getInteractionTest() {
    // Call the getInteraction method with "Aspirin" and "Warfarin"
    String interactionEffect = testInteraction.getInteraction("Aspirin", "Warfarin");

    // Assert the expected interaction effect
    assertEquals("Increased risk of bleeding.", interactionEffect);
  }

  @Test
  @Order(2)
    public void addInteractionTest() {
    boolean result = testInteraction.addInteraction("Ibuprofen", 
        "Naproxen", "Increased risk of gastrointestinal side effects.");
    assertTrue(result, "Adding a new interaction should return true");

    String interactionEffect = testInteraction.getInteraction("Ibuprofen", "Naproxen");
    assertEquals("Increased risk of gastrointestinal side effects.", interactionEffect);
  }

  @Test
  @Order(3)
    public void getInteractionListTest() {
    List<String> drugs = Arrays.asList("Aspirin", "Warfarin", "Ibuprofen");
    List<String> interactions = testInteraction.getInteraction(drugs);

    assertEquals(3, interactions.size(), "Should return 3 interactions for 3 drugs");
    assertTrue(interactions.contains("Increased risk of bleeding."), 
        "Should contain known interaction");
      
  }

  @Test
  @Order(4)
    public void removeInteractionTest() {
    boolean result = testInteraction.removeInteraction("Aspirin", 
        "Warfarin", "Increased risk of bleeding.");
    assertTrue(result, "Removing existing interaction should return true");

    // Test removing non-existent interaction
    result = testInteraction.removeInteraction("Aspirin", "Ibuprofen", "Non-existent interaction");
    assertFalse(result, "Removing non-existent interaction should return false");
  }
  
  // TODO: Add unit test for getInteractionList method
}
