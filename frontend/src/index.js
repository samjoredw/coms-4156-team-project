// index.js
// Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyAefK0EcsWyOiy7RCWaOT54rBxJr9HgqMs",
    authDomain: "drug-interaction-api.firebaseapp.com",
    databaseURL: "https://drug-interaction-api-default-rtdb.firebaseio.com",
    projectId: "drug-interaction-api",
    storageBucket: "drug-interaction-api.appspot.com",
    messagingSenderId: "1063129233703",
    appId: "1:1063129233703:web:3b10cb724f33e2d1535102",
    measurementId: "G-ZB6S14BCRN",
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();

// API base URL
const API_BASE_URL = "https://drug-interaction-api.uk.r.appspot.com/api/v1";

async function fetchAPIWithStatus(endpoint, options = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

    // Check for success (2xx status codes)
    if (response.ok) {
        return "Success"; // Return a generic success message
    }

    // Handle error based on status codes
    switch (response.status) {
        case 400:
            throw new Error("Bad Request");
        case 401:
            throw new Error("Unauthorized: Please log in");
        case 403:
            throw new Error("Forbidden: Access denied");
        case 404:
            throw new Error("Not Found: Resource does not exist");
        case 409:
            throw new Error("Conflict: Resource already exists");
        case 500:
            throw new Error("Internal Server Error");
        default:
            throw new Error(`Unexpected Error: ${response.statusText}`);
    }
}

// Utility function: Fetch API wrapper
async function fetchAPIWithJSON(endpoint, options = {}) {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

    if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return await response.json(); // Parse and return JSON
    }

    throw new Error("Unexpected response type; expected JSON");
}


// Utility function: Display toast notifications
function showToast(message, type) {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => toast.classList.remove("show"), 3000);
}

// Utility function: Clear form fields
function clearForm(...ids) {
    ids.forEach((id) => {
        const field = document.getElementById(id);
        if (field) field.value = "";
    });
}

// Auth state observer
auth.onAuthStateChanged((user) => {
    const isColumbiaEmail = user && user.email.endsWith("@columbia.edu");
    document
        .getElementById("loginBtn")
        .classList.toggle("hidden", isColumbiaEmail);
    document
        .getElementById("logoutBtn")
        .classList.toggle("hidden", !isColumbiaEmail);
    document
        .querySelector(".admin-tab")
        .classList.toggle("hidden", !isColumbiaEmail);
});

// Login with Google
document.getElementById("loginBtn").addEventListener("click", () => {
    const provider = new firebase.auth.GoogleAuthProvider();
    auth.signInWithPopup(provider)
        .then((result) => {
            if (!result.user.email.endsWith("@columbia.edu")) {
                auth.signOut();
                showToast(
                    "Access restricted to Columbia University emails",
                    "error"
                );
            } else {
                showToast("Logged in successfully", "success");
                document.querySelector(".admin-tab").click();
            }
        })
        .catch((error) => showToast(error.message, "error"));
});

// Logout
document.getElementById("logoutBtn").addEventListener("click", () => {
    auth.signOut()
        .then(() => showToast("Logged out successfully", "success"))
        .catch((error) => showToast(error.message, "error"));
});

// Add multiple input fields for drugs
function setupDrugInputs() {
    const container = document.getElementById("drugInputsContainer");
    const addBtn = document.querySelector(".add-drug-btn");

    // Add new input field on button click (starting from 3rd slot)
    addBtn.addEventListener("click", () => {
        const inputCount = container.querySelectorAll(".input-group").length;
        if (inputCount < 5) {
            const newInput = createDrugInput(); // Dynamically create input with a remove button
            container.appendChild(newInput);
        } else {
            showToast("You can only add up to 5 drugs", "error");
        }
    });
}

// Create a single drug input group
function createDrugInput() {
    const group = document.createElement("div");
    group.className = "input-group";

    const input = document.createElement("input");
    input.type = "text";
    input.className = "search-input drug-input";
    input.setAttribute("list", "drugList");
    input.placeholder = "Enter drug name";

    const removeBtn = document.createElement("button");
    removeBtn.className = "remove-drug-btn";
    removeBtn.innerHTML = '<i class="fas fa-minus"></i>';
    removeBtn.addEventListener("click", () => {
        group.remove();
    });

    group.appendChild(input);
    group.appendChild(removeBtn);

    return group;
}

// Event listener for checking drug interactions
async function handleCheckInteraction() {
    const drugInputs = document.querySelectorAll(".drug-input");
    const drugs = Array.from(drugInputs)
        .map((input) => input.value.trim())
        .filter(Boolean); // Filter out empty inputs

    if (drugs.length < 2) {
        showToast("Please enter at least two drugs", "error");
        return;
    }

    try {
        const queryParams = drugs
            .map(
                (drug, index) =>
                    `drug${String.fromCharCode(
                        65 + index
                    )}=${encodeURIComponent(drug)}`
            )
            .join("&");

        const response = await fetchAPIWithJSON(`/get_interactions?${queryParams}`);
        displayInteractionResult(response);
    } catch (error) {
        showToast(error.message, "error");
    }
}

