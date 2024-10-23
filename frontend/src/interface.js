import "./interface.css";
import { getAuth, signOut } from "firebase/auth";
import firebase from "firebase/compat/app";

// Initialize firebase in the new page again since we use auth function
const firebaseConfig = {
    apiKey: "AIzaSyAefK0EcsWyOiy7RCWaOT54rBxJr9HgqMs",
    authDomain: "drug-interaction-api.firebaseapp.com",
    databaseURL: "https://drug-interaction-api-default-rtdb.firebaseio.com",
    projectId: "drug-interaction-api",
    storageBucket: "drug-interaction-api.appspot.com",
    messagingSenderId: "1063129233703",
    appId: "1:1063129233703:web:3b10cb724f33e2d1535102",
    measurementId: "G-ZB6S14BCRN"
};

firebase.initializeApp(firebaseConfig);

// Reference the DOM elements
var drug1Input = document.getElementById('drug1');
var drug2Input = document.getElementById('drug2');
const getInteractionButton = document.getElementById('get-interaction');
var interactionResult = document.getElementById('interaction-result');
const signOutButton = document.getElementById('signout');
const userInfo = document.getElementById('user-info');
const status = document.getElementById('sign-in-status');
var idToken = localStorage.getItem("userToken");
var user = localStorage.getItem("user");

const apiEndpoint = 'https://drug-interaction-api.uk.r.appspot.com';

// Initialize Firebase Auth
const auth = getAuth();

// Handle Get Interaction Button click
getInteractionButton.addEventListener('click', () => {
    const drug1 = drug1Input.value.trim();
    const drug2 = drug2Input.value.trim();

    if (user) {
        // Validate that both drugs are provided
        if (!drug1 || !drug2) {
            interactionResult.textContent = 'Please enter both drug names.';
            return;
        }

        // Construct the API URL with query parameters
        const apiUrl = `${apiEndpoint}/interactions?drugA=${encodeURIComponent(drug1)}&drugB=${encodeURIComponent(drug2)}`;
        console.log(apiUrl);

        // Send the GET request to the API with the ID token in the Authorization header
        fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${idToken}`  // Include the ID token in the Authorization header
            },
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                // Handle the response from the API
                if (data.interactions) {
                    interactionResult.textContent = `Interaction Found: ${data.interactions.interactionEffect}`;
                } else {
                    interactionResult.textContent = `No Interaction: ${data.noInteractions.interactionEffect}`;
                }
            })
            .catch(error => {
                console.error('Error fetching drug interaction:', error);
                interactionResult.textContent = 'Error fetching interaction. Please try again.';
            });
    } else {
        // User is not authenticated, show error
        interactionResult.textContent = 'You need to be signed in to get interaction details.';
    }
});

// Handle sign out
signOutButton.addEventListener('click', () => {
    console.log("signout button clicked");
    signOut(auth).then(() => {
        console.log('User signed out');
        localStorage.clear();
        window.location.href = '/';
    }).catch((error) => {
        console.error('Error signing out:', error);
    });
});