# Tutorial

This page provides a brief tutorial on how to use the Record Classification software.

Please note that this page is still currently under construction and will be added to and changed as the software in finalised.

### Basics

The record classification software provides a mechanism for automatically coding historical records cause of death and occupation 
records to HICD10 and HISCO respectively.

In general this is a two step process, lookup tables and machine learning models must be built using known "gold standard"
data before new records can be classified. 

For convenience, these two steps can be run in one step by providing the software with both a training file and a file to be classified. 
If you prefer you can build the lookup tables and train the models separately and perform classification at at later date.

### File Formats

The software is designed to work with two file formats. The first is the pipe separated format specified by NRS, and the second is the full 
length comma separated format used for current mortality coding. 

Each file must contain only 1 record per line and the format must be consistent through out the file. In both formats missing information
is acceptable, but the file must contain an empty section where that data should be been, ie, two separators next to each other with no content.
     
     
### Data Cleaning

Data cleaning is an important first step before training or using any machine learning system. For more information on the data cleaning classes 
included with this software please see the [data cleaning](dataCleaning.html) page.    


### Training

Training consists of building lookup tables from the training data and training the machine learning models. 
For more information on the machine learning algorithms used, please see the [algorithms information](algorithms-information.html) page.

### Classification

The classification process involves firstly looking up a record in the prebuilt lookup table. If the record is not in the lookup table then
the machine learning models are used to classify the record.

The full machine learning algorithm for classification to multiple records can be found on the [algorithms information](algorithms-information.html) page.

### Output

Output is currently being sent to "target/NRSOutput.txt" and is pipe delimited, one record per line.
 
### Running the Classifier

To run the classifier in testing mode using a single file for training and classification execute the 'runMultipleClassifications.sh' script
in the scripts folder. This script takes as an argument the file to train and test on.     

For example:    

<div class="source">
	$sh runMultipleClassifications.sh recordsToClassify.txt    
</div>

This script will use Maven to do build the software, split the file into a training portion (80%) and a testing proportion (20%).    
The classifier is then trained and the testing files classified using the trained model. Output and analysis metrics are then written to the Experiments Folder.
 


