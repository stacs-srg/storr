## Algorithms Information

The record classification software uses machine learning models as well as lookup tables to perform coding to single and multiple codes. 
These algorithms are detailed here.

### Online Logistic Regression

This is our primary machine learning algorithm. It's is built on top of Mahout's Logistic Regression implementation ([see here](https://mahout.apache.org/users/classification/logistic-regression.html)).

### Classification to Multiple Codes

The ability to classify a record to more than one classification is extremely important when dealing with cause of death records as as single string may
contain several different causes of death. For example the death certificate may list "bronchitis and whooping cough". It would be innapropriate to code this
as just bronchitis (J40) or whooping cough (A37) when both causes contributed.

The following algorithm is used to code single strings to multiple codes:

The original cause of death string a record is extracted.

This string is then split into word level tokens. 

The set of n-gram tokens is then generated, where n ranges from 1 (unigrams) to the maximum number of tokens in the original string. 

Each of token sets from the n-gram set is then classified using the machine learning models and a classification and confidence assigned to each code.

From this result set of classified n-gram token sets, the set of 'valid' combinations of classifications are found. 
A set of classifications is said to be 'valid' if it is a subset of the original token set.

Each valid result set is then passed through a loss function to determine the 'best' classification set. 

The best result set is then assigned to the original string. 