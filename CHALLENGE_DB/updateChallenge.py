import os
import shutil
import sys
from pathlib import Path

# Arguments
if len(sys.argv) != 2:
    print("Usage: python updateChallenge.py <challenge_id>")
    sys.exit(1)

challenge_id = sys.argv[1]
if len(challenge_id) != 9 or not challenge_id.isdigit():
    raise Exception("Provided ID must be a 9-digit numeric string.")

# Paths
SOURCE_DIR = Path("..") / "fabric" / "run" / "saves" / "2" / "generated" / "hbs_challenge_temple" / "structures"
NBT_OUTPUT_DIR = Path("nbt")
RECORDS_DIR = Path("records") / challenge_id

# Ensure destination directories exist
NBT_OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
RECORDS_DIR.mkdir(parents=True, exist_ok=True)

# Room types supported
room_types = ["room_2x2", "room_4x4", "room_8x8"]

# Process files
for file in SOURCE_DIR.glob("*.nbt"):
    for room_type in room_types:
        if room_type in file.name:
            suffix = file.name.split(room_type)[-1]  # e.g., "_00.nbt"
            new_name = f"challenge_{challenge_id}{suffix}"

            # Copy to /nbt
            shutil.copy(file, NBT_OUTPUT_DIR / new_name)

            # Copy to /records/<id>
            shutil.copy(file, RECORDS_DIR / new_name)
            break
    else:
        print(f"Skipping unrecognized file: {file.name}")

print(f"Updated challenge {challenge_id} with new NBTs.")