// Display drug interaction result
function displayInteractionResult(data) {
    const resultContainer = document.getElementById("interactionResult");

    if (!resultContainer) {
        console.error("Result container not found in DOM.");
        return;
    }

    // Clear previous results
    resultContainer.innerHTML = "";
    resultContainer.style.display = "block"; // Ensure visibility

    // Log the received data for debugging
    console.log("Data received:", data);

    // Check if interactions exist
    const hasInteractions = data.interactions?.length > 0;

    if (hasInteractions) {
        const interactionsHTML = data.interactions
            .map(
                (interaction) => `
            <div class="interaction-item">
                <h3>⚠️ Interaction Found</h3>
                <p><strong>${interaction.drugPair}</strong></p>
                <p>${interaction.interactionEffect}</p>
            </div>
        `
            )
            .join("");
        resultContainer.innerHTML += `
            <div class="interaction-section error">
                <h2>Interactions:</h2>
                ${interactionsHTML}
            </div>
        `;
        resultContainer.className = "result-container error"; // Add error styling
    } else if (data.noInteractions?.length > 0) {
        // Only show "No Interactions" if no interactions are found
        const noInteractionsHTML = data.noInteractions
            .map(
                (noInteraction) => `
            <div class="interaction-item">
                <h3>✓ No Interaction</h3>
                <p><strong>${noInteraction.drugPair}</strong></p>
                <p>${noInteraction.interactionEffect}</p>
            </div>
        `
            )
            .join("");
        resultContainer.innerHTML += `
            <div class="interaction-section success">
                <h2>No Interactions:</h2>
                ${noInteractionsHTML}
            </div>
        `;
        resultContainer.className = "result-container success"; // Add success styling
    } else {
        // Fallback if no data is available
        resultContainer.innerHTML = `
            <div class="interaction-section success">
                <h3>✓ No interactions found between the provided drugs.</h3>
            </div>
        `;
        resultContainer.className = "result-container success"; // Add success styling
    }
}

// Event listener for adding a new drug
async function handleAddDrug() {
    const name = document.getElementById("newDrugName").value;
    const description = document.getElementById("newDrugDescription").value;

    if (!name || !description) {
        showToast("Please fill in all fields", "error");
        return;
    }

    try {
        const idToken = await auth.currentUser.getIdToken();
        await fetchAPIWithStatus("/drug/add", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${idToken}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ name, description }),
        });
        showToast("Drug added successfully", "success");
        clearForm("newDrugName", "newDrugDescription");
    } catch (error) {
        showToast(error.message, "error");
    }

    populateManageDrugDropdowns();
}

async function fetchDrugList() {
    try {
        const response = await fetchAPIWithJSON("/drugs");
        const drugs = response; // Assume response is an array of drug names

        // Populate the datalist for drug search
        const datalist = document.getElementById("drugList");
        datalist.innerHTML = drugs
            .map((drug) => `<option value="${drug}">`)
            .join("");
    } catch (error) {
        showToast("Failed to fetch drug list", "error");
        console.error(error);
    }
}

async function fetchDrugInfo() {
    const drugName = document.getElementById("drugSearch").value.trim();
    const resultContainer = document.getElementById("drugInfoResult");

    if (!drugName) {
        showToast("Please enter a drug name", "error");
        return;
    }

    try {
        const response = await fetchAPIWithJSON(
            `/drug?name=${encodeURIComponent(drugName)}`
        );

        // Check if the response is an empty object
        if (Object.keys(response).length === 0) {
            resultContainer.innerHTML = `<p>Drug not found</p>`;
            resultContainer.className = "result-container error"; // Apply error styling
            return;
        }

        // Display drug information
        resultContainer.innerHTML = `
            <h3>Drug Information</h3>
            <p><strong>Name:</strong> ${response.name}</p>
            <p><strong>Indications:</strong> ${
                response.indications || "N/A"
            }</p>
            <p><strong>Contraindications:</strong> ${
                response.contraindications || "N/A"
            }</p>
            <p><strong>Dosage Form:</strong> ${response.dosageForm || "N/A"}</p>
            <p><strong>Side Effects:</strong> ${
                response.sideEffects || "N/A"
            }</p>
            <p><strong>Created At:</strong> ${response.createdAt || "N/A"}</p>
            <p><strong>Updated At:</strong> ${response.updatedAt || "N/A"}</p>
        `;
        resultContainer.className = "result-container success"; // Apply success styling
    } catch (error) {
        resultContainer.innerHTML = `<p>${
            error.message || "An error occurred while fetching drug information"
        }</p>`;
        resultContainer.className = "result-container error"; // Apply error styling
        console.error(error);
    }
}

