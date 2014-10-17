package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanMatchingSoundex;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.shef.wit.simmetrics.similaritymetrics.NeedlemanWunch;
import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap.ClosestMatchMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap.SimilarityMetric;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap.SimilarityMetricFromSimmetricFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap.StringSimilarityClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.ExactMatchClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketFilter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.BucketUtils;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
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
public final class ExperimentalMultipleClassificationTypes {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentalMultipleClassificationTypes.class);
    private static double DEFAULT_TRAINING_RATIO = 0.8;

    /**
     * Entry method for training and classifying a batch of records into
     * multiple codes.
     * 
     * @param args
     *            <file1> training file <file2> file to classify
     * @throws Exception
     *             If exception occurs
     */
    public static void main(final String[] args) throws Exception {

        ExperimentalMultipleClassificationTypes instance = new ExperimentalMultipleClassificationTypes();
        instance.run(args);
    }

    public void run(final String[] args) throws Exception {

        String experimentalFolderName;
        File goldStandard;
        Bucket trainingBucket;
        Bucket predictionBucket1;

        Timer timer = PipelineUtils.initAndStartTimer();

        experimentalFolderName = PipelineUtils.setupExperimentalFolders("Experiments");

        goldStandard = parseGoldStandFile(args);
        double trainingRatio = parseTrainingPct(args);
        boolean multipleClassifications = parseMultipleClassifications(args);

        File codeDictionaryFile = new File(MachineLearningConfiguration.getDefaultProperties().getProperty("codeDictionaryFile"));
        CodeDictionary codeDictionary = new CodeDictionary(codeDictionaryFile);

        BucketGenerator generator = new BucketGenerator(codeDictionary);
        Bucket allRecords = generator.generateTrainingBucket(goldStandard);

        Bucket[] trainingPredicition = randomlyAssignToTrainingAndPrediction(allRecords, trainingRatio);
        trainingBucket = trainingPredicition[0];

        PipelineUtils.printStatusUpdate();

        CodeIndexer codeIndex = new CodeIndexer(allRecords);

        Map<String, Classification> map = getMap(trainingBucket);

        AbstractStringMetric simMetric = new JaccardSimilarity();
        String identifier = "Jaccard";
        predictionBucket1 = trainingPredicition[1];
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket1, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new JaroWinkler();
        identifier = "JaroWinkler";
        Bucket predictionBucket2 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket2, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new DiceSimilarity();
        identifier = "DiceSimilarity";
        Bucket predictionBucket3 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket3, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new Levenshtein();
        identifier = "Levenshtein";
        Bucket predictionBucket4 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket4, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new NeedlemanWunch();
        identifier = "NeedlemanWunch";
        Bucket predictionBucket5 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket5, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new SmithWaterman();
        identifier = "SmithWaterman";
        Bucket predictionBucket6 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket6, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        simMetric = new ChapmanMatchingSoundex();
        identifier = "ChapmanMatchingSoundex";
        Bucket predictionBucket7 = copyOf(trainingPredicition[1]);
        classifyAndWrite(experimentalFolderName, trainingBucket, predictionBucket7, multipleClassifications, allRecords, codeIndex, map, simMetric, identifier);

        timer.stop();

    }

    private Bucket copyOf(final Bucket bucket) {

        Bucket newBucket = new Bucket();
        for (Record record : bucket) {
            newBucket.addRecordToBucket(record.copyOfOriginalRecord(record));
        }
        return newBucket;
    }

    private void classifyAndWrite(final String experimentalFolderName, final Bucket trainingBucket, final Bucket predictionBucket, final boolean multipleClassifications, final Bucket allRecords, final CodeIndexer codeIndex, final Map<String, Classification> map,
                    final AbstractStringMetric simMetric, final String identifier) throws Exception {

        ExactMatchClassifier exactMatchClassifier = new ExactMatchClassifier();
        exactMatchClassifier.setModelFileName(experimentalFolderName + "/Models/lookupTable");
        exactMatchClassifier.train(trainingBucket);
        StringSimilarityClassifier similariryClassifier = trainStringSimilarityClassifier(map, simMetric);

        IPipeline exactMatchPipeline = new ExactMatchPipeline(exactMatchClassifier);
        IPipeline stringSimPipeline = new ClassifierPipeline(similariryClassifier, trainingBucket, new LengthWeightedLossFunction(), multipleClassifications, true);

        Bucket notExactMatched = exactMatchPipeline.classify(predictionBucket);
        Bucket notStringSim = stringSimPipeline.classify(notExactMatched);
        Bucket stringSimClassified = stringSimPipeline.getSuccessfullyClassified();
        Bucket exactMatchClassified = exactMatchPipeline.getSuccessfullyClassified();

        Bucket allClassifed = BucketUtils.getUnion(exactMatchClassified, stringSimClassified);
        Bucket allOutputRecords = BucketUtils.getUnion(exactMatchClassified, notStringSim);

        LOGGER.info("Exact Matched Bucket Size: " + exactMatchClassified.size());
        LOGGER.info("Similarity Metric Bucket Size: " + stringSimClassified.size());

        writeRecords(experimentalFolderName, identifier, allOutputRecords);

        writeComparisonFile(experimentalFolderName, identifier, allOutputRecords);

        final Bucket uniqueRecordsOnly = BucketFilter.uniqueRecordsOnly(allOutputRecords);
        printAllStats(experimentalFolderName, codeIndex, allOutputRecords, uniqueRecordsOnly);
        printAllStats(experimentalFolderName, codeIndex, stringSimClassified, BucketFilter.uniqueRecordsOnly(stringSimClassified));

    }

    public StringSimilarityClassifier trainStringSimilarityClassifier(final Map<String, Classification> map, final AbstractStringMetric simMetric) {

        SimilarityMetricFromSimmetricFactory factory = new SimilarityMetricFromSimmetricFactory();
        SimilarityMetric<String> metric = factory.create(simMetric);
        ClosestMatchMap<String, Classification> closestMatchMap = new ClosestMatchMap<>(metric, map);
        return new StringSimilarityClassifier(closestMatchMap);
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

    private void writeRecords(final String experimentalFolderName, final String identifier, final Bucket allClassifed) throws IOException {

        final String nrsReportPath = "/Data/" + identifier + "/NRSData.txt";
        final File outputPath = new File(experimentalFolderName + nrsReportPath);
        Files.createParentDirs(outputPath);
        final DataClerkingWriter writer = new DataClerkingWriter(outputPath);

        for (final Record record : allClassifed) {
            writer.write(record);
        }
        writer.close();
    }

    private void writeComparisonFile(final String experimentalFolderName, final String identifier, final Bucket allClassifed) throws IOException, FileNotFoundException, UnsupportedEncodingException {

        final String comparisonReportPath = "/Data/" + identifier + "/comaprison.txt";
        final File outputPath2 = new File(experimentalFolderName + comparisonReportPath);
        Files.createParentDirs(outputPath2);

        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(outputPath2, "\t");
        for (final Record record : allClassifed) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

    private Map<String, Classification> getMap(final Bucket bucket) {

        return convert(prePopulateCache(bucket));

    }

    private Map<String, Classification> convert(final Map<TokenSet, Pair<Code, Double>> map) {

        Map<String, Classification> newMap = new HashMap<String, Classification>();
        for (TokenSet key : map.keySet()) {
            newMap.put(key.toString(), new Classification(map.get(key).getLeft(), key, map.get(key).getRight()));
        }
        return newMap;
    }

    private static File parseGoldStandFile(final String[] args) {

        File goldStandard = null;
        if (args.length > 4) {
            System.err.println("usage: $" + ExperimentalMultipleClassificationTypes.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>");
        }
        else {
            goldStandard = new File(args[0]);
            PipelineUtils.exitIfDoesNotExist(goldStandard);

        }
        return goldStandard;
    }

    private boolean parseMultipleClassifications(final String[] args) {

        if (args.length > 3) {
            System.err.println("usage: $" + ClassifyWithExsistingModels.class.getSimpleName() + "    <goldStandardDataFile>    <trainingRatio(optional)>    <output multiple classificatiosn");
        }
        else {
            if (args[2].equals("1")) { return true; }
        }
        return false;

    }

    private static double parseTrainingPct(final String[] args) {

        double trainingRatio = DEFAULT_TRAINING_RATIO;
        if (args.length > 1) {
            double userRatio = Double.valueOf(args[1]);
            if (userRatio > 0 && userRatio < 1) {
                trainingRatio = userRatio;
            }
            else {
                System.err.println("trainingRatio must be between 0 and 1. Exiting.");
                System.exit(1);
            }
        }
        return trainingRatio;
    }

    private Bucket[] randomlyAssignToTrainingAndPrediction(final Bucket bucket, final double trainingRatio) {

        Bucket[] buckets = initBuckets();

        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                buckets[0].addRecordToBucket(record);
            }
            else {
                buckets[1].addRecordToBucket(record);
            }
        }
        return buckets;
    }

    private Bucket[] initBuckets() {

        Bucket[] buckets = new Bucket[2];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

    private Map<TokenSet, Pair<Code, Double>> prePopulateCache(final Bucket cacheBucket) {

        Map<TokenSet, Pair<Code, Double>> cache = new HashMap<TokenSet, Pair<Code, Double>>();

        for (Record record : cacheBucket) {
            List<Classification> singles = getSinglyCodedTriples(record);
            cache = addAll(singles, cache);
        }
        return cache;
    }

    /**
     * Gets the singly coded triples, that is codeTriples that have only one coding.
     *
     * @param record the record to get single triples from
     * @return the singly coded triples
     */
    protected List<Classification> getSinglyCodedTriples(final Record record) {

        List<Classification> singles = new ArrayList<>();

        final Set<Classification> goldStandardClassificationSet = record.getGoldStandardClassificationSet();
        for (Classification codeTriple1 : goldStandardClassificationSet) {
            int count = 0;
            for (Classification codeTriple2 : goldStandardClassificationSet) {
                if (codeTriple1.getTokenSet().equals(codeTriple2.getTokenSet())) {
                    count++;
                }
            }
            if (count == 1) {
                singles.add(codeTriple1);
            }
        }

        return singles;
    }

    /**
     * Add all CodeTriples to the cache.
     * @param setOfCodeTriples Set to add
     * @return 
     */
    public Map<TokenSet, Pair<Code, Double>> addAll(final List<Classification> setOfCodeTriples, final Map<TokenSet, Pair<Code, Double>> cache) {

        for (Classification codeTriple : setOfCodeTriples) {
            cache.put(codeTriple.getTokenSet(), new Pair<Code, Double>(codeTriple.getCode(), codeTriple.getConfidence()));
        }
        return cache;
    }
}
