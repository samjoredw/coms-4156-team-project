package dev.coms4156.project.druginteraction;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DrugInteraction.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DrugsUnitTests {

  @Autowired
  private Drugs testDrugs;

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
}