document.querySelectorAll(".subtab-btn").forEach((tab) =>
    tab.addEventListener("click", () => {
        const action = tab.dataset.action;

        document.querySelectorAll(".sub-panel").forEach((panel) => {
            panel.classList.toggle("active", panel.dataset.panel === action);
        });

        document.querySelectorAll(".subtab-btn").forEach((btn) => {
            btn.classList.toggle("active", btn === tab);
        });
    })
);

// Add Interaction
document
    .getElementById("addInteraction")
    .addEventListener("click", async () => {
        const drugA = document.getElementById("interactionDrugA").value.trim();
        const drugB = document.getElementById("interactionDrugB").value.trim();
        const effect = document
            .getElementById("interactionEffect")
            .value.trim();

        if (!drugA || !drugB || !effect) {
            showToast("Please provide all fields", "error");
            return;
        }

        try {
            const idToken = await auth.currentUser.getIdToken();
            await fetchAPIWithStatus(
                `/interactions/add?drugA=${encodeURIComponent(
                    drugA
                )}&drugB=${encodeURIComponent(
                    drugB
                )}&interactionEffect=${encodeURIComponent(effect)}`,
                {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                        "Content-Type": "application/json",
                    },
                }
            );
            showToast("Interaction added successfully", "success");
        } catch (error) {
            showToast(error.message, "error");
        }
    });

// Update Interaction
document
    .getElementById("updateInteraction")
    .addEventListener("click", async () => {
        const drugA = document
            .getElementById("updateInteractionDrugA")
            .value.trim()
            .toString();
        const drugB = document
            .getElementById("updateInteractionDrugB")
            .value.trim()
            .toString();
        const effect = document
            .getElementById("updateInteractionEffect")
            .value.trim()
            .toString();

        if (!drugA || !drugB || !effect) {
            showToast("Please provide all fields", "error");
            return;
        }

        try {
            const idToken = await auth.currentUser.getIdToken();
            await fetchAPIWithStatus(
                `/interactions/update/?drugA=${encodeURIComponent(
                    drugA
                )}&drugB=${encodeURIComponent(
                    drugB
                )}&interactionEffect=${encodeURIComponent(effect)}`,
                {
                    method: "PATCH",
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                    },
                }
            );

            showToast("Interaction updated successfully", "success");
        } catch (error) {
            showToast(error.message, "error");
        }
    });

// Delete Interaction
document
    .getElementById("deleteInteraction")
    .addEventListener("click", async () => {
        const drugA = document
            .getElementById("deleteInteractionDrugA")
            .value.trim();
        const drugB = document
            .getElementById("deleteInteractionDrugB")
            .value.trim();
        const effect = document
            .getElementById("deleteInteractionEffect")
            .value.trim();

        if (!drugA || !drugB || !effect) {
            showToast("Please provide all fields", "error");
            return;
        }

        try {
            const idToken = await auth.currentUser.getIdToken();
            await fetchAPIWithStatus(
                `/interactions/delete?drugA=${encodeURIComponent(
                    drugA
                )}&drugB=${encodeURIComponent(
                    drugB
                )}&interactionEffect=${encodeURIComponent(effect)}`,
                {
                    method: "DELETE",
                    headers: {
                        Authorization: `Bearer ${idToken}`,
                    },
                }
            );
            showToast("Interaction deleted successfully", "success");
        } catch (error) {
            showToast(error.message, "error");
        }
    });

// Add Drug
document.getElementById("addDrug").addEventListener("click", async () => {
    const name = document.getElementById("newDrugName").value.trim();
    const indications = document.getElementById("newDrugIndications").value.trim();
    const contraindications = document.getElementById("newDrugContraindications").value.trim();
    const dosageForm = document.getElementById("newDrugDosageForm").value.trim();
    const sideEffects = document.getElementById("newDrugSideEffects").value.trim();

    if (!name || !indications || !contraindications || !dosageForm || !sideEffects) {
        showToast("Please fill in all fields", "error");
        return;
    }

    try {
        const idToken = await auth.currentUser.getIdToken();
        await fetchAPIWithStatus("/drug/add", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${idToken}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                name,
                indications,
                contraindications,
                dosageForm,
                sideEffects,
            }),
        });
        showToast("Drug added successfully", "success");
        clearForm("newDrugName", "newDrugIndications", "newDrugContraindications", "newDrugDosageForm", "newDrugSideEffects");
    } catch (error) {
        showToast(error.message, "error");
    }
});


