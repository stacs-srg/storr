#!/bin/sh

#java -cp bin uk.ac.standrews.cs.digitising_scotland.helpertools.DictionaryBuilder
#java -Xmx8120m -cp bin uk.ac.standrews.cs.digitising_scotland.parser.mahout.VotingHelper config.txt
java -Xmx8120m -jar target/digital_scotland-1.0-SNAPSHOT.jar parserConfig.txt
