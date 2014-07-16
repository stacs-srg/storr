====
    Copyright 2014 Digitising Scotland project:
    <http://digitisingscotland.cs.st-andrews.ac.uk/>

    This file is part of the module population_model.

    population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
    version.

    population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
    warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with population_model. If not, see
    <http://www.gnu.org/licenses/>.
====

A Java program can be run from the command line using Maven to configure the class path. For example:

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.population_model.tools.InitialiseDB"

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.population_model.tools.GeneratePopulation" -Dexec.args="-b500 -u7"

See also the shell scripts in src/main/resources/scripts.
