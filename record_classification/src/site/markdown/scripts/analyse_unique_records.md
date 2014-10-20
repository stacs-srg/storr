# Analyse Unique Records

Analyses the distribution of unique records within a given text file.

**SYNOPSIS**

<div class="source">
    <pre>analyse_unique_records.sh <span style="text-decoration: underline">heap size in GB</span> <span style="text-decoration: underline">input file</span> <span style="text-decoration: underline">output file</span> <span style="text-decoration: underline">number of samples</span> <span style="text-decoration: underline">number of repetitions</span> [<span style="text-decoration: underline">prefix length</span>] [<span style="text-decoration: underline">suffix length</span>]</pre>
</div>

**DESCRIPTION**

The **analyse_unique_records.sh** utility reads the strings on separate lines in the given input text file. Empty strings are ignored. At regular intervals it records the number of unique strings so far encountered. The process is repeated a given number of times, with the strings being read in a different random order each time. The results are written to the given output text file.

The arguments are as follows:

* <span style="text-decoration: underline">heap size</span>
    * This argument specifies the heap size in GB.
* <span style="text-decoration: underline">input file</span>
    * This argument specifies the path of the input file, which must exist.
* <span style="text-decoration: underline">output file</span>
    * This argument specifies the path of the output file, which must not exist.
* <span style="text-decoration: underline">number of samples</span>
    * This argument specifies the number of samples to be taken through the file.
* <span style="text-decoration: underline">number of repetitions</span>
    * This argument specifies the number of times that the process is to be repeated.
* <span style="text-decoration: underline">prefix length</span>
    * This optional argument specifies the number of characters that should be ignored at the start of each string.
* <span style="text-decoration: underline">suffix length</span>
    * This optional argument specifies the number of characters that should be ignored at the end of each string.

**EXAMPLE**

<div class="source">
    <pre>analyse_unique_records.sh 20 input.txt output.txt 50 10 4</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/resources/scripts/analyse_unique_records.sh)
* the Java class invoked is [AnalyseUniqueRecords](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/record_classification/tools/AnalyseUniqueRecords.html)

