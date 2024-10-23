// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {
    getAuth,
    connectAuthEmulator,
    signInAnonymously,
} from "firebase/auth";
import firebase from 'firebase/compat/app';
import * as firebaseui from 'firebaseui';
import 'firebaseui/dist/firebaseui.css';  // Make sure to include FirebaseUI CSS
import './index.css';

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
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

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);
connectAuthEmulator(auth, 'http://localhost:9099');

// Initialize the FirebaseUI Widget using Firebase.
const ui = new firebaseui.auth.AuthUI(auth);
const uiConfig = {
    signInSuccessUrl: '/',  // The URL to redirect after successful login
    signInFlow: "popup",
    signInOptions: [
        // TODO: Remove the providers you don't need for your app.
        firebase.auth.GoogleAuthProvider.PROVIDER_ID,
        // {
        //     provider: firebase.auth.FacebookAuthProvider.PROVIDER_ID,
        //     scopes :[
        //         'public_profile',
        //         'email',
        //         'user_likes',
        //         'user_friends'
        //     ]
        // },
        // firebase.auth.TwitterAuthProvider.PROVIDER_ID,
        // firebase.auth.GithubAuthProvider.PROVIDER_ID,
        {
            provider: firebase.auth.EmailAuthProvider.PROVIDER_ID,
            requireDisplayName: true,
            signInMethod: "password",
            disableSignUp: {
                status: false
            }
        },
        {
            provider: firebase.auth.PhoneAuthProvider.PROVIDER_ID,
            recaptchaParameters: {
                size: 'normal', // 'invisible' or 'compact'
            },
        },
        // {
        //     provider: 'microsoft.com',
        //     loginHintKey: 'login_hint'
        // },
        {
            provider: 'apple.com',
        },
        // cannot work since the v9 has compatability issue, implemented manually below
        // firebaseui.auth.AnonymousAuthProvider.PROVIDER_ID,
    ],
    callbacks: {
        signInSuccessWithAuthResult: function(authResult, redirectUrl) {
            console.log('User signed in:', authResult.user);

            // Obtain the auth token
            authResult.user.getIdToken().then((idToken) => {
                localStorage.setItem('userToken', idToken);  // Update token in localStorage if needed
                console.log('JWT ID Token:', idToken);
                // You can now send this token to your backend server for verification later
            }).catch((error) => {
                console.error('Error getting ID token:', error);
            });
            return true;  // Redirect as specified in signInSuccessUrl
        },
        signInFailure: function(error) {
            console.error('Sign-in failed:', error);
            return false;  // Optional: handle errors
        }
    },
};

ui.start('#firebaseui-auth-container', uiConfig);

// Add event listener for the anonymous sign-in button manually
document.getElementById('guest-sign-in').addEventListener('click', () => {
    signInAnonymously(auth)
        .then((userCredential) => {
            console.log('Signed in anonymously:', userCredential.user);
            // Redirect or display something after successful sign-in
        })
        .catch((error) => {
            console.error('Error during anonymous sign-in:', error);
        });
});

auth.onAuthStateChanged(user => {
    if (user) {
        // User is signed in
        user.getIdToken().then((idToken) => {
            localStorage.setItem('userToken', idToken);  // Update token in localStorage if needed
            console.log('Token updated in localStorage:', idToken);
        });
    } else {
        // User is signed out, clear the token
        localStorage.removeItem('userToken');  // Remove token from localStorage
        console.log('User is signed out, token removed from localStorage');
    }
});
