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