A Java program can be run from the command line using Maven to configure the class path. For example:

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.population_model.tools.InitialiseDB"

mvn exec:java -q -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="uk.ac.standrews.cs.digitising_scotland.population_model.tools.GeneratePopulation" -Dexec.args="-b500 -u7"
