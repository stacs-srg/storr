package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions.InputFormatException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;



/**
 *
 * Created by fraserdunlop on 23/10/2014 at 12:28.
 */


public class InteractiveAnalyser {

    public static void main(String args[]) throws IOException, InputFormatException, CodeNotValidException {
        CodeDictionary codeDictionary = new CodeDictionary(new File(args[1]));
        BucketGenerator bucketGen = new BucketGenerator(codeDictionary);
        Bucket bucket = bucketGen.generateTrainingBucket(new File(args[0]));
        FeatureSpaceAnalyser featureSpaceAnalyser = new FeatureSpaceAnalyser(bucket);
        FeatureSpaceAnalyserFormatter formatter = new FeatureSpaceAnalyserFormatter(featureSpaceAnalyser);
        new InteractiveAnalyser(codeDictionary, formatter);
    }

    private final Logger LOGGER = LoggerFactory.getLogger(FeatureSpaceAnalyser.class);
    private FeatureSpaceAnalyserFormatter formatter;

    private static final String STOP_COMMAND = "stop";
    private static final String STOP_MESSAGE = "\nStop call detected. Stopping analyser...";

    private CodeDictionary codeDictionary;


    /**
     * Initiates the command line stopListener. This waits for input from the command line and processes the input.
     */
    public InteractiveAnalyser(final CodeDictionary codeDictionary, final FeatureSpaceAnalyserFormatter featureSpaceAnalyser) throws CodeNotValidException {
        this.codeDictionary = codeDictionary;
        this.formatter = featureSpaceAnalyser;
        LOGGER.info(instructions());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, FileManipulation.FILE_CHARSET))) {
            String line;
            while (true) {
                line = in.readLine();
                if (line != null && processInput(line)) {
                    break;
                }
            }
            in.close();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes the input from the command line. Calls the correct Logger reaction on input and
     * returns true if a message is printed.
     *
     * @param line the line to process
     * @return true, if successful output is printed.
     * @throws InterruptedException the interrupted exception
     */
    private boolean processInput(final String line) throws InterruptedException, CodeNotValidException {

        if(isCode(line)){
            LOGGER.info(formatter.formatReport(codeDictionary.getCode(line)));
        }else {
            switch (line.toLowerCase()) {
                case STOP_COMMAND:
                    LOGGER.info(stopCalled());
                    break;
                default:
                    LOGGER.info(instructions());
            }
        }
        return line.equalsIgnoreCase(STOP_COMMAND);
    }

    private boolean isCode(String line) {
        return codeDictionary.isValid(line);
    }


    /**
     * Calls the Stop method and returns the stop message.
     *
     * @return the string
     */
    private String stopCalled() {

        return STOP_MESSAGE;
    }


    /**
     * Returns the instructions.
     *
     * @return the string
     */
    private String instructions() {
        int depth = 90;
        return   "\n" +
                 formatter.repeatConcatString("#", depth) +
                 "\n### ***FEATURE SPACE ANALYSER INSTRUCTIONS***\n" +
                 "###\n" +
                 "### Prints feature profile information given a code which appears in the training bucket.\n" +
                 "### The table contains the features which predict the code,\n" +
                 "### the number of training examples containing the feature associated with the code,\n" +
                 "### and the number of training examples containing the feature in total.\n" +
                 "###\n" +
                 "### Type \"stop\" to quit program.\n" +
                 "### Type a code for a feature analysis of code (case sensitive).\n" +
                 formatter.repeatConcatString("#", depth);
    }
}