// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {
    getAuth,
    connectAuthEmulator,
    signInAnonymously,
} from "firebase/auth";
import firebase from 'firebase/compat/app';
import "./index.css";

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
