# Initialise Database

Initialises a local MySQL/MariaDB database to store a population.

**SYNOPSIS**

     init_db.sh [heap size in GB]

**DESCRIPTION**

The **init_db.sh** utility connects to a local MySQL or MariaDB database server, removes any population tables already present, and creates the tables needed to store a population.

The options are as follows:

* [optional] heap size in GB

**EXAMPLE**

<div class="source">
    <pre>init_db.sh 2</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/population_model/src/main/resources/scripts/init_db.sh)
* the Java class invoked is [InitialiseDB](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/population_model/tools/InitialiseDB.html)
* the database and table names are set in class [PopulationProperties](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/population_model/config/PopulationProperties.html)
