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