package dev.coms4156.project.individualproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1")
public class RouteController {

  private final Interaction interactionService;
  private final Drugs drugService;

  @Autowired
  public RouteController(Interaction interactionService, Drugs drugService) {
    this.interactionService = interactionService;
    this.drugService = drugService;
  }

  /**
   * Adds a new drug to the database.
   *
   * @param drugName The name of the drug to be added.
   * @return A ResponseEntity indicating success or failure of the operation.
   */
  @PostMapping(value = "/drugs/add", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addDrug(@RequestParam("drugName") String drugName) {
    try {
      boolean added = drugService.addDrug(drugName);
      if (added) {
        return new ResponseEntity<>("Drug added successfully", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("Failed to add drug", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Removes a drug from the database.
   *
   * @param drugName The name of the drug to be removed.
   * @return A ResponseEntity indicating success or failure of the operation.
   */
  @DeleteMapping(value = "/drugs/remove", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> removeDrug(@RequestParam("drugName") String drugName) {
    try {
      boolean removed = drugService.removeDrug(drugName);
      if (removed) {
        return new ResponseEntity<>("Drug removed successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to remove drug", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Retrieves all drugs from the database.
   *
   * @return A ResponseEntity containing a list of all drugs or an error message.
   */
  @GetMapping(value = "/drugs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getAllDrugs() {
    try {
      List<String> drugs = drugService.getAllDrugs();
      return new ResponseEntity<>(drugs, HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Retrieves all interactions for a specific drug.
   *
   * @param drugName The name of the drug to check interactions for.
   * @return A ResponseEntity containing a list of interactions or an error message.
   */
  @GetMapping(value = "/drugs/interactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getDrugInteractions(@RequestParam("drugName") String drugName) {
    try {
      List<String> interactions = drugService.getInteraction(drugName);
      return new ResponseEntity<>(interactions, HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Retrieves the interaction between two specified drugs.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the second drug.
   * @return A ResponseEntity containing the interaction details or an error message.
   */
  @GetMapping(value = "/interactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getInteraction(@RequestParam("drugA") String drugA,
                                          @RequestParam("drugB") String drugB) {
    try {
      if (drugA == null || drugB == null || drugA.isEmpty() || drugB.isEmpty()) {
        return new ResponseEntity<>("Invalid input: Drug names cannot be empty", HttpStatus.BAD_REQUEST);
      }

      String interactionEffect = interactionService.getInteraction(drugA, drugB);

      Map<String, Object> response = new HashMap<>();
      Map<String, Object> interactionData = new HashMap<>();

      interactionData.put("drugPair", drugA + " and " + drugB);
      interactionData.put("interactionBool", !interactionEffect.startsWith("No known interaction"));
      interactionData.put("interactionEffect", interactionEffect);

      if ((boolean) interactionData.get("interactionBool")) {
        response.put("interactions", interactionData);
      } else {
        response.put("noInteractions", interactionData);
      }

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Retrieves interactions for multiple drugs (up to five).
   *
   * @param drugA The name of the first drug (required).
   * @param drugB The name of the second drug (required).
   * @param drugC The name of the third drug (optional).
   * @param drugD The name of the fourth drug (optional).
   * @param drugE The name of the fifth drug (optional).
   * @return A ResponseEntity containing a list of interactions or an error message.
   */
  @GetMapping(value = "/get_interactions", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getMultipleInteractions(@RequestParam("drugA") String drugA,
                                                   @RequestParam("drugB") String drugB,
                                                   @RequestParam(value = "drugC", required = false) String drugC,
                                                   @RequestParam(value = "drugD", required = false) String drugD,
                                                   @RequestParam(value = "drugE", required = false) String drugE) {
    try {
      List<String> drugs = new ArrayList<>();
      drugs.add(drugA);
      drugs.add(drugB);
      if (drugC != null) drugs.add(drugC);
      if (drugD != null) drugs.add(drugD);
      if (drugE != null) drugs.add(drugE);

      List<Map<String, Object>> interactionEffects = interactionService.getInteraction(drugs);

      Map<String, List<Map<String, Object>>> response = new HashMap<>();
      List<Map<String, Object>> interactions = new ArrayList<>();
      List<Map<String, Object>> noInteractions = new ArrayList<>();

      for (Map<String, Object> effect : interactionEffects) {
        if ((boolean) effect.get("interactionBool")) {
          interactions.add(effect);
        } else {
          noInteractions.add(effect);
        }
      }

      if (!noInteractions.isEmpty()) {
        response.put("noInteractions", noInteractions);
      }
      if (!interactions.isEmpty()) {
        response.put("interactions", interactions);
      }

      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Adds a new drug interaction to the database.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the second drug.
   * @param interactionEffect The description of the interaction effect.
   * @return A ResponseEntity indicating success or failure of the operation.
   */
  @PostMapping(value = "/interactions/add", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addInteraction(@RequestParam("drugA") String drugA,
                                          @RequestParam("drugB") String drugB,
                                          @RequestParam("interactionEffect") String interactionEffect) {
    try {
      String existingInteraction = interactionService.getInteraction(drugA, drugB);
      if (!existingInteraction.startsWith("No known interaction")) {
        return new ResponseEntity<>("Interaction already exists", HttpStatus.CONFLICT);
      }

      boolean added = interactionService.addInteraction(drugA, drugB, interactionEffect);
      if (added) {
        return new ResponseEntity<>("Interaction added successfully", HttpStatus.CREATED);
      } else {
        return new ResponseEntity<>("Failed to add interaction", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Updates an existing drug interaction in the database.
   *
   * @param documentId The unique identifier of the interaction document.
   * @param drugA The updated name of the first drug.
   * @param drugB The updated name of the second drug.
   * @param interactionEffect The updated description of the interaction effect.
   * @return A ResponseEntity indicating success or failure of the update operation.
   */
  @PatchMapping(value = "/interactions/update/{documentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateInteraction(@PathVariable String documentId,
                                             @RequestParam("drugA") String drugA,
                                             @RequestParam("drugB") String drugB,
                                             @RequestParam("interactionEffect") String interactionEffect) {
    try {
      boolean updated = interactionService.updateInteraction(documentId, drugA, drugB, interactionEffect);
      if (updated) {
        return new ResponseEntity<>("Interaction updated successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to update interaction", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Deletes a specific drug interaction from the database.
   *
   * @param drugA The name of the first drug in the interaction.
   * @param drugB The name of the second drug in the interaction.
   * @param interactionEffect The description of the interaction effect to be deleted.
   * @return A ResponseEntity indicating success or failure of the deletion operation.
   */
  @DeleteMapping(value = "/interactions/delete", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteInteraction(@RequestParam("drugA") String drugA,
                                             @RequestParam("drugB") String drugB,
                                             @RequestParam("interactionEffect") String interactionEffect) {
    try {
      boolean deleted = interactionService.removeInteraction(drugA, drugB, interactionEffect);
      if (deleted) {
        return new ResponseEntity<>("Interaction deleted successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to delete interaction", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Handles exceptions that occur during the execution of controller methods.
   *
   * @param e The exception that was thrown.
   * @return A ResponseEntity containing an error message and an appropriate HTTP status code.
   */
  private ResponseEntity<?> handleException(Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}