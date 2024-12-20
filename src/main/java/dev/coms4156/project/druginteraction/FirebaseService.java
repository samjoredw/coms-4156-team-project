package dev.coms4156.project.druginteraction;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


/**
 * This class provides methods to interact with Firebase Firestore database.
 */
@Service
public class FirebaseService {
  // enable the firestore database and ability to add / remove

  // @Autowired
  private final FirebaseApp firebaseApp;

  @Autowired
  private Firestore firestore;

  /**
   * Creates an instance of the Firebase Service.
   *
   * @param firebaseApp A `FirebaseApp` object representing the Firebase configuration.
   */
  @Autowired
  public FirebaseService(FirebaseApp firebaseApp) {
    this.firebaseApp = firebaseApp;
  }

  /**
   * Returns a reference to the Firebase Realtime Database. If multiple clients are using this
   * service, it will provide the same database reference instance.
   *
   * @return A reference to the Firebase Realtime Database.
   */
  public DatabaseReference getDatabaseReference() {
    FirebaseDatabase database = FirebaseDatabase.getInstance(firebaseApp);
    return database.getReference();
  }

  /**
   * Adds a document to the specified collection in the Firestore database.
   *
   * @param collection The name of the collection to add the document to.
   * @param documentId The ID of the document to add.
   * @param data The data to add to the document.
   *
   * @return A CompletableFuture that completes when the document is added
   */
  public CompletableFuture<Void> addDocument(String collection, String documentId,
      Map<String, Object> data) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    DocumentReference docRef = firestore.collection(collection).document(documentId);
    ApiFuture<WriteResult> result = docRef.set(data);
    result.addListener(() -> {
      try {
        result.get();
        future.complete(null);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }

  /**
   * Removes a document from the specified collection in the Firestore database.
   *
   * @param collection The name of the collection to remove the document
   * @param documentId The ID of the document to remove.
   *
   * @return A CompletableFuture that completes when the document is removed
   */
  public CompletableFuture<Void> removeDocument(String collection, String documentId) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    DocumentReference docRef = firestore.collection(collection).document(documentId);
    ApiFuture<WriteResult> result = docRef.delete();
    result.addListener(() -> {
      try {
        result.get();
        future.complete(null);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }

  /**
   * Retrieves a document from the specified collection in the Firestore database.
   *
   * @param collection The name of the collection to retrieve the document from.
   * @param documentId The ID of the document to retrieve.
   *
   * @return A CompletableFuture that completes with the document data when the
   */
  public CompletableFuture<Map<String, Object>> getDocument(String collection, String documentId) {
    CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
    DocumentReference docRef = firestore.collection(collection).document(documentId);
    ApiFuture<com.google.cloud.firestore.DocumentSnapshot> result = docRef.get();
    result.addListener(() -> {
      try {
        com.google.cloud.firestore.DocumentSnapshot document = result.get();
        if (document.exists()) {
          future.complete(document.getData());
        } else {
          future.complete(null);
        }
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }

  /**
   * Retrieves all documents from the specified collection in the Firestore database.
   *
   * @param collection The name of the collection to retrieve documents from.
   * 
   * @return A CompletableFuture that completes with a list of document data when the documents are
   *         retrieved.
   */
  public CompletableFuture<List<Map<String, Object>>> getAllDocuments(String collection) {
    CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
    ApiFuture<com.google.cloud.firestore.QuerySnapshot> result =
        firestore.collection(collection).get();
    result.addListener(() -> {
      try {
        List<Map<String, Object>> documents = new ArrayList<>();
        com.google.cloud.firestore.QuerySnapshot querySnapshot = result.get();
        for (com.google.cloud.firestore.QueryDocumentSnapshot document : querySnapshot
            .getDocuments()) {
          documents.add(document.getData());
        }
        future.complete(documents);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }

  /**
   * Updates a document in the specified collection in the Firestore database.
   *
   * @param collection The name of the collection to update the document in.
   * @param documentId The ID of the document to update.
   * @param data The data to update in the document.
   *
   * @return A CompletableFuture that completes when the document is updated
   */
  public CompletableFuture<Void> updateDocument(String collection, String documentId,
      Map<String, Object> data) {
    CompletableFuture<Void> future = new CompletableFuture<>();
    ApiFuture<WriteResult> result =
        firestore.collection(collection).document(documentId).update(data);
    result.addListener(() -> {
      try {
        result.get();
        future.complete(null);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }

  /**
   * Authenticate whether a idToken is valid or not for API request.
   *
   * @param headers The HttpHeaders that were sent to the API, including token
   *
   * @return A boolean Indicating whether the token is valid or not
   */
  public boolean authenticateToken(HttpHeaders headers) {
    String authorizationHeader = headers.getFirst("Authorization");
    if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
      return false;
    }

    String idToken = authorizationHeader.substring(7); // Remove "Bearer " prefix

    // Verify the Firebase ID token
    try {
      FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
      return (decodedToken != null);
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Queries documents in the specified collection based on criteria.
   *
   * @param collection The name of the collection to query.
   * @param criteria A map of field names to their corresponding values to filter the documents.
   * @return A CompletableFuture that completes with a list of document data matching the criteria.
   */
  public CompletableFuture<List<Map<String, Object>>> queryDocuments(
          String collection, Map<String, Object> criteria) {
    CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();
    com.google.cloud.firestore.CollectionReference collectionRef = firestore.collection(collection);

    // Build the query based on the criteria
    com.google.cloud.firestore.Query query = collectionRef;
    for (Map.Entry<String, Object> entry : criteria.entrySet()) {
      query = query.whereEqualTo(entry.getKey(), entry.getValue());
    }

    // Execute the query
    ApiFuture<com.google.cloud.firestore.QuerySnapshot> result = query.get();
    result.addListener(() -> {
      try {
        List<Map<String, Object>> documents = new ArrayList<>();
        com.google.cloud.firestore.QuerySnapshot querySnapshot = result.get();
        for (com.google.cloud.firestore.QueryDocumentSnapshot document :
                querySnapshot.getDocuments()) {
          Map<String, Object> documentData = document.getData();
          documentData.put("id", document.getId()); // Add document ID for reference
          documents.add(documentData);
        }
        future.complete(documents);
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    }, Runnable::run);
    return future;
  }
}
