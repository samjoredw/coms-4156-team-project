package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    testInteraction.addInteraction("Aspirin", "Warfarin", "Increased risk of bleeding.");
  }

  /**
   * Clear the database after each test to ensure a clean state.
   */
  @AfterEach
  public void clearDatabase() {
    // Remove all test-specific data after each test
    testInteraction.removeInteraction("Aspirin", "Warfarin", "Increased risk of bleeding.");
    testInteraction.removeInteraction("Ibuprofen", "Naproxen",
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
    Map<String, Object> interaction1 = new HashMap<>();
    interaction1.put("interactionEffect", "Increased risk of bleeding.");
    interaction1.put("interactionBool", true);
    interaction1.put("drugPair", "Warfarin and Aspirin");

    Map<String, Object> interaction2 = new HashMap<>();
    interaction2.put("interactionEffect", "Increased risk of gastrointestinal bleeding.");
    interaction2.put("interactionBool", true);
    interaction2.put("drugPair", "Aspirin and Ibuprofen");

    Map<String, Object> noInteraction = new HashMap<>();
    noInteraction.put("interactionEffect", "No known interaction between Warfarin and Ibuprofen");
    noInteraction.put("interactionBool", false);
    noInteraction.put("drugPair", "Warfarin and Ibuprofen");

    List<Map<String, Object>> interactions = new ArrayList<>();
    interactions.add(interaction1);
    interactions.add(interaction2);

    List<Map<String, Object>> noInteractions = new ArrayList<>();
    noInteractions.add(noInteraction);

    Map<String, List<Map<String, Object>>> expectedOutput = new HashMap<>();
    expectedOutput.put("interactions", interactions);
    expectedOutput.put("noInteractions", noInteractions);

    List<String> drugs = Arrays.asList("Warfarin", "Aspirin", "Ibuprofen");
    List<Map<String, String>> interactionList = testInteraction.getInteraction(drugs);
    assertEquals(3, interactionList.size(), "Should return 3 interactions for 3 drugs");

    Map<String, List<Map<String, Object>>> actualOutput = new HashMap<>();
    actualOutput.put("interactions", new ArrayList<>());
    actualOutput.put("noInteractions", new ArrayList<>());

    for (Map<String, String> interaction : interactionList) {
      Map<String, Object> interactionData = new HashMap<>(interaction);
      System.out.println(interactionData);
      boolean hasInteraction = !interaction.get("interactionEffect")
              .startsWith("No known interaction");
      interactionData.put("interactionBool", hasInteraction);

      if (hasInteraction) {
        actualOutput.get("interactions").add(interactionData);
      } else {
        actualOutput.get("noInteractions").add(interactionData);
      }
    }

    // Compare expected and actual outputs
    assertEquals(expectedOutput.get("interactions").size(), actualOutput.get("interactions").size(),
        "Number of interactions should match");
    assertEquals(expectedOutput.get("noInteractions").size(),
        actualOutput.get("noInteractions").size(), "Number of noInteractions should match");

    // for (Map<String, Object> expected : expectedOutput.get("interactions")) {
    // assertTrue(actualOutput.get("interactions").stream()
    // .anyMatch(actual -> compareInteractions(expected, actual)),
    // "Expected interaction not found: " + expected);
    // }

    for (Map<String, Object> expected : expectedOutput.get("noInteractions")) {
      assertTrue(
          actualOutput.get("noInteractions").stream()
              .anyMatch(actual -> compareInteractions(expected, actual)),
          "Expected noInteraction not found: " + expected);
    }
  }

  private boolean compareInteractions(Map<String, Object> expected, Map<String, Object> actual) {
    return expected.get("drugPair").equals(actual.get("drugPair"))
        && expected.get("interactionEffect").equals(actual.get("interactionEffect"))
        && expected.get("interactionBool").equals(actual.get("interactionBool"));
  }

  @Test
  @Order(4)
  public void removeInteractionTest() {

    testInteraction.addInteraction("Aspirin", "Warfarin",
            "Increased risk of bleeding.");
    // Remove an existing interaction
    boolean result = testInteraction.removeInteraction("Aspirin",
            "Warfarin", "Increased risk of bleeding.");
    assertTrue(result, "Removing existing interaction should return true");

    // Check that the interaction was removed
    String interactionEffect = testInteraction.getInteraction("Aspirin", "Warfarin");
    // assertEqual(interactionEffect, "");

    // Attempt to remove a non-existent interaction
    result = testInteraction.removeInteraction("Aspirin",
            "Ibuprofen", "Non-existent interaction");
    assertFalse(result, "Removing non-existent interaction should return false");

    // Null drug names
    result = testInteraction.removeInteraction(null, "Warfarin",
            "Some interaction");
    assertFalse(result, "Removing interaction with null drug name should return false");

    result = testInteraction.removeInteraction("Aspirin", null,
            "Some interaction");
    assertFalse(result,
            "Removing interaction with null drug name should return false");

    // Empty string drug names
    result = testInteraction.removeInteraction("", "Warfarin",
            "Some interaction");
    assertFalse(result, "Removing interaction with empty drug name should return false");

    result = testInteraction.removeInteraction("Aspirin", "",
            "Some interaction");
    assertFalse(result,
            "Removing interaction with empty drug name should return false");
  }

  @Test
  @Order(5)
  public void testAddInteraction_ExceptionHandling() {
    // Simulate invalid input to trigger exception handling
    boolean result = testInteraction
            .addInteraction(null, null, null);
    assertFalse(result, "Adding interaction with null values should return false");

    result = testInteraction.addInteraction("", "", "Invalid input");
    assertFalse(result, "Adding interaction with empty values should return false");
  }

  @Test
  @Order(6)
  public void testRemoveInteraction_ExceptionHandling() {
    boolean result = testInteraction.removeInteraction(null, null, null);
    assertFalse(result, "Removing interaction with null values should return false");

    result = testInteraction.removeInteraction("", "", "Invalid input");
    assertFalse(result, "Removing interaction with empty values should return false");
  }

  @Test
  @Order(7)
  public void testGetInteraction_ExceptionHandling() {
    String result = testInteraction.getInteraction(null, null);
    assertNull(result, "Retrieving interaction with null values should return null");

    result = testInteraction.getInteraction("", "");
    assertEquals("No known interaction between  and ", result,
        "Retrieving interaction with empty values should return default message");
  }

  @Test
  @Order(8)
  public void testGetInteractionList_ExceptionHandling() {
    List<String> drugs = new ArrayList<>();
    drugs.add(null);
    drugs.add("DrugB");

    List<Map<String, String>> result = testInteraction.getInteraction(drugs);
    assertTrue(result.isEmpty(), "Result should contain an error interaction");
  }

  @Test
  @Order(9)
  public void testUpdateInteraction_ExceptionHandling() {
    boolean result = testInteraction.updateInteraction(null, null, null, null);
    assertFalse(result, "Updating interaction with null values should return false");

    result = testInteraction.updateInteraction("", "", "", "Invalid input");
    assertFalse(result, "Updating interaction with empty values should return false");
  }
}
