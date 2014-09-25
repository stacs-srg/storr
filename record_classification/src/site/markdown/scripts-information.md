# Scripts

Various bash scripts are located in the directory:

<div class="source">
    <pre><a href="http://quicksilver.hg.cs.st-andrews.ac.uk/digitising_scotland/file/tip/record_classification/src/main/scripts">record_classification/src/main/scripts</a></pre>
</div>

----

*[trainModel.sh](scripts/trainModel.html) Trains a model and writes it to disk for later use.
*[pilot.sh](scripts/pilot.html) Trains a model and classifies a file containing data in the [NRS pilot data format](dataFormats/pilotData.html)
*[experimentalRun.sh](scripts/experimentalRun.html) Trains a model and validates the performance of that model using a single data file.
*[classifyWithModel.sh](scripts/classifyWithModel.html) Classifies a dataset using an existing model.
*[levenshteinCleaner.sh](scripts/levenshteinCleaner.html) Performs spelling correction on a dataset using a levenshtein distance as measure of how good a potential correction is.
