Team: Runtime Terrors

Prerequisites
Make sure you have the following installed on your system before proceeding:

JDK 17: Required for compiling and running the project.
Maven 3.9.9: Required for managing dependencies and building the project.

To verify that you have the correct versions installed, run the following commands:
$ java -version
# Output should indicate JDK 17
$ mvn -version
# Output should indicate Maven 3.9.9 (older versions might work)

Firebase setup
To set up Firebase, follow these steps:
Go to Firebase Console > Settings > Service Accounts > Generate New Private Key
Download the JSON file and save it in the project's resources folder, 
renaming it to firebase_config.json.


# Drug Interaction API Documentation

## Base URL
```
/api/v1
```

---

## 1. **Get Interaction Between Two Drugs**
Retrieve the interaction effect between two specific drugs.

- **Endpoint:** `/interactions`  
- **Method:** `GET`  
- **Parameters:**  
  - `drugA` (String) – Required. Name of the first drug.  
  - `drugB` (String) – Required. Name of the second drug.  
- **Response:**  
  - **200 OK:**  
    ```json
    {
      "interactions": {
        "drugPair": "DrugA and DrugB",
        "interactionBool": true,
        "interactionEffect": "Description of the interaction effect"
      }
    }
    ```
  - **200 OK (No known interaction):**  
    ```json
    {
      "noInteractions": {
        "drugPair": "DrugA and DrugB",
        "interactionBool": false,
        "interactionEffect": "No known interaction"
      }
    }
    ```
  - **400 Bad Request:** Invalid input or missing drug names.
  - **500 Internal Server Error:** Unexpected error occurred.

---

## 2. **Get Interactions Between Multiple Drugs**
Retrieve interactions among multiple drugs (up to five).

- **Endpoint:** `/get_interactions`  
- **Method:** `GET`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `drugC` (String) – Optional.  
  - `drugD` (String) – Optional.  
  - `drugE` (String) – Optional.  
- **Response:**  
  - **200 OK:**  
    ```json
    {
      "interactions": [
        {
          "drugPair": "DrugA and DrugB",
          "interactionBool": true,
          "interactionEffect": "Interaction description"
        }
      ],
      "noInteractions": [
        {
          "drugPair": "DrugC and DrugD",
          "interactionBool": false,
          "interactionEffect": "No known interaction"
        }
      ]
    }
    ```
  - **500 Internal Server Error:** Unexpected error occurred.

---

## 3. **Add New Drug Interaction**
Add a new interaction between two drugs to the database.

- **Endpoint:** `/interactions/add`  
- **Method:** `POST`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.  
- **Response:**  
  - **201 Created:** Interaction added successfully.  
  - **409 Conflict:** Interaction already exists.  
  - **400 Bad Request:** Failed to add interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---

## 4. **Update Existing Drug Interaction**
Update the details of an existing drug interaction.

- **Endpoint:** `/interactions/update/{documentId}`  
- **Method:** `PATCH`  
- **Path Parameter:**  
  - `documentId` (String) – Required. ID of the interaction to update.  
- **Request Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.  
- **Response:**  
  - **200 OK:** Interaction updated successfully.  
  - **400 Bad Request:** Failed to update interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---

## 5. **Delete Drug Interaction**
Delete a specific drug interaction from the database.

- **Endpoint:** `/interactions/delete`  
- **Method:** `DELETE`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.  
- **Response:**  
  - **200 OK:** Interaction deleted successfully.  
  - **400 Bad Request:** Failed to delete interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---

## 6. **Error Handling**
Handles exceptions that occur during any endpoint execution.

- **Response Format:**  
  - **500 Internal Server Error:**  
    ```json
    {
      "message": "An error occurred: <error_message>"
    }
    ```
---
