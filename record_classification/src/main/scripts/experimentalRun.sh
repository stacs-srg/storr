#!/bin/sh

# This script will install the record_classification software using 'mvn clean compile' then perform a training/classification run
# using the TrainClassifyOneFile main class. This assumes modern training data that is used for both training and classifying.

# Setup variables
HEAP="8g"
USAGE="Usage: $0 <goldStandard File>	<trainingRatio>	<heap size - optional>"

# Check that there is at least 1 argument

[[ $# -le 0 ]] && { 
	>&2 echo "Invalid number of arguments. Expected between 1 and 3, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1; 
}

# Check that are no more than 3 arguments
[[ $# -ge 4 ]] && { 
	>&2	echo "Invalid number of arguments. Expected between 1 and 3, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1;  
}

# Confirm user inputs
echo "Gold Standard file $1"

if [[ -n "$2" ]];
then
	echo "Training ratio size $2"
fi

if [[ -n "$3" ]];
then
	echo "Heap size $3g"  
	HEAP="$3g" 
fi



# Build and run model
mvn clean compile assembly:single

echo startTime:
date +"%H:%M:%S"

java -d64 -Xmx$HEAP -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.TrainClassifyOneFile $1 $2

echo finishTime: 
date +"%H:%M:%S"