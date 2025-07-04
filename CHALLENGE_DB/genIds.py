import csv
import hashlib

INPUT_FILE = "challenges.csv"
OUTPUT_FILE = "challenges_updated.csv"


# Global set of used IDs
used_ids = set()

# Optional counter map per prefix if you want to remember last suffix for an author or whole ID
id_counters = {}
def hash_to_n_digits(s, digits, key=None):
    """
    Hash a string to an N-digit integer as a zero-padded string.
    If `key` is provided, auto-increment until unique for that key.
    """
    base_hash = int(hashlib.sha256(s.encode()).hexdigest(), 16)
    number = base_hash % (10 ** digits)

    # If no collision check, just return
    if key is None:
        return f"{number:0{digits}d}"

    tries = 0
    while True:
        candidate = f"{number % (10 ** digits):0{digits}d}"
        if key + candidate not in used_ids:
            used_ids.add(key + candidate)
            return candidate
        number += 1
        tries += 1
        if tries > 999:
            raise ValueError(f"Too many hash collisions for key={key}")


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

    name_part = hash_to_n_digits(challenge_name, 3)

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

print(f"Updated CSV written to: {OUTPUT_FILE}")
