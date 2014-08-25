#!/bin/sh

# This script will install the record_classification software using 'mvn clean compile' then perform a training/classification run
# using the PIlot main class. This assumes modern training data and classification file in the pilot study format.

echo "Training file $1"
echo "Classification file $2" 

mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx110g -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PIlot $1 $2

echo finishTime: 
date +"%H:%M:%S"