#!/bin/sh
#
# Copyright 2014 Digitising Scotland project:
# <http://digitisingscotland.cs.st-andrews.ac.uk/>
#
# This file is part of the module record_classification.
#
# record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
# License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with record_classification. If not, see
# <http://www.gnu.org/licenses/>.
#


# This script will install the record_classification software using 'mvn clean compile' then perform a training/classification run
# using the TrainClassifyOneFile main class. This assumes modern training data that is used for both training and classifying.

# Setup environmental and local variables
set -o pipefail
set -o errexit
HEAP="8g"
USAGE="Usage: $0 <goldStandard File>	<propertiesFile>	<trainingRatio>		<multipleClassifications>	<heap size - optional>"

# Check that there is at least 1 argument
[[ $# -le 0 ]] && { 
	>&2 echo "Invalid number of arguments. Expected between 1 and 3, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1; 
}

# Check that are no more than 3 arguments
[[ $# -ge 6 ]] && { 
	>&2	echo "Invalid number of arguments. Expected between 1 and 6, got $#"; 
	>&2 echo "$USAGE"; 
	exit 1;  
}

# Confirm user inputs
echo "Gold Standard file $1"

if [[ -n "$2" ]];
then
	echo "Training ratio size $2"
fi

if [ -n "$5" ] &&  [ "$5" -ne 0 -o "$5" -eq 0 2>/dev/null ];
then
	echo "Heap size $5g"  
	HEAP="$5g" 
else 
	>&2 echo "Heap size must be an integer" ; 
	>&2 echo "$USAGE"; 
	exit 1; 
fi



# Build and run model
# mvn clean compile assembly:single

echo startTime:
date +"%H:%M:%S"

java -d64 -Xmx$HEAP -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main.TrainClassifyOneFile $1 $2 $3 $3 $5

echo finishTime: 
date +"%H:%M:%S"