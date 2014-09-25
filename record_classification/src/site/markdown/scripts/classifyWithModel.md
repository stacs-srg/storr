# Classify with Model

Classifies a dataset with an existing model.

**SYNOPSIS**

<div class="source">
    <pre>classifyWithModel.sh [path to model] [data to be classified] [<span style="text-decoration: underline">heap size in GB</span>]</pre>
</div>

**DESCRIPTION**

The **experimentalRun.sh** script takes the supplied training data file and splits the dataset into two batches. The first batch is used for training the model and second is used to validate the model. The ratio of training to validation data is set by the [training data ratio] option.

The options are as follows:

* <span style="text-decoration: underline">path to model</span>
    * This option specifies the path to the previously trained model.
* <span style="text-decoration: underline">data to be classified</span>
    * This option specifies file containing the data to be classified.
* <span style="text-decoration: underline">heap size</span>
    * This option specifies the heap size in GB.

**EXAMPLE**

<div class="source">
    <pre>classifyWithModel.sh models/trainedModel dataToBeClassified.txt 25</pre>
</div>

**ADDITIONAL INFO**

* [source code](http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/scripts/classifyWithModel.sh)
* the Java class invoked is [TrainClassifyOneFile](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/uk/ac/standrews/cs/digitising_scotland/record_classification/pipeline/classifyWithModel.html)

