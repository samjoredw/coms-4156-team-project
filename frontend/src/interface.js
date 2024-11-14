import "./interface.css";
import { getAuth, signOut } from "firebase/auth";
import firebase from "firebase/compat/app";

class DrugInteractionChecker {
    constructor() {
        this.apiBase = "http://localhost:8080/api/v1";
        this.maxDrugs = 5;
        this.firebaseConfig = {
            apiKey: "AIzaSyAefK0EcsWyOiy7RCWaOT54rBxJr9HgqMs",
            authDomain: "drug-interaction-api.firebaseapp.com",
            databaseURL: "https://drug-interaction-api-default-rtdb.firebaseio.com",
            projectId: "drug-interaction-api",
            storageBucket: "drug-interaction-api.appspot.com",
            messagingSenderId: "1063129233703",
            appId: "1:1063129233703:web:3b10cb724f33e2d1535102",
            measurementId: "G-ZB6S14BCRN"
        };
        this.init();
    }

    async init() {
        firebase.initializeApp(this.firebaseConfig);

        this.drugInputs = document.getElementById("drugInputs");
        this.addDrugBtn = document.getElementById("addDrugBtn");
        this.checkBtn = document.getElementById("checkBtn");
        this.results = document.getElementById("results");
        this.drugsList = document.getElementById("drugsList");
        this.idToken = localStorage.getItem("userToken");
        this.userInfo = localStorage.getItem("user");
        this.auth = getAuth();

        this.addDrugBtn.addEventListener("click", () =>
            this.addDrugInput()
        );
        this.checkBtn.addEventListener("click", () =>
            this.checkInteractions()
        );

        await this.fetchDrugsList();
    }

    async fetchWithTimeout(url, options = {}) {
        const timeout = 5000;
        const controller = new AbortController();
        const id = setTimeout(() => controller.abort(), timeout);

        const defaultOptions = {
            mode: "cors",
        };

        try {
            const response = await fetch(url, {
                ...defaultOptions,
                ...options,
                signal: controller.signal,
            });

            clearTimeout(id);

            if (response.status === 403) {
                throw new Error(
                    "Access forbidden. Please check your authentication."
                );
            }

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(
                    `HTTP error! status: ${response.status}, message: ${errorText}`
                );
            }

            return response;
        } catch (error) {
            clearTimeout(id);
            if (error.name === "AbortError") {
                throw new Error("Request timed out");
            }
            throw error;
        }
    }

    async fetchDrugsList() {
        try {
            const response = await this.fetchWithTimeout(
                `${this.apiBase}/drugs`
            );
            const drugs = await response.json();

            this.drugsList.innerHTML = drugs
                .map((drug) => `<option value="${drug}">`)
                .join("");
        } catch (error) {
            this.showError(
                `Failed to fetch drugs list: ${error.message}`
            );
        }
    }

    addDrugInput() {
        const inputs = this.drugInputs.querySelectorAll("input");
        if (inputs.length >= this.maxDrugs) return;

        const group = document.createElement("div");
        group.className = "drug-input-group";

        group.innerHTML = `
            <input type="text" placeholder="Enter drug ${
            inputs.length + 1
        }" list="drugsList">
            <button class="btn-outline remove-btn" onclick="this.parentElement.remove()">×</button>
        `;

        this.drugInputs.appendChild(group);
    }

    async checkInteractions() {
        const inputs = Array.from(
            this.drugInputs.querySelectorAll("input")
        );
        const drugs = inputs
            .map((input) => input.value.trim())
            .filter(Boolean);

        if (drugs.length < 2) {
            this.showError("Please select at least two drugs");
            return;
        }

        this.setLoading(true);

        try {
            const params = new URLSearchParams();
            drugs.forEach((drug, index) => {
                params.append(
                    `drug${String.fromCharCode(65 + index)}`,
                    drug
                );
            });

            if (!this.idToken) {
                this.showError("User is not authenticated");
                return;
            }

            // Include the idToken for this request
            const response = await this.fetchWithTimeout(
                `${this.apiBase}/get_interactions?${params}`,
                {
                    headers: {
                        Authorization: `Bearer ${this.idToken}`,
                        Accept: "application/json",
                        "Content-Type": "application/json",
                    },
                }
            );
            const data = await response.json();
            this.displayResults(data);
        } catch (error) {
            this.showError(
                `Failed to check drug interactions: ${error.message}`
            );
        } finally {
            this.setLoading(false);
        }
    }

    setLoading(isLoading) {
        this.checkBtn.disabled = isLoading;
        this.checkBtn.innerHTML = isLoading
            ? '<div class="loading"></div>Checking...'
            : "Check Interactions";
    }

    showError(message) {
        this.results.innerHTML = `
            <div class="alert alert-error">
                ${message}
            </div>
        `;
    }

    showMessage(message, type = "error") {
        this.results.innerHTML = `
            <div class="alert alert-${type}">
                ${message}
            </div>
        `;
    }

    displayResults(data) {
        let html = "";

        if (data.interactions?.length) {
            html += `
                <div class="alert alert-error">
                    <strong>Found Interactions</strong>
                    <ul class="interaction-list">
                        ${data.interactions
                .map(
                    (interaction) => `
                            <li class="interaction-item">
                                <strong>${interaction.drugPair}:</strong> 
                                ${interaction.interactionEffect}
                            </li>
                        `
                )
                .join("")}
                    </ul>
                </div>
            `;
        }

        if (data.noInteractions?.length) {
            html += `
                <div class="alert alert-success">
                    <strong>No Interactions Found</strong>
                    <ul class="interaction-list">
                        ${data.noInteractions
                .map(
                    (interaction) => `
                            <li class="interaction-item">
                                <strong>${interaction.drugPair}:</strong> 
                                ${interaction.interactionEffect}
                            </li>
                        `
                )
                .join("")}
                    </ul>
                </div>
            `;
        }

        this.results.innerHTML = html;
    }
}

const app = new DrugInteractionChecker();