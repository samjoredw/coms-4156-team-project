package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
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
}
