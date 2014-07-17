# Generate Population

Creates a new synthetic population and stores it in a local MySQL/MariaDB database.

**SYNOPSIS**

<div class="source">
    <pre>generate_population.sh [heap size in GB] [<strong>-b</strong><span style="text-decoration: underline">batch size</span>] [<strong>-n</strong><span style="text-decoration: underline">number of batches</span>] [<strong>-u</strong><span style="text-decoration: underline">number of progress updates</span>]</pre>
</div>

**DESCRIPTION**

The **generate_population.sh** utility generates a new synthetic population, in a series of batches, and stores it in a local MySQL or MariaDB database. Each batch is a separate sub-population unconnected to the rest.

The options are as follows:

* <span style="text-decoration: underline">heap size</span>
    * This option specifies the heap size in GB. It must appear first.
* <strong>-b</strong><span style="text-decoration: underline">batch size</span>
    * This option specifies the population size for each batch.
* <strong>-n</strong><span style="text-decoration: underline">number of batches</span>
    * This option specifies the number of batches to be generated.
* <strong>-u</strong><span style="text-decoration: underline">number of progress updates</span>
    * This option specifies the number of progress updates to be issued during the generation process.

**EXAMPLE**

<div class="source">
    <pre>generate_population.sh 20 -b100000 -n10 -u100</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/population_model/src/main/resources/scripts/generate_population.sh)
* the Java class invoked is [GenerateCompactPopulationInDB](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/population_model/tools/GenerateCompactPopulationInDB.html)
* the database and table names are set in class [PopulationProperties](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/population_model/config/PopulationProperties.html)
