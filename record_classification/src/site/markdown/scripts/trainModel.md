# Train Model

Creates a model from known training data and writes it to a directory.

**SYNOPSIS**

<div class="source">
    <pre>trainModel.sh [training data] [location to save model] [<span style="text-decoration: underline">heap size in GB</span>]</pre>
</div>

**DESCRIPTION**

The **pilot.sh** script takes the supplied training data file and uses it to build the model. This model is then written to the supplied location.

The options are as follows:

* <span style="text-decoration: underline">training data</span>
    * This option specifies the file that contains the training data.
* <span style="text-decoration: underline">location to save the model</span>
    * This option specifies the location to write the model to.
* <span style="text-decoration: underline">heap size</span>
    * This option specifies the heap size in GB.

**EXAMPLE**

<div class="source">
    <pre>trainModel.sh goldStandardData.txt modelLocation 25</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/scripts/trainModel.sh)
* the Java class invoked is [TrainClassifyOneFile](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/uk/ac/standrews/cs/digitising_scotland/record_classification/pipeline/trainModel.html)

