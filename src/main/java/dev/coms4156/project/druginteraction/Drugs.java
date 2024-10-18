package dev.coms4156.project.druginteraction;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;

/**
 * This class represents drugs and their properties.
 */
@Service
public class Drugs {

  private final FirebaseService firebaseService;

  @Autowired
  private Firestore firestore;

  @Autowired
  public Drugs(FirebaseService firebaseService) {
    this.firebaseService = firebaseService;
  }

  /**
   * Retrieve information about a specific drug.
   *
   * @param drugName The name of the drug.
   * @return A map containing the drug information, or null if not found.
   */
  public Map<String, Object> getDrug(String drugName) {
    try {
      DocumentSnapshot document = firestore.collection("drugs").document(drugName.toLowerCase()).get().get();
      if (document.exists()) {
        return document.getData();
      } else {
        return null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Add a new drug to the Firestore collection.
   *
   * @param drugInfo A map containing the drug information.
   * @return A boolean indicating whether the addition was successful.
   */
  public boolean addDrug(Map<String, Object> drugInfo) {
    try {
      String drugName = (String) drugInfo.get("name");
      if (drugName == null || drugName.isEmpty()) {
        return false;
      }

      // Prepare a timestamp for createdAt and updatedAt fields
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      drugInfo.put("createdAt", sdf.format(timestamp));
      drugInfo.put("updatedAt", sdf.format(timestamp));
      drugInfo.put("createdBy", "admin");
      drugInfo.put("updatedBy", "admin");

      String documentId = drugName.toLowerCase();

      // Add the new document to Firestore
      CompletableFuture<Void> future = firebaseService.addDocument("drugs", documentId, drugInfo);
      future.join();

      return true; // Successfully added
    } catch (Exception e) {
      e.printStackTrace();
      return false; // Error occurred during addition
    }
  }

  /**
   * Update an existing drug in the Firestore collection.
   *
   * @param drugName The name of the drug to update.
   * @param updates A map containing the fields to update.
   * @return A boolean indicating whether the update was successful.
   */
  public boolean updateDrug(String drugName, Map<String, Object> updates) {
    try {
      String documentId = drugName.toLowerCase();
      DocumentSnapshot document = firestore.collection("drugs").document(documentId).get().get();

      if (!document.exists()) {
        return false;
      }

      // Prepare a timestamp for updatedAt field
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      updates.put("updatedAt", sdf.format(timestamp));
      updates.put("updatedBy", "admin");

      // Update the document in Firestore
      ApiFuture<WriteResult> writeResult = firestore.collection("drugs").document(documentId).update(updates);
      writeResult.get(); // Wait for the update to complete

      return true; // Successfully updated
    } catch (Exception e) {
      e.printStackTrace();
      return false; // Error occurred during update
    }
  }

  /**
   * Remove a specific drug from the Firestore "drugs" collection.
   *
   * @param drugName The name of the drug to be removed.
   * @return A boolean indicating whether the removal was successful.
   */
  public boolean removeDrug(String drugName) {
    try {
      String documentId = drugName.toLowerCase();
      ApiFuture<WriteResult> writeResult = firestore.collection("drugs").document(documentId).delete();
      writeResult.get(); // Wait for the deletion to complete
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false; // Error occurred during deletion
    }
  }

  /**
   * Get all drugs from the "drugs" collection.
   *
   * @return A list of drug names.
   */
  public List<String> getAllDrugs() {
    List<String> drugs = new ArrayList<>();

    try {
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> query = firestore
              .collection("drugs")
              .get();

      com.google.cloud.firestore.QuerySnapshot querySnapshot = query.get();

      if (!querySnapshot.isEmpty()) {
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
          drugs.add((String) document.get("name"));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return drugs;
  }

  /**
   * Get all interactions for a specific drug.
   *
   * @param drugName The name of the drug to check interactions for.
   * @return A list of strings, each representing an interaction involving the drug.
   */
  public List<String> getInteraction(String drugName) {
    List<String> interactions = new ArrayList<>();

    try {
      // Query where drugA = drugName
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> queryA = firestore
              .collection("interactions")
              .whereEqualTo("drugA", drugName)
              .get();
      com.google.cloud.firestore.QuerySnapshot querySnapshotA = queryA.get();

      for (DocumentSnapshot document : querySnapshotA.getDocuments()) {
        String interactionEffect = (String) document.get("interactionEffect");
        String otherDrug = (String) document.get("drugB");
        interactions.add(
            "Interaction between " + drugName + " and " + otherDrug + ": " + interactionEffect);
      }

      // Query where drugB = drugName
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> queryB = firestore
              .collection("interactions")
              .whereEqualTo("drugB", drugName)
              .get();

      com.google.cloud.firestore.QuerySnapshot querySnapshotB = queryB.get();

      for (DocumentSnapshot document : querySnapshotB.getDocuments()) {
        String interactionEffect = (String) document.get("interactionEffect");
        String otherDrug = (String) document.get("drugA");
        interactions.add(
            "Interaction between " + drugName + " and " + otherDrug + ": " + interactionEffect);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return interactions;
  }
}