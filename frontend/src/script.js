const baseUrl = 'https://drug-interaction-api.uk.r.appspot.com/';
const responseElement = document.getElementById('response');

function updateResponse(data) {
  if (typeof data === 'string') {
    responseElement.textContent = data;
  } else {
    responseElement.textContent = JSON.stringify(data, null, 2);
  }
}

function handleError(error) {
  responseElement.textContent = `Error: ${error.message || error}`;
}

async function handleFetch(url, options = {}) {
  try {
    const token = localStorage.getItem('userToken');

    // Ensure headers exist and add Authorization token
    options.headers = {
      ...(options.headers || {}),
      Authorization: `Bearer ${token}`, // Add the token
    };

    const res = await fetch(url, options);
    const text = await res.text();
    try {
      const data = JSON.parse(text);
      updateResponse(data);
    } catch (jsonError) {
      // Response is not JSON, display raw text
      updateResponse(text);
    }
  } catch (error) {
    handleError(error);
  }
}

// Fetch All Drugs
async function getAllDrugs() {
  await handleFetch(`${baseUrl}/drugs`);
}

// Fetch All Drug Interactions
async function getAllDrugInteractions() {
  const drugName = document.getElementById('drugName').value;
  if (!drugName) return alert('Please enter a drug name.');
  await handleFetch(`${baseUrl}/drugs/interactions?drugName=${encodeURIComponent(drugName)}`);
}

// Get Drug Info
async function getDrugInfo() {
  const drugName2 = document.getElementById('drugName2').value;
  if (!drugName2) return alert('Please enter a drug name.');
  await handleFetch(`${baseUrl}/drug?name=${encodeURIComponent(drugName2)}`);
}

// Add New Drug
async function addNewDrug() {
  const name = document.getElementById('newDrugName').value;
  const dosageForm = document.getElementById('newDrugDosageForm').value;
  const indications = document.getElementById('newDrugIndications').value;
  const contraindications = document.getElementById('newDrugContraindications').value;

  if (!name || !dosageForm) return alert('Please fill out all required fields.');

  const payload = {
    "name": name,
    "dosageForm": dosageForm,
    "indications": indications,
    "contraindications": contraindications,
    "createdBy": "admin",
    "updatedBy": "admin",
    "createdAt": new Date().toISOString(),
    "updatedAt": new Date().toISOString(),
  };

  await handleFetch(`${baseUrl}/drug/add`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

// Update Existing Drug
async function updateExistingDrug() {
  const drugName = document.getElementById('updateDrugName').value;
  const dosageForm = document.getElementById('updateDrugDosageForm').value || "";
  const indications = document.getElementById('updateDrugIndications').value || "";
  const contraindications = document.getElementById('updateDrugContraindications').value || "";

  if (!drugName) {
    alert('Please provide the drug name to update.');
    return;
  }

  const payload = {
    ...(dosageForm && { dosageForm }),
    ...(indications && { indications }),
    ...(contraindications && { contraindications }),
  };

  try {
    await handleFetch(`${baseUrl}/drug/update/${encodeURIComponent(drugName)}`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(payload),
    });
  } catch (error) {
    handleError(error);
  }
}


// Delete Drug
async function deleteDrug() {
  const drugName = document.getElementById('deleteDrugName').value;
  if (!drugName) return alert('Please enter a drug name.');
  await handleFetch(`${baseUrl}/drug/remove?name=${encodeURIComponent(drugName)}`, {
    method: 'DELETE',
  });
}

// Check Drug Interactions
async function checkInteractions() {
  const drugA = document.getElementById('interactionDrugA').value;
  const drugB = document.getElementById('interactionDrugB').value;
  if (!drugA || !drugB) return alert('Please enter both drug names.');
  await handleFetch(`${baseUrl}/interactions?drugA=${encodeURIComponent(drugA)}&drugB=${encodeURIComponent(drugB)}`);
}


// Check Multiple Drug Interactions
async function checkMultipleInteractions() {
  const drugA = document.getElementById('interactionMultiDrugA').value;
  const drugB = document.getElementById('interactionMultiDrugB').value;
  const drugC = document.getElementById('interactionMultiDrugC').value;
  const drugD = document.getElementById('interactionMultiDrugD').value;
  const drugE = document.getElementById('interactionMultiDrugE').value;

  if (!drugA || !drugB) {
    alert('Please enter at least two drug names.');
    return;
  }

  // Construct query string dynamically based on provided inputs
  const params = new URLSearchParams();
  if (drugA) params.append('drugA', drugA);
  if (drugB) params.append('drugB', drugB);
  if (drugC) params.append('drugC', drugC);
  if (drugD) params.append('drugD', drugD);
  if (drugE) params.append('drugE', drugE);

  // Fetch interactions
  try {
    await handleFetch(`${baseUrl}/get_interactions?${params.toString()}`);
  } catch (error) {
    handleError(error);
  }
}

// Add New Drug Interaction
async function addDrugInteraction() {
  const drugA = document.getElementById('addInteractionDrugA').value;
  const drugB = document.getElementById('addInteractionDrugB').value;
  const interactionEffect = document.getElementById('addInteractionEffect').value;

  if (!drugA || !drugB || !interactionEffect) return alert('Please fill out all required fields.');

  await handleFetch(`${baseUrl}/interactions/add?interactionEffect=${encodeURIComponent(interactionEffect)}&drugA=${encodeURIComponent(drugA)}&drugB=${encodeURIComponent(drugB)}`, {
    method: 'POST',
  });
}

// Delete Existing Drug Interaction
async function deleteDrugInteraction() {
  const drugA = document.getElementById('deleteInteractionDrugA').value;
  const drugB = document.getElementById('deleteInteractionDrugB').value;
  const interactionEffect = document.getElementById('deleteInteractionEffect').value;

  if (!drugA || !drugB || !interactionEffect) return alert('Please fill out all required fields.');

  await handleFetch(`${baseUrl}/interactions/delete?interactionEffect=${encodeURIComponent(interactionEffect)}&drugA=${encodeURIComponent(drugA)}&drugB=${encodeURIComponent(drugB)}`, {
    method: 'DELETE',
  });
}

// Update Existing Drug Interaction
async function updateDrugInteraction() {
  const interactionId = document.getElementById('updateInteractionId').value;
  const drugA = document.getElementById('updateInteractionDrugA').value;
  const drugB = document.getElementById('updateInteractionDrugB').value;
  const interactionEffect = document.getElementById('updateInteractionEffect').value;

  if (!interactionId || !drugA || !drugB || !interactionEffect) return alert('Please fill out all required fields.');

  await handleFetch(`${baseUrl}/interactions/update/${encodeURIComponent(interactionId)}?drugA=${encodeURIComponent(drugA)}&drugB=${encodeURIComponent(drugB)}&interactionEffect=${encodeURIComponent(interactionEffect)}`, {
    method: 'PATCH',
  });
}

