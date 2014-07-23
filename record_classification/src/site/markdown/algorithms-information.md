## Algorithms Information

The record classification software uses machine learning models as well as lookup tables to perform coding to single and multiple codes. 
These algorithms are detailed here.

### Online Logistic Regression

This is our primary machine learning algorithm. It is a heavily modified version of Mahout's Logistic Regression implementation ([see here](https://mahout.apache.org/users/classification/logistic-regression.html)).
The model training algorithm architecture is hierarchical.

#### The OLR Object
At the lowest level we have the OLR (online logistic regression) object which holds the model parameters 
and training methods for an individual logistic regression model. The model is 'online' in the sense that it is trained on a record by record basis. We pass in a record and this makes
the model parameters change slightly in the direction suggested by that particular training example. We iterate over all of the records (perhaps multiple times) repeating this training
process for each record.

#### The OLRShuffled Object
This is the next level up in the model training algorithm architecture. This object contains and OLR object and a set of records. The OLRShuffled object implements the Runnable
interface - the run method calls a routine which shuffles the training files and trains the OLR object on them, this process of shuffling the records and training may be repeated for
a chosen number of repetitions.

#### The OLRPool Object
This is the next layer in the model training algorithm architecture. This object contains a pool of OLRShuffled objects and also implements the Runnable interface. The run method trains
the OLRShuffled Objects on their data then starts a process of model evaluation based on held back data which is owned by the OLRPool object. All of the trained OLRShuffled objects are tested
on this hold-out set. The models are ranked based upon their performance and the poorest performing models discarded. This is to help deal with the fact that the gradient descent optimisation
routine is only successful a certain percentage of the time - we simply discard the unsuccessful models.

#### The OLRCrossFold Object
The final layer of the model training algorithm architecture. This object contains a pool of OLRPool objects. We take the training data and partition it into 'folds' each OLRPool is trained
and tested on a different fold of the data. e.g. if we choose to have 10 folds then we will have 10 OLRPools each being trained on a different 9 of the 10 folds (the fold that each model does not
see in training is used for its internal evaluation routine). Once all of the models have been trained we average the model parameters of each model (just simple matrix addition) so that
we end up with a final model for classification.


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