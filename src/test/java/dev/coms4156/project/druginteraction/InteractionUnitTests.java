package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
  public void setupDatabase() {
    // Add any baseline data needed before each test
    testInteraction.addInteraction("Aspirin", "Warfarin", "Increased risk of bleeding.");
  }

  @AfterEach
  public void clearDatabase() {
    // Remove all test-specific data after each test
    testInteraction.removeInteraction(
        "Aspirin", "Warfarin", "Increased risk of bleeding.");
    testInteraction.removeInteraction(
        "Ibuprofen", "Naproxen",
        "Increased risk of gastrointestinal side effects.");
  }


  @Test
  @Order(1)
  public void getInteractionTest() {
    // Valid case: Retrieve known interaction
    String interactionEffect = testInteraction.getInteraction("Aspirin", "Warfarin");
    assertEquals("Increased risk of bleeding.", interactionEffect);


    // Non-existent interaction case
    // interactionEffect = testInteraction.getInteraction("Aspirin", "Ibuprofen");
    // assertNull(interactionEffect, "Non-existent interaction should return null");

    // Null drug names
    interactionEffect = testInteraction.getInteraction(null, "Warfarin");
    assertNull(interactionEffect, "Null drug name should return null");

    interactionEffect = testInteraction.getInteraction("Aspirin", null);
    assertNull(interactionEffect, "Null drug name should return null");
  }

  @Test
  @Order(2)
  public void addInteractionTest() {
    // Valid addition of new interaction
    boolean result = testInteraction.addInteraction("Ibuprofen", "Naproxen",
        "Increased risk of gastrointestinal side effects.");
    assertTrue(result, "Adding a new interaction should return true");

    // Check that the interaction was added
    String interactionEffect = testInteraction.getInteraction("Ibuprofen", "Naproxen");
    assertEquals("Increased risk of gastrointestinal side effects.", interactionEffect);

    // Add duplicate interaction
    result = testInteraction.addInteraction("Aspirin", "Warfarin", "Increased risk of bleeding.");
    assertFalse(result, "Adding duplicate interaction should return false");

    // Null drug name cases
    result = testInteraction.addInteraction(null, "Ibuprofen", "Null drug interaction");
    assertFalse(result, "Adding interaction with null drug name should return false");

    result = testInteraction.addInteraction("Ibuprofen", null, "Null drug interaction");
    assertFalse(result, "Adding interaction with null drug name should return false");

    // Empty string drug name cases
    result = testInteraction.addInteraction("", "Ibuprofen", "Empty drug interaction");
    assertFalse(result, "Adding interaction with empty drug name should return false");

    result = testInteraction.addInteraction("Ibuprofen", "", "Empty drug interaction");
    assertFalse(result, "Adding interaction with empty drug name should return false");
  }

  @Test
  @Order(3)
  public void getInteractionListTest() {
    List<String> drugs = Arrays.asList("Aspirin", "Warfarin", "Ibuprofen");
    List<String> interactions = testInteraction.getInteraction(drugs);

    // Filter out any non-interaction messages (like default messages or errors)
    interactions.removeIf(interaction -> interaction.startsWith(
        "No known interaction") || interaction.startsWith("Error"));

    // Test with a null input
    List<String> none = new ArrayList<>();
    interactions = testInteraction.getInteraction(none);
    assertNull(interactions, "Null drug list should return an empty interactions list");

    // Test with a list of non-existent drugs
    drugs = Arrays.asList("NonExistentDrug1", "NonExistentDrug2");
    interactions = testInteraction.getInteraction(drugs);
    assertTrue(interactions.isEmpty(),
        "Non-existent drugs should return an empty interactions list");
  }

  @Test
  @Order(4)
  public void removeInteractionTest() {

    testInteraction.addInteraction("Aspirin", "Warfarin",
        "Increased risk of bleeding.");
    // Remove an existing interaction
    boolean result = testInteraction.removeInteraction("Aspirin", "Warfarin",
        "Increased risk of bleeding.");
    assertTrue(result, "Removing existing interaction should return true");

    // Check that the interaction was removed
    String interactionEffect = testInteraction.getInteraction("Aspirin", "Warfarin");
//    assertEqual(interactionEffect, "");

    // Attempt to remove a non-existent interaction
    result = testInteraction.removeInteraction("Aspirin", "Ibuprofen", "Non-existent interaction");
    assertFalse(result, "Removing non-existent interaction should return false");

    // Null drug names
    result = testInteraction.removeInteraction(null, "Warfarin", "Some interaction");
    assertFalse(result, "Removing interaction with null drug name should return false");

    result = testInteraction.removeInteraction("Aspirin", null, "Some interaction");
    assertFalse(result, "Removing interaction with null drug name should return false");

    // Empty string drug names
    result = testInteraction.removeInteraction("", "Warfarin", "Some interaction");
    assertFalse(result, "Removing interaction with empty drug name should return false");

    result = testInteraction.removeInteraction("Aspirin", "", "Some interaction");
    assertFalse(result, "Removing interaction with empty drug name should return false");
  }
}
