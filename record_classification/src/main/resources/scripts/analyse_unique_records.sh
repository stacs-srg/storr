#!/bin/sh

# Copyright 2014 Digitising Scotland project:
# <http://digitisingscotland.cs.st-andrews.ac.uk/>
#
# This file is part of the module record_classification.
#
# population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
# License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
# version.
#
# population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with population_model. If not, see
# <http://www.gnu.org/licenses/>.

# Documentation: http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/scripts/analyse_unique_records.html

if [ -n "$1" ];
then
    export MAVEN_OPTS="-Xmx"$1"G"
    echo Setting heap size: $1GB
fi

# Commented out line using maven as not sure if that is installed on LWH systems
#mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.record_classification.tools.AnalyseUniqueRecords" -e -Dexec.args="$2 $3 $4 $5 $6 $7"
java -d64 -Xmx$1G -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.record_classification.tools.AnalyseUniqueRecords $2 $3 $3 $5 $6 $7
