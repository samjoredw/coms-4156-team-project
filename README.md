# Team: Runtime Terrors - Drug Interaction and Information API
# Service Overview

This service provides useful information on individual drugs and their interactions with each other. 

One part of the service covers drug interactions. This part takes in up to five pharmaceutical drug names and returns two things about each potential pair of drugs (ex: 1+2, 1+3, 1+4, 1+5, 2+3, etc.): (1) whether or not there’s an interaction between them, (2) if there is, a description of that interaction.

The other part of the service covers information about drugs themselves. This takes in a drug name and returns key information about the drug.

# Using the API as a client

For a short while, our service will be hosted on Google Cloud.

Please see the API documentation below for the endpoints that can be reached using Postman
or any other such service

# Building, running, and testing the service locally

## Prerequisites
Make sure you have the following installed on your system before proceeding:

JDK 17: Required for compiling and running the project.
Maven 3.9.9: Required for managing dependencies and building the project.

To verify that you have the correct versions installed, run the following commands:
```
$ java -version
```
Output should indicate JDK 17
```
$ mvn -version
```
Output should indicate Maven 3.9.9 (older versions might work)

You will also need the [Google Cloud CLI](https://cloud.google.com/cli?hl=en) installed, as well as [Firebase](console.firebase.com)

## Firebase setup
This API uses a Firebase Cloud Firestore as its database. To set up your own Firestore instance, follow these steps:
- Sign up for [Firebase](console.firebase.com)
- Create a Project
- Create a Firestore Database (left hand column)
- Go to Firebase Console > Settings > Service Accounts > Generate New Private Key
- Download the JSON file and save it in the project's resources folder, renaming it to firebase_config.json.

Note that a new service would not have any data, so it would have to be populated using the endpoints below. For
a short while, we will host our service with data on Google Cloud (see the "Using the API" section below)

## Building and running

You can buld the service by running the following command from the root directory:
```
mvn -B package --file pom.xml
```
The API can then be started by running the following command from the root directory:
```
mvn spring-boot:run
```
## Running tests

Tests can be run with the following command from the root directory:
```
mvn test
```

# Drug Interaction API Documentation


## Base URL
```
/api/v1
```

---

## Drug Endpoints

### 1. Get Drug Information
Retrieve information about a specific drug.

- **Endpoint:** `/drug`
- **Method:** `GET`
- **Parameters:**
  - `name` (String) – Required. Name of the drug.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drug?name=alcohol
- **Example Response:**
    ```json
   {
      "createdAt": "2024-10-23 19:42:02",
      "indications": "Not a medication; consumed as a beverage",
      "contraindications": "Liver disease, certain medications, and pregnancy",
      "updatedBy": "admin",
      "createdBy": "admin",
      "dosageForm": "Liquid",
      "name": "Alcohol",
      "sideEffects": "Impaired judgment, liver damage, and increased risk of accidents",
      "updatedAt": "2024-10-23 19:42:02"
  }
    ```
- **Response:**
  - **200 OK:** Drug information retrieved successfully.
  - **400 Bad Request:** Invalid input or missing drug name.
  - **404 Not Found:** Drug not found.
  - **500 Internal Server Error:** Unexpected error occurred.

### 2. Add New Drug
Add a new drug to the database.

- **Endpoint:** `/drug/add`
- **Method:** `POST`
- **Example Request URL:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drug/add
- **Example Request Body (in Postman select "Body" => "Raw"):**
    ```json
    {
      "name": "Test Drug",
      "dosageForm": "Tablet",
      "indications": "None",
      "contraindications": "Also none",
      "sideEffects": "Maybe some",
      "createdBy": "admin",
      "updatedBy": "admin",
      "createdAt": "2024-10-24 04:02:00",
      "updatedAt": "2024-10-25 19:42:02"
    }
    ```
- **Response:**
  - **201 Created:** Drug added successfully.
  - **400 Bad Request:** Invalid input or failed to add drug.
  - **409 Conflict:** Drug already exists.
  - **500 Internal Server Error:** Unexpected error occurred.

### 3. Update Existing Drug
Update an existing drug in the database.

- **Endpoint:** `/drug/update/{name}`
- **Method:** `PATCH`
- **Example Request URL:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drug/update/digoxin
- **Example Request Body (in Postman select "Body" => "Raw"):**
    ```json
    {
      "dosageForm": "Tablet",
      "indications": "For pain when writing software",
      "contraindications": "None"
    }
    ```
- **Response:**
  - **200 OK:** Drug updated successfully.
  - **400 Bad Request:** Failed to update drug.
  - **404 Not Found:** Drug not found.
  - **500 Internal Server Error:** Unexpected error occurred.

### 4. Remove Drug
Remove a specific drug from the database.

- **Endpoint:** `/drug/remove`
- **Method:** `DELETE`
- **Parameters:**
  - `name` (String) – Required. Name of the drug to be removed.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drug/remove?name=fluconazole
- **List of Drugs to Try (for demo):**
  - digoxin, paroxetine, simvastatin
- **Response:**
  - **200 OK:** Drug removed successfully.
  - **400 Bad Request:** Invalid input or missing drug name.
  - **404 Not Found:** Failed to remove drug (drug not found).
  - **500 Internal Server Error:** Unexpected error occurred.

### 5. Get All Drugs
Retrieve all drugs from the database.

- **Endpoint:** `/drugs`
- **Method:** `GET`
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drugs
- **Response:**
  - **200 OK:** List of all drugs retrieved successfully.
  - **500 Internal Server Error:** Unexpected error occurred.

### 6. Get Drug Interactions
Retrieve all interactions for a specific drug.

- **Endpoint:** `/drugs/interactions`
- **Method:** `GET`
- **Parameters:**
  - `drugName` (String) – Required. Name of the drug to check interactions for.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/drugs/interactions?drugName=TestDrugA
- **Response:**
  - **200 OK:** List of interactions retrieved successfully.
  - **500 Internal Server Error:** Unexpected error occurred.

---

## Drug Interaction Endpoints

### 1. **Get Interaction Between Two Drugs**
Retrieve the interaction effect between two specific drugs.

- **Endpoint:** `/interactions`  
- **Method:** `GET`  
- **Parameters:**  
  - `drugA` (String) – Required. Name of the first drug.  
  - `drugB` (String) – Required. Name of the second drug.
- **Example Request (Interaction Exists):**
   - https://drug-interaction-api.uk.r.appspot.com/api/v1/interactions?drugA=Warfarin&drugB=Aspirin
- **Example Response:**  
  - **200 OK:**  
    ```json
    {
        "interactions": {
            "interactionEffect": "Increased risk of bleeding.",
            "interactionBool": true,
            "drugPair": "Warfarin and Aspirin"
        }
    }
    ```
- **Example Request (No Interaction Exists):**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/interactions?drugA=Warfarine&drugB=Aspirin
  - **200 OK (No known interaction):**  
    ```json
    {
        "noInteractions": {
        "interactionEffect": "No known interaction between Warfarine and Aspirin",
        "interactionBool": false,
        "drugPair": "Warfarine and Aspirin"
        }
    }
    ```
  - **400 Bad Request:** Invalid input or missing drug names.
  - **500 Internal Server Error:** Unexpected error occurred.

---

### 2. **Get Interactions Between Multiple Drugs**
Retrieve interactions among multiple drugs (up to five).

- **Endpoint:** `/get_interactions`  
- **Method:** `GET`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `drugC` (String) – Optional.  
  - `drugD` (String) – Optional.  
  - `drugE` (String) – Optional.
- **Example Request:**
   - https://drug-interaction-api.uk.r.appspot.com/api/v1/get_interactions?drugA=Warfarin&drugB=Aspirin&drugC=Not a Drug
- **Example Response:**    
- **Response:**  
  - **200 OK:**  
    ```json
    {
        "noInteractions": [
            {
                "interactionEffect": "No known interaction between Warfarin and Not a Drug",
                "interactionBool": false,
                "drugPair": "Warfarin and Not a Drug"
            },
            {
                "interactionEffect": "No known interaction between Aspirin and Not a Drug",
                "interactionBool": false,
                "drugPair": "Aspirin and Not a Drug"
            }
        ],
        "interactions": [
            {
                "interactionEffect": "Increased risk of bleeding.",
                "interactionBool": true,
                "drugPair": "Warfarin and Aspirin"
            }
        ]
    }
    ```
  - **400 Bad Request:** Invalid input or missing drug names.
  - **500 Internal Server Error:** Unexpected error occurred.

---

### 3. **Add New Drug Interaction**
Add a new interaction between two drugs to the database.

- **Endpoint:** `/interactions/add`  
- **Method:** `POST`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/interactions/add?drugA=TestDrugA&drugB=TestDrugB&interactionEffect=This is a test interaction.
- **Response:**  
  - **201 Created:** Interaction added successfully.  
  - **409 Conflict:** Interaction already exists.  
  - **400 Bad Request:** Failed to add interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---

### 4. **Update Existing Drug Interaction**
Update the details of an existing drug interaction.

- **Endpoint:** `/interactions/update/{documentId}`  
- **Method:** `PATCH`  
- **Path Parameter:**  
  - `documentId` (String) – Required. ID of the interaction to update.
- **Request Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/interactions/update/11Zyx?drugA=UpdateA&drugB=UpdateB&interactionEffect=This is a test update.
- **Example IDs to Try**
  - 98fkB, 9xtu5, 9zjMs 
- **Response:**  
  - **200 OK:** Interaction updated successfully.  
  - **400 Bad Request:** Failed to update interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---

### 5. **Delete Drug Interaction**
Delete a specific drug interaction from the database.

- **Endpoint:** `/interactions/delete`
- **Method:** `DELETE`  
- **Parameters:**  
  - `drugA` (String) – Required.  
  - `drugB` (String) – Required.  
  - `interactionEffect` (String) – Required.
- **Example Request:**
  - https://drug-interaction-api.uk.r.appspot.com/api/v1/interactions/delete?drugA=Aspirin&drugB=Ibuprofen&interactionEffect=Increased risk of gastrointestinal bleeding.
- **Response:**  
  - **200 OK:** Interaction deleted successfully.  
  - **400 Bad Request:** Failed to delete interaction.  
  - **500 Internal Server Error:** Unexpected error occurred.

---
## General
### 1. **Error Handling**
Handles exceptions that occur during any endpoint execution.

- **Response Format:**  
  - **500 Internal Server Error:**  
    ```json
    {
      "message": "An error occurred: <error_message>"
    }
    ```
---

