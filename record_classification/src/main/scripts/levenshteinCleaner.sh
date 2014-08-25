#!/bin/sh
if [ -n "$1" ];
then
    echo Running data cleaning on $1
fi

if [ -n "$2" ];
then
	echo Cleaning file will be saved to $2
fi

if [ -n "$3" ];
then
	echo Token limit set to $3
fi
	
if [ -n "$4" ];
then
 	echo Similarity set to $4
fi

mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx6g -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.datacleaning.LevenshteinCleaner $1 $2 $3 $4

echo finishTime: 
date +"%H:%M:%S"