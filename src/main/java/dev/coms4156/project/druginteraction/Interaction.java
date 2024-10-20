package dev.coms4156.project.druginteraction;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class represents interactions between drugs.
 */
@Service
public class Interaction {

  private final FirebaseService firebaseService;
  private final Firestore firestore;

  @Autowired
  public Interaction(FirebaseService firebaseService, Firestore firestore) {
    this.firebaseService = firebaseService;
    this.firestore = firestore;
  }

  /**
   * Add a new interaction document to the Firestore collection.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the second drug.
   * @param interactionEffect The effect of the interaction.
   * @return A CompletableFuture indicating completion of the addition.
   */
  public boolean addInteraction(String drugA, String drugB, String interactionEffect) {
    try {
      // Prepare a timestamp for createdAt and updatedAt fields
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      // Create a new interaction document
      Map<String, Object> newInteraction = new HashMap<>();
      newInteraction.put("drugA", drugA);
      newInteraction.put("drugB", drugB);
      newInteraction.put("interactionEffect", interactionEffect);
      newInteraction.put("createdBy", "Admin");
      newInteraction.put("createdAt", sdf.format(timestamp));
      newInteraction.put("updatedBy", "Admin");
      newInteraction.put("updatedAt", sdf.format(timestamp));

      // Generate a random 5-character alphanumeric document ID
      String documentId = generateRandomId(5);

      // Add the new document to Firestore with the generated document ID
      CompletableFuture<Void> future =
          firebaseService.addDocument("interactions", documentId, newInteraction);
      future.join();

      return true; // Successfully added
    } catch (Exception e) {
      e.printStackTrace();
      return false; // Error occurred during addition
    }
  }

  /**
   * Generate a random alphanumeric ID of the specified length.
   *
   * @param length The length of the generated ID.
   *
   * @return A random alphanumeric ID.
   */
  private String generateRandomId(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    Random random = new Random();
    StringBuilder result = new StringBuilder(length);

    for (int i = 0; i < length; i++) {
      result.append(characters.charAt(random.nextInt(characters.length())));
    }
    return result.toString();
  }

  /**
   * Remove a specific document (drug interaction) from the Firestore collection.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the Second drug.
   * @param interactionEffect The interaction effect between drugA and drugB.
   * @return boolean values indicating whether the interaction pair is removed or not.
   */
  public boolean removeInteraction(String drugA, String drugB, String interactionEffect) {
    try {
      // Query where drugA = drugA and drugB = drugB
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> query = firestore
          .collection("interactions").whereEqualTo("drugA", drugA).whereEqualTo("drugB", drugB)
          .whereEqualTo("interactionEffect", interactionEffect).get();

      com.google.cloud.firestore.QuerySnapshot querySnapshot = query.get();

      if (!querySnapshot.isEmpty()) {
        // Document found, delete the interaction
        com.google.cloud.firestore.DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        document.getReference().delete();
        return true;
      } else {
        // Try reverse: drugA = drugB and drugB = drugA
        ApiFuture<com.google.cloud.firestore.QuerySnapshot> reverseQuery = firestore
            .collection("interactions").whereEqualTo("drugA", drugB).whereEqualTo("drugB", drugA)
            .whereEqualTo("interactionEffect", interactionEffect).get();

        com.google.cloud.firestore.QuerySnapshot reverseQuerySnapshot = reverseQuery.get();

        if (!reverseQuerySnapshot.isEmpty()) {
          // Interaction found with reverse order, delete the interaction
          com.google.cloud.firestore.DocumentSnapshot reverseDocument =
              reverseQuerySnapshot.getDocuments().get(0);
          reverseDocument.getReference().delete();
          return true;
        } else {
          // No interaction found in either order
          return false;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Get a specific interaction from 2 specified drugs.
   *
   * @param drugA The name of the first drug.
   * @param drugB The name of the Second drug.
   * @return A String indicating the interaction effect between them
   */
  public String getInteraction(String drugA, String drugB) {
    try {
      // Query where drugA = drugA and drugB = drugB
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> query =
          firestore.collection("interactions").whereEqualTo("drugA", drugA)
              .whereEqualTo("drugB", drugB).get();

      com.google.cloud.firestore.QuerySnapshot querySnapshot = query.get();

      if (!querySnapshot.isEmpty()) {
        // Interaction found
        com.google.cloud.firestore.DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        return (String) document.get("interactionEffect");
      } else {
        // Try the reverse (drugA = drugB and drugB = drugA)
        ApiFuture<com.google.cloud.firestore.QuerySnapshot> reverseQuery =
            firestore.collection("interactions").whereEqualTo("drugA", drugB)
                .whereEqualTo("drugB", drugA).get();

        com.google.cloud.firestore.QuerySnapshot reverseQuerySnapshot = reverseQuery.get();

        if (!reverseQuerySnapshot.isEmpty()) {
          // Interaction found with reverse order
          com.google.cloud.firestore.DocumentSnapshot reverseDocument =
              reverseQuerySnapshot.getDocuments().get(0);
          return (String) reverseDocument.get("interactionEffect");
        } else {
          // No interaction found in either order
          return "No known interaction between " + drugA + " and " + drugB;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return "Error retrieving drug interaction: " + e.getMessage();
    }
  }

  /**
   * Get a specific interaction from a list of specified drugs.
   *
   * @param drugs The List of drugs to check for any pairwise interaction.
   * @return A List of String indicating all pairwise interaction effect
   */
  public List<Map<String, String>> getInteraction(List<String> drugs) {
    List<Map<String, String>> interactions = new ArrayList<>();

    try {
      for (int i = 0; i < drugs.size(); i++) {
        for (int j = i + 1; j < drugs.size(); j++) {
          String drugA = drugs.get(i);
          String drugB = drugs.get(j);

          String interactionEffect = getInteraction(drugA, drugB);
          Map<String, String> interaction = new HashMap<>();
          interaction.put("drugPair", drugA + " and " + drugB);
          interaction.put("interactionEffect", interactionEffect);
          interactions.add(interaction);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, String> errorInteraction = new HashMap<>();
      errorInteraction.put("drugPair", "Error");
      errorInteraction.put("interactionEffect",
              "Error retrieving drug interactions: " + e.getMessage());
      interactions.add(errorInteraction);
    }

    return interactions;
  }

  /**
   * Update a drug interaction document based on interaction ID.
   *
   * @param documentId The interaction id to update.
   * @param drugA The new first drug used to update.
   * @param drugB The new second drug used to update.
   * @param interactionEffect The new interaction used to update.
   * @return A CompletableFuture containing a list of document data.
   */
  public boolean updateInteraction(String documentId, String drugA, String drugB,
      String interactionEffect) {
    try {
      // Prepare a timestamp for updatedAt field
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      // Create a map for the updated fields
      Map<String, Object> updatedInteraction = new HashMap<>();
      updatedInteraction.put("drugA", drugA);
      updatedInteraction.put("drugB", drugB);
      updatedInteraction.put("interactionEffect", interactionEffect);
      updatedInteraction.put("updatedBy", "Admin");
      updatedInteraction.put("updatedAt", sdf.format(timestamp));

      // Use FirebaseService to update the document with the specified ID
      CompletableFuture<Void> future =
          firebaseService.updateDocument("interactions", documentId, updatedInteraction);
      future.join();

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
