#!/bin/sh

if [ -n "$1" ];
then
    echo Running classification and testing on $1
fi

mvn clean compile assembly:single

echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx110g -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.TrainAndMultiplyClassify $1

echo finishTime: 
date +"%H:%M:%S"