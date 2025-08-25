#!/bin/bash

# Define the log directory
log_dir="log"

# Function to handle Ctrl+C
cleanup() {
    echo "Ctrl+C detected. Exiting script."
    exit 1
}

# Trap the SIGINT signal (Ctrl+C) and call the cleanup function
trap cleanup SIGINT

# Check if the log directory exists, if not, create it
if [ ! -d "$log_dir" ]; then
  mkdir "$log_dir"
fi

# Loop 100 times
for i in {1..100}; do
  # Get the current date and time in the format YYYYMMDD_HHMMSS
  timestamp=$(date +"%Y%m%d_%H%M%S")

  # Define the log file name
  log_file="${log_dir}/${timestamp}.log"

  # Execute the script and redirect the output to the log file
  sh tmp.sh > "$log_file" 2>&1
done

echo "Script execution complete. Logs are saved in the '$log_dir' directory."
