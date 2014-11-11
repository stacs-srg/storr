## Frequently Asked Questions

#### My data is in a different format to the ones you mention on the website, what should I do?

If your data is in a different format to the ones we have provided readers for, you have two options.

Firstly, you can alter your data so that it is in the same format as one of the standard formats that the classification
software can read out of the box. If you do not have data that these formats require the missing data can be left blank or dummy
data added.

The second option is to write a custom data reader class for your file format. This requires downloading and altering the source code. 
For details on how to obtain the source code please see the [tutorial](tutorial.html). 

In order to write a custom data reader you should extend the [AbstractFormatConverter](https://builds.cs.st-andrews.ac.uk/job/digitising_scotland/javadoc/uk/ac/standrews/cs/digitising_scotland/record_classification/datareaders/AbstractFormatConverter.html)
class and implement your custom convert() method. This method should read a file and return a list of populated Record objects.
