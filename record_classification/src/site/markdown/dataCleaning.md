# Data Cleaning

## The Basics

Data cleaning is the task of removing unwanted characters and words (features) from the input data prior to
training and classification. Data cleaning plays an important role in the machine learning pipeline as noisy or dirty data
can have an adverse effect on the quality of the classifications produced.    

This software comes with several relatively simple tools to clean the the data before performing training and classification.    

These are details below.

### Data Cleaning Modules

The data cleaning classes are contained within the [datacleaning package](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/record_classification/)

These classes are designed to be run in a separate step to the machine learning, but could be chained together inside a script.

### Levenshtein Cleaner

The [Levenshtein Cleaner](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/record_classification/) is a spelling correcter that is built around
the [Levenshtein Distance](http://en.wikipedia.org/wiki/Levenshtein_distance) metric. The basic assumption of this data cleaner is that frequent words are spelt correctly and infrequent words are probably misspellings.
    
The data cleaner produces a count for each unique word and words that are below a given frequency threshold are corrected to the most similar word that is above a given similarity threshold. 
         
The output is then saved to file in the same format as the original file but with the new corrected data in place of the original data.     

### Custom Word Cleaner

The [Custom Word Cleaner](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/index.html?uk/ac/standrews/cs/digitising_scotland/record_classification/) class is designed to remove words that appear in a specified word list from
the records. Any words in the original record are removed from the original record. This is useful for removing high frequency, low value words. 


