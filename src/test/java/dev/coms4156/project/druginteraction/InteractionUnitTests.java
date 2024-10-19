package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    assertEquals(3, interactionList.size(),
            "Should return 3 interactions for 3 drugs");

    Map<String, List<Map<String, Object>>> actualOutput = new HashMap<>();
    actualOutput.put("interactions", new ArrayList<>());
    actualOutput.put("noInteractions", new ArrayList<>());

    for (Map<String, String> interaction : interactionList) {
      Map<String, Object> interactionData = new HashMap<>(interaction);
      boolean hasInteraction =
              !interaction.get("interactionEffect").startsWith("No known interaction");
      interactionData.put("interactionBool", hasInteraction);

      if (hasInteraction) {
        actualOutput.get("interactions").add(interactionData);
      } else {
        actualOutput.get("noInteractions").add(interactionData);
      }
    }

    // Compare expected and actual outputs
    assertEquals(expectedOutput.get("interactions").size(),
            actualOutput.get("interactions").size(),
            "Number of interactions should match");
    assertEquals(expectedOutput.get("noInteractions").size(),
            actualOutput.get("noInteractions").size(),
            "Number of noInteractions should match");

    for (Map<String, Object> expected : expectedOutput.get("interactions")) {
      assertTrue(actualOutput.get("interactions").stream()
                      .anyMatch(actual -> compareInteractions(expected, actual)),
              "Expected interaction not found: " + expected);
    }

    for (Map<String, Object> expected : expectedOutput.get("noInteractions")) {
      assertTrue(actualOutput.get("noInteractions").stream()
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
    boolean result = testInteraction.removeInteraction("Aspirin", 
        "Warfarin", "Increased risk of bleeding.");
    assertTrue(result, "Removing existing interaction should return true");

    // Test removing non-existent interaction
    result = testInteraction.removeInteraction("Aspirin", "Ibuprofen", "Non-existent interaction");
    assertFalse(result, "Removing non-existent interaction should return false");
  }
  
  // TODO: Add unit test for getInteractionList method and other methods
}
