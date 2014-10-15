#!/bin/sh

# This script will read the arguments supplied to the script and pass them to the LevenshteinCleaner class in the record_classification module.

# Check user inputs and echo to user.

# Setup environmental and local variables
set -o pipefail
set -o errexit

USAGE="Usage: $0 <inputFile>	<outputFile>	<tokenLimit>	<similarityLimit>
	   tokenLimit and similarity must be integers"

if [ -n "$1" ] ;
then
    echo "Running data cleaning on $1"
else
	echo "$USAGE"
	exit 1;
fi

if [ -n "$2" ] ;
then
	echo Cleaning file will be saved to $2
else
	echo "$USAGE"
	exit 1;
fi


if [ -n "$3" ] &&  [ "$3" -ne 0 -o "$3" -eq 0 2>/dev/null ];
then
	echo Token limit set to $3
else
	echo "Supplied Input $3 is not an Integer."
	echo "$USAGE"
	exit 1;
fi
	
if [ -n "$4" ] &&  [ "$4" -ne 0 -o "$4" -eq 0 2>/dev/null ];
then
 	echo Similarity set to $4
else
	echo "Supplied Input $4 is not an Integer."
	echo "$USAGE"
	exit 1;
fi

# install the software with a clean target directory.
mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

# run the software with a 64bit JVM and a heap size of 6 gigabytes.
java -d64 -Xmx6g -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenshteinCleaner $1 $2 $3 $4

echo finishTime: 
date +"%H:%M:%S"