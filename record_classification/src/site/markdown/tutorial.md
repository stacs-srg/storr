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

### Training

### Classification

### Output

