#!/bin/sh

# This script will install the record_classification software using 'mvn clean compile' then perform a training/classification run
# using the PIlot main class. This assumes modern training data and classification file in the pilot study format.

# Setup script and environmental variables 
set -o pipefail
set -o errexit
HEAP="8g"
USAGE="Usage: $0 <trainingFile>	<classificationFile>	<propertiesFile>	<heap size - optional>"

# Check that are at least 2 arguments, echos to stderr
[[ $# -le 1 ]] && { 
	>&2 echo "Invalid number of arguments. Expected 2 or 3, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1; 
}

# Check that are no more than 4 arguments, echoes to stderr
[[ $# -ge 5 ]] && { 
	>&2 echo "Invalid number of arguments. Expected 3 or 4, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1;  
}

# Confirm user inputs
echo "Training file $1"
echo "Classification file $2"
echo "Properties file $3" 
 

if [ -n "$4" ] &&  [ "$4" -ne 0 -o "$4" -eq 0 2>/dev/null ];
then
	echo "Heap size $4g" 
	HEAP="$4g"
else
	>&2 echo "Heap size must be an integer" ; 
	>&2 echo "$USAGE"; 
	exit 1; 
fi

# Build and run model
#mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx$HEAP -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main.PIlot $1 $2 $3

echo finishTime: 
date +"%H:%M:%S"