import sys
import shutil
import csv
from pathlib import Path

# Config
CSV_FILE = Path("challenges.csv")
JSON_DIR = Path("json")
RECORDS_DIR = Path("records")

def main():
    if len(sys.argv) != 2:
        print("Usage: python removeChallenge.py <9-digit ID>")
        sys.exit(1)

    challenge_id = sys.argv[1].strip()

    if not challenge_id.isdigit() or len(challenge_id) != 9:
        print("Error: Challenge ID must be a 9-digit number.")
        sys.exit(1)

    # Remove records/<ID> folder
    """
    record_path = RECORDS_DIR / challenge_id
    if record_path.exists():
        shutil.rmtree(record_path)
        print(f"Removed folder: {record_path}")
    else:
        print(f"Warning: Folder {record_path} does not exist.")
    """

    # Remove json/<ID>.json
    json_file = JSON_DIR / f"{challenge_id}.json"
    if json_file.exists():
        json_file.unlink()
        print(f"Removed JSON: {json_file}")
    else:
        print(f"Warning: JSON {json_file} does not exist.")

    # Remove from challenges.csv
    if not CSV_FILE.exists():
        print(f"Error: {CSV_FILE} does not exist.")
        sys.exit(1)

    rows = []
    removed = False

    with open(CSV_FILE, newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        fieldnames = reader.fieldnames or []
        for row in reader:
            if row.get("challengeId") == challenge_id:
                removed = True
                continue
            rows.append(row)

    if not removed:
        print(f"Warning: ID {challenge_id} not found in {CSV_FILE}.")
    else:
        with open(CSV_FILE, "w", newline='', encoding='utf-8') as f:
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(rows)
        print(f"Removed ID {challenge_id} from {CSV_FILE}.")

    print("Done.")

if __name__ == "__main__":
    main()
