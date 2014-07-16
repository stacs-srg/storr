#!/bin/sh

# Copyright 2014 Digitising Scotland project:
# <http://digitisingscotland.cs.st-andrews.ac.uk/>
#
# This file is part of the module population_model.
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

# Documentation: http://digitisingscotland.cs.st-andrews.ac.uk/population_model/scripts/generate_population.html

if [ -n "$1" ];
then
    export MAVEN_OPTS="-Xmx"$1"G"
    echo Setting heap size: $1GB
fi

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.population_model.tools.GenerateCompactPopulationInDB" -e -Dexec.args="$2 $3 $4"
