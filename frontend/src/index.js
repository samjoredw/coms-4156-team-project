// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {
    getAuth,
    connectAuthEmulator,
    signInWithEmailAndPassword
} from "firebase/auth";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

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

