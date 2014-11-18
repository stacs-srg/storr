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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr.OLRClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ClassifierPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.ExactMatchPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.IPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.MetricsWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Timer;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

import com.google.common.io.Files;

/**
 * This class integrates the training of machine learning models and the
 * classification of records using those models. The classification process is
 * as follows: <br>
 * <br>
 * The gold standard training file is read in from the command line and a
 * {@link Bucket} of {@link Record}s are created from this file. A
 * {@link VectorFactory} is then created to manage the creation of vectors for
 * these records. The vectorFactory also manages the mapping of vectors IDs to
 * words, ie the vector dictionary. <br>
 * <br>
 * An {@link AbstractClassifier} is then created from the training bucket and
 * the model(s) are trained and saved to disk. <br>
 * <br>
 * The records to be classified are held in a file with the correct format as
 * specified by NRS. One record per line. This class initiates the reading of
 * these records. These are stored as {@link Record} objects inside a
 * {@link Bucket}. <br>
 * <br>
 * After the records have been created and stored in a bucket, classification
 * can begin. This is carried out by the {@link BucketClassifier} class which in
 * turn implements the {@link ClassifierPipeline}. Please see this
 * class for implementation details. <br>
 * <br>
 * Some initial metrics are then printed to the console and classified records
 * are written to file (target/NRSData.txt).
 * 
 * @author jkc25, frjd2
 */
public final class ClassifyWithExistingModels {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassifyWithExistingModels.class);

    /**
     * Entry method for training and classifying a batch of records into
     * multiple codes.
     * 
     * @param args
     *            <file1> training file <file2> file to classify
     * @throws Exception
     *             If exception occurs
     */
    public static void main(final String[] args) throws Exception, CodeNotValidException {

        ClassifyWithExistingModels instance = new ClassifyWithExistingModels();
        instance.run(args);

    }

    public ClassifyWithExistingModels() {

    }

    public Bucket run(final String[] args) throws Exception, CodeNotValidException {

        Timer timer = PipelineUtils.initAndStartTimer();

        String experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        File predictionFile = parsePredictionFile(args);
        String modelLocation = parseModelLocation(args);
        boolean multipleClassifications = parseMultipleClassifications(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);
        BucketGenerator generator = new BucketGenerator(codeDictionary);
        Bucket allInputRecords = generator.createPredictionBucket(predictionFile);

        PipelineUtils.printStatusUpdate();

        final ExactMatchClassifier existingExactMatchClassifier = PipelineUtils.getExistingExactMatchClassifier(modelLocation);
        final OLRClassifier existingOLRModel = PipelineUtils.getExistingOLRModel(modelLocation);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(existingExactMatchClassifier);
        IPipeline machineLearningClassifier = new ClassifierPipeline(existingOLRModel, allInputRecords, new LengthWeightedLossFunction(), multipleClassifications, true);

        Bucket notExactMatched = exactMatchPipeline.classify(allInputRecords);
        Bucket notMachineLearned = machineLearningClassifier.classify(notExactMatched);
        Bucket successfullyClassifiedMachineLearning = machineLearningClassifier.getSuccessfullyClassified();

        LOGGER.info("Exact Matched Bucket Size: " + exactMatchPipeline.getSuccessfullyClassified().size());
        LOGGER.info("Machine Learned Bucket Size: " + successfullyClassifiedMachineLearning.size());
        LOGGER.info("Not Classifed Bucket Size: " + notMachineLearned.size());

        Bucket allClassifed = BucketUtils.getUnion(exactMatchPipeline.getSuccessfullyClassified(), successfullyClassifiedMachineLearning);
        Bucket allOutputRecords = BucketUtils.getUnion(allClassifed, notMachineLearned);

        writeRecords(experimentalFolderName, allOutputRecords);

        writeComparisons(experimentalFolderName, allOutputRecords);

        CodeIndexer codeIndexer = existingOLRModel.getVectorFactory().getCodeIndexer();

        LOGGER.info("********** Output Stats **********");

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(allOutputRecords);
        printAllStats(experimentalFolderName, codeIndexer, allOutputRecords, uniqueRecordsOnly);
        printAllStats(experimentalFolderName, codeIndexer, successfullyClassifiedMachineLearning, BucketFilter.uniqueRecordsOnly(successfullyClassifiedMachineLearning));
        timer.stop();

        return allOutputRecords;
    }

    private void writeComparisons(final String experimentalFolderName, final Bucket allClassifed) throws IOException, FileNotFoundException, UnsupportedEncodingException {

        final String comparisonReportPath = "/Data/" + "MachineLearning" + "/comaprison.txt";
        final File outputPath2 = new File(experimentalFolderName + comparisonReportPath);
        Files.createParentDirs(outputPath2);

        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(outputPath2, "\t");
        for (final Record record : allClassifed) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    private void writeRecords(final String experimentalFolderName, final Bucket allClassifed) throws IOException {

        final String nrsReportPath = "/Data/" + "MachineLearning" + "/NRSData.txt";
        final File outputPath = new File(experimentalFolderName + nrsReportPath);
        Files.createParentDirs(outputPath);
        final DataClerkingWriter writer = new DataClerkingWriter(outputPath);

        for (final Record record : allClassifed) {
            writer.write(record);
        }
        writer.close();
    }

    private void printAllStats(final String experimentalFolderName, final CodeIndexer codeIndex, final Bucket allClassifed, final Bucket uniqueRecordsOnly) throws IOException {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(allClassifed, codeIndex), codeIndex);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(allClassifed, codeMetrics);
        MetricsWriter metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write("machine learning", "firstBucket");
        accuracyMetrics.prettyPrint("All Records");

        LOGGER.info("Unique Only");
        LOGGER.info("Unique Only  Bucket Size: " + uniqueRecordsOnly.size());

        CodeMetrics codeMetrics1 = new CodeMetrics(new StrictConfusionMatrix(uniqueRecordsOnly, codeIndex), codeIndex);
        accuracyMetrics = new ListAccuracyMetrics(uniqueRecordsOnly, codeMetrics1);
        accuracyMetrics.prettyPrint("Unique Only");
        metricsWriter = new MetricsWriter(accuracyMetrics, experimentalFolderName, codeIndex);
        metricsWriter.write("machine learning", "unique records");
        accuracyMetrics.prettyPrint("Unique Records");
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>    <output multiple classificatiosn");
        }
        else {
            if (args[2].equals("1")) { return true; }
        }
        return false;

    }

    private File parsePredictionFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);
        }
        return goldStandard;

    }

    private String parseModelLocation(final String[] args) {

        String modelLocation = null;
        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            modelLocation = args[1];
            PipelineUtils.exitIfDoesNotExist(new File(modelLocation));
        }
        return modelLocation;
    }
}
