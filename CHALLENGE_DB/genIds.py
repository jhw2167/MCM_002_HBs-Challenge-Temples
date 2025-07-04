import csv
import hashlib

INPUT_FILE = "challenges.csv"
OUTPUT_FILE = "challenges_updated.csv"


def hash_to_n_digits(s, digits):
    """Hash a string to an N-digit integer as a zero-padded string."""
    h = int(hashlib.sha256(s.encode()).hexdigest(), 16)
    return f"{h % (10 ** digits):0{digits}d}"


def generate_challenge_id(author, challenge_name):
    """
    Generates a 9-digit ID:
      - First 6 digits: stable hash of the author name.
      - Last 3 digits: stable hash of the challenge name + author.
    """
    if author == "Holy_Buckets":
        author_part = "000000"
    else:
        author_part = hash_to_n_digits(author, 6)

    name_part = hash_to_n_digits(author + "|" + challenge_name, 3)

    return author_part + name_part


# --- Read CSV ---
rows = []
with open(INPUT_FILE, newline='', encoding='utf-8') as csvfile:
    reader = csv.DictReader(csvfile)
    fieldnames = reader.fieldnames or []

    # Ensure required columns exist
    if "author" not in fieldnames:
        fieldnames.append("author")
    if "doUse" not in fieldnames:
        fieldnames.append("doUse")
    if "challengeId" not in fieldnames:
        fieldnames.insert(0, "challengeId")  # put ID first if missing

    for row in reader:
        # Defaults
        row.setdefault("author", "UnknownAuthor")
        row.setdefault("doUse", "1")
        row.setdefault("challengeName", row.get("challengeName", "")).strip()

        # If no ID, generate one
        if not row.get("challengeId") or row["challengeId"].strip() == "":
            row["challengeId"] = generate_challenge_id(row["author"], row["challengeName"])

        rows.append(row)

# --- Write updated CSV ---
with open(OUTPUT_FILE, 'w', newline='', encoding='utf-8') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(rows)

print(f"✅ Updated CSV written to: {OUTPUT_FILE}")
