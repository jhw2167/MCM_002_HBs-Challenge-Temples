import csv
import hashlib
import json
import os
import shutil
import sys
from pathlib import Path

INPUT_CSV = "challenges.csv"
TEMPLATE_JSON = "addChallenge/CHALLENGE_TEMPLATE.json"
NBT_INPUT_DIR = Path("addChallenge")
NBT_OUTPUT_DIR = Path("nbt")
JSON_OUTPUT_DIR = Path("json")
RECORDS_DIR = Path("records")


# Keep track of used IDs
used_ids = set()


def hash_to_n_digits(s, digits, key=None):
    """Hash a string to an N-digit number as string. Increment if needed for uniqueness."""
    base_hash = int(hashlib.sha256(s.encode()).hexdigest(), 16)
    number = base_hash % (10 ** digits)

    if key is None:
        return f"{number:0{digits}d}"

    tries = 0
    while True:
        candidate = f"{number % (10 ** digits):0{digits}d}"
        full = key + candidate
        if full not in used_ids:
            used_ids.add(full)
            return candidate
        number += 1
        tries += 1
        if tries > 999:
            raise Exception(f"Could not find unique hash for key={key}")


def generate_challenge_id(author, challenge_name):
    """Generate stable 9-digit ID: 6 for author, 3 for challenge."""
    if author == "Holy_Buckets":
        author_part = "000000"
    else:
        author_part = hash_to_n_digits(author, 6)

    suffix_part = hash_to_n_digits(author + "|" + challenge_name, 3, key=author_part)
    return author_part + suffix_part


def main():
    forced_id = None
    if len(sys.argv) > 1:
        forced_id = sys.argv[1].strip()
        if len(forced_id) < 9:
            raise Exception(f"Provided ID must be a 9-digit string, got: {forced_id}")
            
    # Load existing IDs
    if Path(INPUT_CSV).exists():
        with open(INPUT_CSV, newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            for row in reader:
                used_ids.add(row["challengeId"])
    else:
        print(f"Warning: {INPUT_CSV} does not exist, creating new.")

    # Load template JSON
    with open(TEMPLATE_JSON, encoding='utf-8') as f:
        challenge = json.load(f)

    author = challenge["author"].strip()
    challenge_name = challenge["challengeName"].strip()

    new_id = generate_challenge_id(author, challenge_name)
    if forced_id:
        new_id = forced_id
        if new_id in used_ids:
            raise Exception(f"ID {new_id} already exists in database.")
        used_ids.add(new_id)
    
    # Check for collisions in files/folders
    if (JSON_OUTPUT_DIR / f"{new_id}.json").exists():
        raise Exception(f"JSON output {new_id}.json already exists.")

    if (RECORDS_DIR / new_id).exists():
        raise Exception(f"Record folder {RECORDS_DIR/new_id} already exists.")

    for file in NBT_INPUT_DIR.glob("*.nbt"):
        target_name = file.name.replace("room_4x4", f"challenge_{new_id}")
        if (NBT_OUTPUT_DIR / target_name).exists():
            raise Exception(f"NBT output file {target_name} already exists.")

    # Update JSON
    challenge["challengeId"] = new_id

    # Save JSON to json/
    JSON_OUTPUT_DIR.mkdir(exist_ok=True)
    with open(JSON_OUTPUT_DIR / f"{new_id}.json", "w", encoding="utf-8") as f:
        json.dump(challenge, f, indent=4)

    # Copy & rename NBT files to nbt/
    NBT_OUTPUT_DIR.mkdir(exist_ok=True)
    room_sizes = ["room_2x2", "room_4x4", "room_8x8"]

    for file in NBT_INPUT_DIR.glob("*.nbt"):
        matched = False
        for pattern in room_sizes:
            if pattern in file.name:
                # Remove leading challenge_ if present
                new_name = file.name
                if new_name.startswith("challenge_"):
                    new_name = new_name[len("challenge_"):]
                new_name = new_name.replace(pattern, f"challenge_{new_id}")
                matched = True
                break
        if not matched:
            raise Exception(f"Could not match known room pattern in filename: {file.name}")

        target_file = NBT_OUTPUT_DIR / new_name
        if target_file.exists():
            raise Exception(f"NBT output file {new_name} already exists.")

        shutil.copy(file, target_file)


    # Append row to CSV
    rows = []
    if Path(INPUT_CSV).exists():
        with open(INPUT_CSV, newline='', encoding='utf-8') as csvfile:
            reader = csv.DictReader(csvfile)
            fieldnames = reader.fieldnames or []
            rows.extend(reader)
    else:
        fieldnames = ["challengeId", "author", "challengeName", "doUse"]

    # Add the new row
    rows.append({
        "challengeId": new_id,
        "author": author,
        "challengeName": challenge_name,
        "doUse": "1"
    })

    with open(INPUT_CSV, "w", newline='', encoding='utf-8') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

    # Move addChallenge/ to records/<ID>
    RECORDS_DIR.mkdir(exist_ok=True)
    shutil.move(str(NBT_INPUT_DIR), str(RECORDS_DIR / new_id))

    print(f"New challenge ID: {new_id}")
    print(f"All done, files written and folders updated.")


if __name__ == "__main__":
    main()
