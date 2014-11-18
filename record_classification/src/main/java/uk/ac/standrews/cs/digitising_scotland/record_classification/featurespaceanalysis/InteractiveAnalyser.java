/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
        new InteractiveAnalyser(codeDictionary, featureSpaceAnalyser);
    }

    private FeatureSpaceAnalyser featureSpaceAnalyser;

    private boolean expectedInput;

    private final Logger LOGGER = LoggerFactory.getLogger(FeatureSpaceAnalyser.class);
    private CodeReportFormatter codeReportFormatter;
    private FeatureReportFormatter featureReportFormatter;

    private static final String STOP_COMMAND = "stop";
    private static final String STOP_MESSAGE = "\nStop call detected. Stopping analyser...";

    private CodeDictionary codeDictionary;


    /**
     * Initiates the command line stopListener. This waits for input from the command line and processes the input.
     */
    public InteractiveAnalyser(final CodeDictionary codeDictionary, final FeatureSpaceAnalyser featureSpaceAnalyser) throws CodeNotValidException {
        expectedInput = true;
        this.featureSpaceAnalyser = featureSpaceAnalyser;
        this.codeDictionary = codeDictionary;
        this.codeReportFormatter = new CodeReportFormatter(featureSpaceAnalyser);
        this.featureReportFormatter = new FeatureReportFormatter(featureSpaceAnalyser);
        LOGGER.info(instructions());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in, FileManipulation.FILE_CHARSET))) {
            String line;
            while (true) {
                line = in.readLine();
                if ( line!=null && processCodeInput(line)) {
                    break;
                }
            }
            in.close();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private String switchExpectedInput() {
        expectedInput = !expectedInput;
        String message = "\nExpected input type changed to: ";
        if(expectedInput){
            return message + "Code\n";
        } else {
            return message + "Feature\n";
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
    private boolean processCodeInput(final String line) throws Exception, CodeNotValidException {

        if(line.equals("")){
            LOGGER.info(switchExpectedInput());
            return false;
        }else if(expectedInput && isCode(line)){
                LOGGER.info(codeReportFormatter.formatReport(codeDictionary.getCode(line)));

        }else if(!expectedInput && isFeature(line)) {
            LOGGER.info(featureReportFormatter.formatReport(line));

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

    private boolean isFeature(String line) {
        return featureSpaceAnalyser.isFeature(line);
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
                 FormattingUtils.repeatConcatString("#", depth) +
                 "\n### ***FEATURE SPACE ANALYSER INSTRUCTIONS***\n" +
                 "###\n" +
                 "### Prints feature profile information given a code which appears in the training bucket.\n" +
                 "### The table contains the features which predict the code,\n" +
                 "### the number of training examples containing the feature associated with the code,\n" +
                 "### and the number of training examples containing the feature in total.\n" +
                 "###\n" +
                 "### Type \"stop\" to quit program.\n" +
                 "### Return an empty line to toggle between feature and code lookup.\n" +
                 "### Type a code for a feature analysis of code (case sensitive).\n" +
                 "### Type a feature for a code analysis of feature (case sensitive).\n" +

                FormattingUtils.repeatConcatString("#", depth);
    }
}