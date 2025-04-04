import csv
import hashlib
import os

INPUT_FILE = "challenges.csv"
OUTPUT_FILE = "challenges_updated.csv"

def hash_to_3_digits(s):
    """Hash a string to a 3-digit number using sha256."""
    return f"{int(hashlib.sha256(s.encode()).hexdigest(), 16) % 1000:03d}"

def generate_challenge_id(author, challenge_name):
    if author == "Holy_Buckets":
        author_hash = "000"
    else:
        author_hash = hash_to_3_digits(author)
    name_hash = hash_to_3_digits(challenge_name)
    return author_hash + name_hash

# Read CSV and update rows
rows = []
with open(INPUT_FILE, newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)
    fieldnames = reader.fieldnames or []
    
    # Ensure all columns are present
    if "author" not in fieldnames:
        fieldnames.append("author")
    if "doUse" not in fieldnames:
        fieldnames.append("doUse")
    if "challengeId" not in fieldnames:
        fieldnames.insert(0, "challengeId")  # ensure it's first
    
    for row in reader:
        # Default values
        row.setdefault("author", "UnknownAuthor")
        row.setdefault("doUse", "1")
        row.setdefault("challengeName", "").strip()

        # Generate challengeId if missing or empty
        if not row.get("challengeId") or row["challengeId"].strip() == "":
            row["challengeId"] = generate_challenge_id(row["author"], row["challengeName"])
        
        rows.append(row)

# Write updated CSV
with open(OUTPUT_FILE, 'w', newline='', encoding='utf-8') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)

print(f"Updated CSV written to {OUTPUT_FILE}")
