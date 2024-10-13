package dev.coms4156.project.individualproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.cloud.firestore.WriteResult;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;

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
   * Add a new document (drug) to the Firestore collection.
   *
   * @param drugName  The name of the drug.
   * @return A boolean indicating whether the addition was successful.
   */
  public boolean addDrug(String drugName) {
    try {
      // Prepare a timestamp for createdAt fields
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      // Create a new drug document with only the drug name
      Map<String, Object> newDrug = new HashMap<>();
      newDrug.put("drugName", drugName);
      newDrug.put("createdAt", sdf.format(timestamp));

      String documentId = drugName.toLowerCase();

      // Add the new document to Firestore
      CompletableFuture<Void> future = firebaseService.addDocument("drugs", documentId, newDrug);
      future.join();

      return true;  // Successfully added
    } catch (Exception e) {
      e.printStackTrace();
      return false;  // Error occurred during addition
    }
  }

  /**
   * Remove a specific drug from the Firestore "drugs" collection.
   *
   * @param drugName  The name of the drug to be removed.
   * @return A boolean indicating whether the removal was successful.
   */
  public boolean removeDrug(String drugName) {
    try {
      // Query where drugName = drugName
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> query = firestore.collection("drugs")
          .whereEqualTo("drugName", drugName)
          .get();

      com.google.cloud.firestore.QuerySnapshot querySnapshot = query.get();

      if (!querySnapshot.isEmpty()) {
        // Document found, delete the drug
        com.google.cloud.firestore.DocumentSnapshot document = querySnapshot.getDocuments().get(0);
        document.getReference().delete();
        return true;
      } else {
        return false;  // No document found for the specified drug
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;  // Error occurred during deletion
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
        for (com.google.cloud.firestore.DocumentSnapshot document : querySnapshot.getDocuments()) {
          drugs.add((String) document.get("drugName"));
        }
      } else {
        drugs.add("No drugs found.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      drugs.add("Error retrieving drugs: " + e.getMessage());
    }

    return drugs;
  }

  /**
   * Get all interactions for a specific drug.
   *
   * @param drugName  The name of the drug to check interactions for.
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

      for (com.google.cloud.firestore.DocumentSnapshot document : querySnapshotA.getDocuments()) {
        String interactionEffect = (String) document.get("interactionEffect");
        String otherDrug = (String) document.get("drugB");
        interactions.add("Interaction between " + drugName + " and " + otherDrug + ": " + interactionEffect);
      }

      // Query where drugB = drugName
      ApiFuture<com.google.cloud.firestore.QuerySnapshot> queryB = firestore
          .collection("interactions")
          .whereEqualTo("drugB", drugName)
          .get();

      com.google.cloud.firestore.QuerySnapshot querySnapshotB = queryB.get();

      for (com.google.cloud.firestore.DocumentSnapshot document : querySnapshotB.getDocuments()) {
        String interactionEffect = (String) document.get("interactionEffect");
        String otherDrug = (String) document.get("drugA");
        interactions.add("Interaction between " + drugName + " and " + otherDrug + ": " + interactionEffect);
      }

      if (interactions.isEmpty()) {
        interactions.add("No known interactions for drug: " + drugName);
      }
    } catch (Exception e) {
      e.printStackTrace();
      interactions.add("Error retrieving interactions for drug: " + e.getMessage());
    }

    return interactions;
  }
}
