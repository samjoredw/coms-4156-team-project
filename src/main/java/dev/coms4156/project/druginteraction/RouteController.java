package dev.coms4156.project.druginteraction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling routes related to interactions.
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(allowCredentials = "false")
public class RouteController {

  private final Interaction interactionService;
  private final Drugs drugService;

  @Autowired
  public RouteController(Interaction interactionService, Drugs drugService) {
    this.interactionService = interactionService;
    this.drugService = drugService;
  }

  /**
   * Retrieves information about a specific drug.
   *
   * @param name The name of the drug.
   * @return A ResponseEntity containing the drug information or an error message.
   */
  @GetMapping(value = "/drug", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getDrug(@RequestParam("name") String name) {
    try {
      if (name == null || name.isEmpty()) {
        return new ResponseEntity<>("Invalid input: Drug name cannot be empty",
            HttpStatus.BAD_REQUEST);
      }

      Map<String, Object> drugInfo = drugService.getDrug(name);
      if (drugInfo != null) {
        return new ResponseEntity<>(drugInfo, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Drug not found", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Adds a new drug to the database.
   *
   * @param drugInfo A map containing the drug information.
   * @return A ResponseEntity indicating success or failure of the operation.
   */
  @PostMapping(value = "/drug/add", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addDrug(@RequestBody Map<String, Object> drugInfo,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            "Access denied: " + email + " is not associated with the Columbia University");
      }

      String name = (String) drugInfo.get("name");
      if (name == null || name.isEmpty()) {
        return new ResponseEntity<>("Invalid input: Drug name cannot be empty",
            HttpStatus.BAD_REQUEST);
      }

      if (!drugService.getDrug(name).isEmpty()) {
        return new ResponseEntity<>("Drug already exists", HttpStatus.CONFLICT);
      }

      boolean added = drugService.addDrug(drugInfo);
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
   * Updates an existing drug in the database.
   *
   * @param name The name of the drug to update.
   * @param updates A map containing the fields to update.
   * @return A ResponseEntity indicating success or failure of the update operation.
   */
  @PatchMapping(value = "/drug/update/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateDrug(@PathVariable String name,
      @RequestBody Map<String, Object> updates,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
              "Access denied: " + email + " is not associated with the Columbia University");
      }

      if (drugService.getDrug(name) == null) {
        return new ResponseEntity<>("Drug not found", HttpStatus.NOT_FOUND);
      }

      boolean updated = drugService.updateDrug(name, updates);
      if (updated) {
        return new ResponseEntity<>("Drug updated successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to update drug", HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      return handleException(e);
    }
  }

  /**
   * Removes a specific drug from the database.
   *
   * @param name The name of the drug to be removed.
   * @return A ResponseEntity indicating success or failure of the removal operation.
   */
  @DeleteMapping(value = "/drug/remove", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> removeDrug(@RequestParam("name") String name,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                "Access denied: " + email + " is not associated with the Columbia University");
      }

      if (name == null || name.isEmpty()) {
        return new ResponseEntity<>("Invalid input: Drug name cannot be empty",
            HttpStatus.BAD_REQUEST);
      }

      boolean removed = drugService.removeDrug(name);
      if (removed) {
        return new ResponseEntity<>("Drug removed successfully", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Failed to remove drug", HttpStatus.NOT_FOUND);
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
  public ResponseEntity<?> getAllDrugs(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
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
        return new ResponseEntity<>("Invalid input: Drug names cannot be empty",
            HttpStatus.BAD_REQUEST);
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
   * Retrieves interactions between multiple drugs.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the second drug.
   * @param drugC The name of the third drug.
   * @param drugD The name of the fourth drug.
   * @param drugE The name of the fifth drug.
   * @return A ResponseEntity containing the interaction details or an error message.
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
      if (drugC != null) {
        drugs.add(drugC);
      }
      if (drugD != null) {
        drugs.add(drugD);
      }
      if (drugE != null) {
        drugs.add(drugE);
      }

      List<Map<String, String>> interactionEffects = interactionService.getInteraction(drugs);

      Map<String, List<Map<String, Object>>> response = new HashMap<>();
      List<Map<String, Object>> interactions = new ArrayList<>();
      List<Map<String, Object>> noInteractions = new ArrayList<>();

      for (Map<String, String> effect : interactionEffects) {
        Map<String, Object> interactionData = new HashMap<>();
        String drugPair = effect.get("drugPair");
        String interactionEffect = effect.get("interactionEffect");

        interactionData.put("drugPair", drugPair);
        interactionData.put("interactionEffect", interactionEffect);

        // Changed logic to check if the interaction effect indicates no known interaction
        // or if it's an unknown interaction
        if (interactionEffect.startsWith("No known interaction")
            || "Unknown interaction".equals(interactionEffect)) {
          interactionData.put("interactionBool", false);
          noInteractions.add(interactionData);
        } else {
          interactionData.put("interactionBool", true);
          interactions.add(interactionData);
        }
      }

      response.put("interactions", interactions);
      response.put("noInteractions", noInteractions);

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
      @RequestParam("interactionEffect") String interactionEffect,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                "Access denied: " + email + " is not associated with the Columbia University");
      }

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
   * @param drugA The updated name of the first drug.
   * @param drugB The updated name of the second drug.
   * @param interactionEffect The updated description of the interaction effect.
   * @return A ResponseEntity indicating success or failure of the update operation.
   */
  @PatchMapping(value = "/interactions/update/",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateInteraction(
      @RequestParam("drugA") String drugA, @RequestParam("drugB") String drugB,
      @RequestParam("interactionEffect") String interactionEffect,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                "Access denied: " + email + " is not associated with the Columbia University");
      }

      boolean updated =
          interactionService.updateInteraction(drugA, drugB, interactionEffect);
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
      @RequestParam("interactionEffect") String interactionEffect,
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    try {
      // Check if the Authorization header is present
      if (authorization == null || !authorization.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Missing or invalid Authorization header");
      }

      // Extract the idToken from the Authorization header
      String idToken = authorization.substring(7);

      // Verify the token using Firebase Authentication
      FirebaseToken decodedToken;
      try {
        decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
      }

      // Retrieve user information from the token
      String email = decodedToken.getEmail();
      if (email == null || !email.endsWith("@columbia.edu")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                "Access denied: " + email + " is not associated with the Columbia University");
      }

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
   * Handle exceptions that occur during the execution of controller methods.
   *
   * @param e exception that was thrown.
   * @return ResponseEntity containing an error message and an appropriate HTTP status code.
   */
  private ResponseEntity<?> handleException(Exception e) {
    e.printStackTrace();
    return new ResponseEntity<>("An error occurred: " + e.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}