// Update Drug
document.getElementById("updateDrug").addEventListener("click", async () => {
    const name = document.getElementById("updateDrugSelect").value.trim();
    const indications = document.getElementById("updateDrugIndications").value.trim();
    const contraindications = document.getElementById("updateDrugContraindications").value.trim();
    const dosageForm = document.getElementById("updateDrugDosageForm").value.trim();
    const sideEffects = document.getElementById("updateDrugSideEffects").value.trim();

    if (!name || !indications || !contraindications || !dosageForm || !sideEffects) {
        showToast("Please fill in all fields", "error");
        return;
    }

    try {
        const idToken = await auth.currentUser.getIdToken();
        await fetchAPIWithStatus(`/drug/update/${encodeURIComponent(name)}`, {
            method: "PATCH",
            headers: {
                Authorization: `Bearer ${idToken}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                indications,
                contraindications,
                dosageForm,
                sideEffects,
            }),
        });
        showToast("Drug updated successfully", "success");
        clearForm("updateDrugIndications", "updateDrugContraindications", "updateDrugDosageForm", "updateDrugSideEffects");
    } catch (error) {
        showToast(error.message, "error");
    }
});

// Delete Drug
document.getElementById("deleteDrug").addEventListener("click", async () => {
    const name = document.getElementById("deleteDrugSelect").value.trim();

    if (!name) {
        showToast("Please provide a drug name", "error");
        return;
    }
    
    try {
        const idToken = await auth.currentUser.getIdToken();
        await fetchAPIWithStatus(`/drug/remove?name=${encodeURIComponent(name)}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        showToast("Drug deleted successfully", "success");
    } catch (error) {
        showToast(error.message, "error");
    }
    populateManageDrugDropdowns();
});

async function populateManageDrugDropdowns() {
    try {
        const drugs = await fetchAPIWithJSON("/drugs"); // Ensure you're using the JSON-fetching function
        console.log("Fetched drugs:", drugs); // Log the response for debugging
        const updateDropdown = document.getElementById("updateDrugSelect");
        const deleteDropdown = document.getElementById("deleteDrugSelect");

        // Ensure elements exist
        if (!updateDropdown || !deleteDropdown) {
            console.error("Dropdown elements not found in the DOM.");
            return;
        }

        // Clear existing options
        updateDropdown.innerHTML = "";
        deleteDropdown.innerHTML = "";

        // Populate dropdowns
        drugs.forEach((drug) => {
            const option = document.createElement("option");
            option.value = drug;
            option.textContent = drug;
            updateDropdown.appendChild(option);

            const deleteOption = option.cloneNode(true);
            deleteDropdown.appendChild(deleteOption);
        });

        console.log("Dropdowns populated successfully");
    } catch (error) {
        console.error("Failed to populate manage drug dropdowns:", error);
        showToast("Failed to load drug list for management", "error");
    }
}

function setupManagementTabs() {
    const managementTabs = document.querySelectorAll(".management-tab");
    const managementPanels = document.querySelectorAll(".management-panel");

    managementTabs.forEach((tab) => {
        tab.addEventListener("click", () => {
            const action = tab.dataset.action;

            // Toggle active class for the tabs
            managementTabs.forEach((t) =>
                t.classList.toggle("active", t === tab)
            );

            // Toggle active class for the panels
            managementPanels.forEach((panel) =>
                panel.classList.toggle("active", panel.dataset.panel === action)
            );
        });
    });
}

function setupDrugSearch() {
    document
        .getElementById("searchDrugBtn")
        .addEventListener("click", fetchDrugInfo);

    document
        .getElementById("drugSearch")
        .addEventListener("keypress", (event) => {
            if (event.key === "Enter") {
                fetchDrugInfo();
            }
        });

    // Fetch the drug list for autofill
    fetchDrugList();
}

// Setup tabs
function setupTabs() {
    document.querySelectorAll(".tab-btn").forEach((btn) =>
        btn.addEventListener("click", () => {
            const tabId = btn.dataset.tab;

            console.log(`Switching to tab: ${tabId}`); // Debugging log

            // Toggle active class for tab buttons
            document.querySelectorAll(".tab-btn").forEach((tabBtn) => {
                tabBtn.classList.toggle("active", tabBtn === btn);
            });

            // Toggle active class for tab content
            document.querySelectorAll(".tab-content").forEach((content) => {
                content.classList.toggle("active", content.id === tabId);
            });
        })
    );
}

// Setup event listeners
function setupEventListeners() {
    const checkInteractionBtn = document.getElementById("checkInteraction");
    if (checkInteractionBtn) {
        checkInteractionBtn.addEventListener("click", handleCheckInteraction);
    }

    const addDrugBtn = document.getElementById("addDrug");
    if (addDrugBtn) {
        addDrugBtn.addEventListener("click", handleAddDrug);
    }

    setupTabs();
    setupDrugInputs();
    setupDrugSearch();
    populateManageDrugDropdowns();
}

// Initialize everything on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
    setupEventListeners();
    setupManagementTabs();
});
