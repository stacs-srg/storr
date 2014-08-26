# Scripts Information

The software comes with a selection of shell scripts to speed up the process of using the software. Generally these scripts are designed to do some basic input validation, build the software then run the main class in question.

## Available Scripts

### Experimental Run
This script expects at least 1 argument, and has two optional arguments. 
The 1st argument is mandatory and is the file containing the gold standard data to use for training and validation.
The 2nd argument is optional, this is a number between 0 and 1.0 which is the ratio of training data to validation data, eg 0.9 means 90% training, 10% validation. The default is 0.8.
The 3rd argument is the size of the heap to give the jvm in gigabytes, this should be an integer between 1 and the maximum amount of RAM on your machine. The default is 8.

Example useage:

<div class="source">
	$sh experimentalRun.sh goldStandardData.tsv 0.9 25
</div>

Run an experimental run with the file 'goldStandardData.tsv', splitting into a training set of 90% of the records and a validation set of 10% and use a java heap space of 25Gb.

### Multiple Classifications

This script expects at least 2 arguments, with a 3rd optional.
The 1st argument is mandatory and is the file containing the gold standard data to use for training.
The 2nd argument is mandatory, this is the file that we want to classify.
The 3rd argument is the size of the heap to give the jvm in gigabytes, this should be an integer between 1 and the maximum amount of RAM on your machine. The default is 8.

<div class="source">
	$sh runMultipleClassifiations.sh goldStandardData.tsv dataToBeClassified.txt 25
</div>

Run an classification run with the file 'goldStandardData.tsv', using that to classify the records in 'dataToBeClassified.txt' using a java heap space of 25Gb.


### Pilot Data Classifications

This script expects at least 2 arguments, with a 3rd optional.
The 1st argument is mandatory and is the file containing the records that we want to clean.
The 2nd argument is mandatory, this is the location to write out cleaned records to
The 3rd argument is optional and is the size of the heap to give the jvm in gigabytes, this should be an integer between 1 and the maximum amount of RAM on your machine. The default is 8.

<div class="source">
	$sh pilot.sh goldStandardData.tsv pilotData.tsv 50
</div>

Run a classification run with the file 'goldStandardData.tsv', using that to classify the records in 'pilotData.tsv' using a java heap space of 50Gb.

### Levenshtein Cleaner

This script expects at least 2 arguments, with a 3rd and 4th arguments optional.
The 1st argument is mandatory and is the file containing the records that we want to clean.
The 2nd argument is mandatory, this is the location to write out cleaned records to
The 3rd argument is optional and is the token limit to be used for the levenshtein cleaner.
the 4th argument is optional and is the similarity limit to be used for the levenshtein cleaner.

<div class="source">
	$sh levenshteinCleaner.sh goldStandardData.tsv cleanedData.txt 4 0.8
</div>

Run a data cleaning run with the levenshtein cleaner on file 'goldStandardData.tsv', writing the cleaned records to cleanedData.txt and using a token limit of 4 and a similaraity limit of 0.8
