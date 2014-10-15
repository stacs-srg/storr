# Experimental Run

Uses a dataset with known output classes to train and validate a classification model. 

**SYNOPSIS**

<div class="source">
    <pre>experimentalRun.sh [training data] [<span style="text-decoration: underline">training data ratio</span>] [<span style="text-decoration: underline">heap size in GB</span>]</pre>
</div>

**DESCRIPTION**

The **experimentalRun.sh** script takes the supplied training data file and splits the dataset into two batches. The first batch is used for training the model and second is used to validate the model. The ratio of training to validation data is set by the [training data ratio] option.

The options are as follows:

* <span style="text-decoration: underline">training data</span>
    * This option specifies the file that contains the training data.
* <span style="text-decoration: underline">training data ratio</span>
    * This option specifies the size of the training data batch to be generated. Must be a value between 0 and 1. A ratio of 0.8 would mean 80% of data will be used for training, 20% for validation.
* <span style="text-decoration: underline">heap size</span>
    * This option specifies the heap size in GB.

**EXAMPLE**

<div class="source">
    <pre>experimentalRun.sh goldStandardData.txt 0.75 25</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/scripts/experimentalRun.sh)
* the Java class invoked is [TrainClassifyOneFile](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/uk/ac/standrews/cs/digitising_scotland/record_classification/pipeline/TrainClassifyOneFile.html)

