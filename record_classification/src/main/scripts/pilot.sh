#!/bin/sh

# This script will install the record_classification software using 'mvn clean compile' then perform a training/classification run
# using the PIlot main class. This assumes modern training data and classification file in the pilot study format.

# Setup variables
HEAP="8g"

# Check that are at least 2 arguments
[ $# -le 1 ] && { 
	echo "Invalid number of arguments. Expected 2 or 3, got $#"; 
	echo "Usage: $0 <trainingFile>	<classificationFile>	<heap size - optional>"; 
	exit 1; 
}

# Check that are no more than 3 arguments
[ $# -ge 4 ] && { 
	echo "Invalid number of arguments. Expected 2 or 3, got $#"; 
	echo "Usage: $0 <trainingFile>	<classificationFile>	<heap size - optional>"; 
	exit 1;  
}

# Confirm user inputs
echo "Training file $1"
echo "Classification file $2" 

if [ -n "$3" ];
then
	echo "Heap size $3g" 
	HEAP="$3g"
fi

# Build and run model
mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx$HEAP -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PIlot $1 $2

echo finishTime: 
date +"%H:%M:%S"