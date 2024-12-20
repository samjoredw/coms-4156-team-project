#!/bin/bash

# First, create a proper JSON file
cat >drugs.json <<'EOL'
[
    {
        "name": "Aspirin",
        "dosageForm": "Tablet",
        "indications": "Pain relief, fever reduction, and prevention of heart attacks and strokes",
        "contraindications": "Active stomach ulcers, bleeding disorders, or aspirin allergy",
        "sideEffects": "Stomach irritation, increased bleeding risk, and tinnitus",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Lisinopril",
        "dosageForm": "Tablet",
        "indications": "Treatment of high blood pressure and heart failure",
        "contraindications": "Pregnancy, history of angioedema, or bilateral renal artery stenosis",
        "sideEffects": "Dry cough, dizziness, and hyperkalemia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Simvastatin",
        "dosageForm": "Tablet",
        "indications": "Lowering cholesterol and reducing the risk of cardiovascular disease",
        "contraindications": "Active liver disease or unexplained persistent elevations in liver enzymes",
        "sideEffects": "Muscle pain, liver enzyme abnormalities, and increased blood sugar",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Levothyroxine",
        "dosageForm": "Tablet",
        "indications": "Treatment of hypothyroidism and thyroid hormone replacement",
        "contraindications": "Thyrotoxicosis or uncorrected adrenal insufficiency",
        "sideEffects": "Palpitations, insomnia, and weight loss",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Ciprofloxacin",
        "dosageForm": "Tablet",
        "indications": "Treatment of bacterial infections, including urinary tract and respiratory infections",
        "contraindications": "History of tendon disorders or concurrent use with tizanidine",
        "sideEffects": "Tendon rupture, peripheral neuropathy, and photosensitivity",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Metronidazole",
        "dosageForm": "Tablet",
        "indications": "Treatment of anaerobic bacterial and protozoal infections",
        "contraindications": "First trimester of pregnancy or concurrent use with disulfiram",
        "sideEffects": "Metallic taste, nausea, and disulfiram-like reaction with alcohol",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Fluoxetine",
        "dosageForm": "Tablet",
        "indications": "Treatment of depression, obsessive-compulsive disorder, and bulimia nervosa",
        "contraindications": "Use of MAO inhibitors within 14 days or pimozide",
        "sideEffects": "Insomnia, and anxiety",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Digoxin",
        "dosageForm": "Tablet",
        "indications": "Treatment of heart failure and atrial fibrillation",
        "contraindications": "Ventricular fibrillation or hypersensitivity to digoxin",
        "sideEffects": "Nausea, visual disturbances, and cardiac arrhythmias",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Warfarin",
        "dosageForm": "Tablet",
        "indications": "Prevention and treatment of blood clots and stroke prevention in atrial fibrillation",
        "contraindications": "Active major bleeding or pregnancy",
        "sideEffects": "Bleeding, skin necrosis, and purple toe syndrome",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Omeprazole",
        "dosageForm": "Tablet",
        "indications": "Treatment of gastroesophageal reflux disease (GERD) and peptic ulcers",
        "contraindications": "Hypersensitivity to proton pump inhibitors",
        "sideEffects": "Headache, abdominal pain, and vitamin B12 deficiency",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Clarithromycin",
        "dosageForm": "Tablet",
        "indications": "Treatment of respiratory tract infections and Helicobacter pylori eradication",
        "contraindications": "History of QT prolongation or concurrent use with certain drugs metabolized by CYP3A4",
        "sideEffects": "Taste alterations, abdominal pain, and hepatotoxicity",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Tramadol",
        "dosageForm": "Tablet",
        "indications": "Management of moderate to moderately severe pain",
        "contraindications": "Acute intoxication with alcohol, hypnotics, or psychotropic drugs",
        "sideEffects": "Dizziness, constipation, and risk of serotonin syndrome",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Amlodipine",
        "dosageForm": "Tablet",
        "indications": "Treatment of hypertension and coronary artery disease",
        "contraindications": "Cardiogenic shock or severe aortic stenosis",
        "sideEffects": "Peripheral edema, flushing, and palpitations",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Cyclosporine",
        "dosageForm": "Tablet",
        "indications": "Prevention of organ transplant rejection and treatment of severe autoimmune disorders",
        "contraindications": "Uncontrolled hypertension or malignancy",
        "sideEffects": "Nephrotoxicity, hypertension, and increased risk of infections",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Allopurinol",
        "dosageForm": "Tablet",
        "indications": "Prevention of gout and treatment of kidney stones",
        "contraindications": "Acute gout attack or severe renal impairment",
        "sideEffects": "Skin rash, Stevens-Johnson syndrome, and liver function abnormalities",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Carbamazepine",
        "dosageForm": "Tablet",
        "indications": "Treatment of epilepsy, trigeminal neuralgia, and bipolar disorder",
        "contraindications": "Bone marrow depression or use of MAO inhibitors within 14 days",
        "sideEffects": "Dizziness, ataxia, and aplastic anemia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Fluconazole",
        "dosageForm": "Tablet",
        "indications": "Treatment of fungal infections, including candidiasis and cryptococcal meningitis",
        "contraindications": "Coadministration with terfenadine in patients receiving fluconazole doses of 400 mg or higher",
        "sideEffects": "Nausea, headache, and liver toxicity",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Phenytoin",
        "dosageForm": "Tablet",
        "indications": "Control of seizures and prevention of seizures during neurosurgery",
        "contraindications": "Sinus bradycardia, sinoatrial block, or second- and third-degree AV block",
        "sideEffects": "Gingival hyperplasia, cerebellar ataxia, and osteomalacia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Rifampin",
        "dosageForm": "Tablet",
        "indications": "Treatment of tuberculosis and prevention of meningococcal disease",
        "contraindications": "Jaundice or known hypersensitivity to rifamycins",
        "sideEffects": "Orange discoloration of body fluids, hepatotoxicity, and drug interactions",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Theophylline",
        "dosageForm": "Tablet",
        "indications": "Treatment of asthma and chronic obstructive pulmonary disease (COPD)",
        "contraindications": "Hypersensitivity to xanthine derivatives",
        "sideEffects": "Nausea, tachycardia, and seizures at toxic levels",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "ACE inhibitors",
        "dosageForm": "Tablet",
        "indications": "Treatment of hypertension, heart failure, and diabetic nephropathy",
        "contraindications": "History of angioedema related to previous ACE inhibitor therapy",
        "sideEffects": "Dry cough, hyperkalemia, and acute kidney injury",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Metformin",
        "dosageForm": "Tablet",
        "indications": "Treatment of type 2 diabetes and polycystic ovary syndrome",
        "contraindications": "Severe renal impairment or acute or chronic metabolic acidosis",
        "sideEffects": "Gastrointestinal disturbances, lactic acidosis, and vitamin B12 deficiency",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Sulfamethoxazole",
        "dosageForm": "Tablet",
        "indications": "Treatment of urinary tract infections and prevention of Pneumocystis pneumonia",
        "contraindications": "Known hypersensitivity to sulfonamides or trimethoprim",
        "sideEffects": "Skin rash, photosensitivity, and bone marrow suppression",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Ibuprofen",
        "dosageForm": "Tablet",
        "indications": "Pain relief, fever reduction, and treatment of inflammatory conditions",
        "contraindications": "History of gastrointestinal bleeding or ulceration",
        "sideEffects": "Gastrointestinal irritation, increased bleeding risk, and renal impairment",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Potassium supplements",
        "dosageForm": "Tablet",
        "indications": "Treatment and prevention of hypokalemia",
        "contraindications": "Hyperkalemia or severe renal impairment",
        "sideEffects": "Gastrointestinal ulceration, hyperkalemia, and arrhythmias",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Grapefruit juice",
        "dosageForm": "Liquid",
        "indications": "Not a medication; consumed as a beverage",
        "contraindications": "Concurrent use with certain medications metabolized by CYP3A4",
        "sideEffects": "Increased bioavailability of certain drugs, leading to potential toxicity",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Calcium supplements",
        "dosageForm": "Tablet",
        "indications": "Prevention and treatment of calcium deficiency and osteoporosis",
        "contraindications": "Hypercalcemia or severe kidney impairment",
        "sideEffects": "Constipation, kidney stones, and potential cardiovascular risks",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Dairy products",
        "dosageForm": "Tablet",
        "indications": "Not a medication; consumed as food",
        "contraindications": "Lactose intolerance or milk protein allergy",
        "sideEffects": "Gastrointestinal discomfort in lactose-intolerant individuals",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Alcohol",
        "dosageForm": "Liquid",
        "indications": "Not a medication; consumed as a beverage",
        "contraindications": "Liver disease, certain medications, and pregnancy",
        "sideEffects": "Impaired judgment, liver damage, and increased risk of accidents",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Paroxetine",
        "dosageForm": "Tablet",
        "indications": "Treatment of major depressive disorder, anxiety disorders, and PTSD",
        "contraindications": "Use of MAO inhibitors within 14 days or thioridazine",
        "sideEffects": "Weight gain, and discontinuation syndrome",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Verapamil",
        "dosageForm": "Tablet",
        "indications": "Treatment of hypertension, angina, and certain types of arrhythmias",
        "contraindications": "Severe left ventricular dysfunction or hypotension",
        "sideEffects": "Constipation, dizziness, and heart block",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Vitamin K",
        "dosageForm": "Tablet",
        "indications": "Treatment of vitamin K deficiency and reversal of excessive anticoagulation",
        "contraindications": "Known hypersensitivity to vitamin K1 or its components",
        "sideEffects": "Allergic reactions and interference with anticoagulant therapy",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Clopidogrel",
        "dosageForm": "Tablet",
        "indications": "Prevention of atherothrombotic events in patients with acute coronary syndrome or stroke",
        "contraindications": "Active pathological bleeding or severe liver impairment",
        "sideEffects": "Bleeding, thrombotic thrombocytopenic purpura, and neutropenia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Tacrolimus",
        "dosageForm": "Tablet",
        "indications": "Prevention of organ transplant rejection and treatment of atopic dermatitis",
        "contraindications": "Hypersensitivity to tacrolimus or other macrolides",
        "sideEffects": "Nephrotoxicity, neurotoxicity, and increased risk of infections",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Probenecid",
        "dosageForm": "Tablet",
        "indications": "Treatment of chronic gout and hyperuricemia",
        "contraindications": "Known blood dyscrasias or uric acid kidney stones",
        "sideEffects": "Gastrointestinal disturbances, headache, and urinary frequency",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Lithium",
        "dosageForm": "Tablet",
        "indications": "Treatment of bipolar disorder and prevention of manic episodes",
        "contraindications": "Severe renal impairment or significant cardiovascular disease",
        "sideEffects": "Tremor, polyuria, and thyroid abnormalities",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Valproic acid",
        "dosageForm": "Tablet",
        "indications": "Treatment of epilepsy, bipolar disorder, and migraine prophylaxis",
        "contraindications": "Hepatic disease or significant hepatic dysfunction",
        "sideEffects": "Weight gain, hair loss, and hepatotoxicity",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Spironolactone",
        "dosageForm": "Tablet",
        "indications": "Treatment of heart failure, hypertension, and hyperaldosteronism",
        "contraindications": "Anuria, acute renal insufficiency, or significant hyperkalemia",
        "sideEffects": "Gynecomastia, menstrual irregularities, and hyperkalemia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Cimetidine",
        "dosageForm": "Tablet",
        "indications": "Treatment of gastroesophageal reflux disease and peptic ulcers",
        "contraindications": "Known hypersensitivity to cimetidine or other H2 blockers",
        "sideEffects": "Headache, dizziness, and gynecomastia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    },
    {
        "name": "Amiodarone",
        "dosageForm": "Tablet",
        "indications": "Treatment of ventricular arrhythmias and atrial fibrillation",
        "contraindications": "Anuria, acute renal insufficiency, or significant hyperkalemia",
        "sideEffects": "Gynecomastia, menstrual irregularities, and hyperkalemia",
        "createdBy": "admin",
        "updatedBy": "admin",
        "createdAt": "2024-10-08T10:05:00Z",
        "updatedAt": "2024-10-08T10:05:00Z"
    }
]
EOL

# Process each entry
cat drugs.json | jq -c '.[]' | while read -r drug; do
    drugName=$(echo $drug | jq -r '.drugName')
    echo "Sending request for drug: $drugName"

    curl -X POST \
        -H "Content-Type: application/json" \
        -d "$drug" \
        http://localhost:8080/api/v1/drug/add

    echo -e "\n"

    # Add a small delay between requests
    sleep 0.5
done
