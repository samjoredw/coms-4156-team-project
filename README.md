# Team: Runtime Terrors - Drug Interaction and Information API
# Service Overview

This service provides useful information on individual drugs and their interactions with each other. 

One part of the service covers drug interactions. This part takes in up to five pharmaceutical drug names and returns two things about each potential pair of drugs (ex: 1+2, 1+3, 1+4, 1+5, 2+3, etc.): (1) whether or not there’s an interaction between them, (2) if there is, a description of that interaction.

The other part of the service covers information about drugs themselves. This takes in a drug name and returns key information about the drug.

# Using the API as a client

For a short while, our service will be hosted on Google Cloud at https://drug-interaction-api.uk.r.appspot.com/api/v1/.

Please see the API documentation below for the endpoints that can be reached using Postman or any other such service.
Please note that our service requires authentication.

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

# Our Client Application
Our client application targets healthcare professionals, providing a user-friendly interface to access drug information and check potential interactions between medications. The app makes it easier than ever for healthcare workers to verify drug safety and access comprehensive medication information. 

## Client Application Features
* Query drug interactions between multiple medications simultaneously (up to 5 drugs)
* Access detailed drug information including dosage forms, indications, contraindications, and side effects
* Simple and intuitive interface requiring no specialized database knowledge
* Free, open-source solution for healthcare professionals
* Only users with an @columbia.edu email are able to make modifications to the database

## How it works with our Service
When a user performs an action in the client app (like searching for drug interactions), a request is sent to our drug interaction API hosted on GCP. The API processes the request and returns relevant drug information or interaction data, which is then presented to the user in an easy-to-understand format.

## Building and Running the Client

#### Prerequisites
* python

#### Running the Client
```bash
# Change to correct directory
cd frontend/src

# Start development server
python -m http.server 8000
```

The web client can be accessed at the URL shown in the terminal after starting the server at http://localhost:8080.

### Authentication
You will need to log in to the client using one of the options provided on the home page.

### End-to-End Testing
Please perform the following tests in order to verify proper functionality:

1. Start the client application following the instructions above

2. Navigate to the web client in your browser
   - Expected result: Display of the welcome page

3. Search for drug interactions:
   - Enter two or more drug names
   - Click "Check Interactions"
   - Expected result: Display of interaction information or "No known interactions" message

4. View drug information:
   - Search for a specific drug
   - Expected result: Display of comprehensive drug information including dosage, indications, and side effects

5. Test multiple drug search:
   - Enter up to 5 drug names
   - Expected result: Display of all potential interactions between entered drugs

Note: For testing purposes, you can use the following known drug combinations:
- Warfarin + Aspirin (known interaction)
- Digoxin + Paroxetine (known interaction)
- Simvastatin + Warfarin (known interaction)

## A Note to Developers
This client application serves as a reference implementation for our Drug Interaction API. Developers are encouraged to build their own clients using our documented API endpoints. The source code demonstrates best practices for:
- Making API calls to check drug interactions
- Handling API responses and errors
- Presenting drug information to users
- Managing multiple drug queries

For more information on developing your own client application, please refer to the API Documentation section to use the endpoints our service provides.

### What makes our Client Better/Faster/Cheaper than prior Solutions
- **Free and Open Source**: No subscription fees or hidden costs
- **Privacy-Focused**: We don't sell user data to third parties
- **Built-in Multiple Drug Analysis**: Check up to 5 drug interactions simultaneously
- **Simple Interface**: Intuitive design requires minimal training
- **Modern Technology Stack**: Built with current web technologies for optimal performance
- **API-First Design**: Easy to integrate with existing healthcare systems

# Drug Interaction API Documentation
For a short period, this API is hosted on Google Cloud at https://drug-interaction-api.uk.r.appspot.com/

## Base URL
```
/api/v1
```

---

## Authentication

Before using any of the server endpoints, a user needs to be authenticated. Authentication can either happen
via our client, or as follows if using a tool like Postman:
- Send a POST request to Google's sign-in service: https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={{$KEY}}
- For testing, you can use the following key instead of {{$KEY}} in the above URL: AIzaSyAefK0EcsWyOiy7RCWaOT54rBxJr9HgqMs
- The body of the request should contain the following:
  ```json
   {
    "email": "you_email",
    "password": "your_password",
    "returnSecureToken": "true"
  }
    ```
- For testing, you can use the following values:
- ```json
  "email": "test@columbia.edu",
  "password": "testuser",
  "returnSecureToken": "true"
  ```
- The POST request will return a payload containing a field called `idToken`
- This idToken will have to be included in the header of each API request as
- ```json
  "Authorization": "Bearer {{idToken}"
  ```

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

## Project Management
- Jira Project Board: https://4156-runtime-terrors.atlassian.net/jira/software/projects/SCRUM/boards/1

## Cloud Deployment
### Service Deployment
- Platform: Google Cloud Platform
- Region: uk.r.appspot.com
- Deployment URL: https://drug-interaction-api.uk.r.appspot.com/api/v1/

## Configuration Files
The following configuration files are required:
/  
└── firebase_config.json   # Firebase configuration

## Tools Used 🧰
This section includes notes on tools and technologies used in building this project.

### Core Technologies
* **Spring Boot (v3.2.2)**
  * Core framework for building the REST API
  * Provides dependency injection and web server capabilities

* **Firebase Admin SDK (v9.3.0)**
  * Cloud-based database for storing drug and interaction data
  * Provides real-time updates and scalable storage

* **Maven Package Manager**
  * Used for dependency management and project building
  * Handles all Java dependencies and build configurations

* **GitHub Actions CI**
  * Enabled via the "Actions" tab on GitHub
  * Ensures code builds successfully and tests pass with each push

### Code Quality Tools
* **Checkstyle (v3.2.0)**
  * Used for enforcing Google Java Style Guide
  * Run using: `mvn checkstyle:check`
  * Configuration includes test source directories

* **PMD (v3.21.2)**
  * Performs static analysis of Java code
  * Configured with quickstart ruleset
  * Includes Copy-Paste Detection (CPD)
  * Run using: `mvn pmd:check`

### Testing and Coverage
* **JUnit (Spring Boot Test)**
  * Framework for unit testing Java code
  * Included via spring-boot-starter-test
  * Tests run automatically in CI pipeline
  * Located in `src/test/java/dev/coms4156/project/druginteraction`

* **JaCoCo (v0.8.11)**
  * Generates code coverage reports
  * Integrated into Maven build lifecycle
  * Reports generated during prepare-package phase
 
* **Newman**
  * Runs Postman API tests automatically during Github CI 

### Deployment Tools
* **Google Cloud App Engine Maven Plugin (v2.7.0)**
  * Used for deploying to Google Cloud Platform
  * Configured for App Engine standard environment

### Required Versions
* Java: JDK 17 (required by Spring Boot 3.x)
* Maven: 3.9.9+ (for build system)
* Node.js: Latest LTS version (for client development)

### Development Tools
* **Postman**
  * Used for API endpoint testing
  * Collection of API tests available in repository
  * Provides example requests for all endpoints

All dependencies and versions are managed through Maven - see `pom.xml` for the complete list of dependencies and plugins.
