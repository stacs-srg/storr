# Pilot Data Classifier

Creates a model from known training data and uses the model to classify a file containing NRS style pilot data.

**SYNOPSIS**

<div class="source">
    <pre>pilot.sh [training data] [data to be classified] [<span style="text-decoration: underline">heap size in GB</span>]</pre>
</div>

**DESCRIPTION**

The **pilot.sh** script takes the supplied training data file and uses it to build the model. This model is then used to classify the data to be classified.

The options are as follows:

* <span style="text-decoration: underline">training data</span>
    * This option specifies the file that contains the training data.
* <span style="text-decoration: underline">training data</span>
    * This option specifies the file that contains the data to be classified.
* <span style="text-decoration: underline">heap size</span>
    * This option specifies the heap size in GB.

**EXAMPLE**

<div class="source">
    <pre>pilot.sh goldStandardData.txt dataToBeClassified.txt 25</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/scripts/pilot.sh)
* the Java class invoked is [TrainClassifyOneFile](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/uk/ac/standrews/cs/digitising_scotland/record_classification/pipeline/PIlot.html)

