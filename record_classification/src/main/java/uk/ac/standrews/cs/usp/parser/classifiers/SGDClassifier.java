//package uk.ac.standrews.cs.usp.parser.classifiers;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.Path;
//import org.apache.mahout.classifier.ClassifierResult;
//import org.apache.mahout.classifier.naivebayes.BayesUtils;
//import org.apache.mahout.classifier.sgd.AdaptiveLogisticRegression;
//import org.apache.mahout.classifier.sgd.CrossFoldLearner;
//import org.apache.mahout.classifier.sgd.L1;
//import org.apache.mahout.classifier.sgd.ModelSerializer;
//import org.apache.mahout.math.NamedVector;
//import org.apache.mahout.math.Vector;
//import org.apache.mahout.vectorizer.encoders.Dictionary;
//
//import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
//import uk.ac.standrews.cs.usp.parser.datastructures.Provenance;
//import uk.ac.standrews.cs.usp.parser.datastructures.Record;
//import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
//import uk.ac.standrews.cs.usp.parser.datastructures.classifications.Classification;
//import uk.ac.standrews.cs.usp.parser.datastructures.classifications.ClassificationSet;
//import uk.ac.standrews.cs.usp.parser.datastructures.classifications.ClassifierList;
//import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
//import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
//import uk.ac.standrews.cs.usp.parser.datastructures.vectors.VectorFactory;
//import uk.ac.standrews.cs.usp.parser.resolver.Pair;
//import uk.ac.standrews.cs.usp.tools.configuration.MachineLearningConfiguration;
//
///**
// * Implements Mahout's SGD Classifier. This is really here only for completeness,
// * the Classifier works better in practice.
// * @author jkc25
// */
//public class SGDClassifier extends AbstractClassifier {
//
//    private Properties properties;
//    private String sgdModelPath;
//    private Dictionary dictionary;
//    private int reps;
//    private boolean shuffle;
//    private Configuration conf;
//    private File labelindexPath;
//    private CrossFoldLearner classifier;
//
//    /**
//     * Create SGD classifier with default properties.
//     * @param vectorFactory vector factory
//     */
//    public SGDClassifier(final VectorFactory vectorFactory) {
//
//        super(vectorFactory);
//        properties = MachineLearningConfiguration.getDefaultProperties();
//        sgdModelPath = properties.getProperty("sgdModelPath");
//        dictionary = new Dictionary();
//        reps = Integer.parseInt(properties.getProperty("reps"));
//        shuffle = Boolean.parseBoolean(properties.getProperty("sgd.shuffle"));
//        conf = new Configuration();
//        labelindexPath = new File(properties.getProperty("labelindex"));
//
//    }
//
//    /**
//     * Create SGD classifier with custom properties.
//     * @param customProperties Customised properties file.
//     * @param vectorFactory vector factory
//     */
//    public SGDClassifier(final String customProperties, final VectorFactory vectorFactory) {
//
//        this(vectorFactory);
//
//        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
//        properties = mlc.extendDefaultProperties(customProperties);
//    }
//
//    @Override
//    public void train(final Bucket bucket) throws Exception {
//
//        //TODO get real cardinality
//        AdaptiveLogisticRegression learningAlgorithm = createLearningAlgorithm();
//
//        List<Record> recordList = getRecordsAsList(bucket);
//
//        Collections.shuffle(recordList);
//
//        trainLearningAlgorithm(learningAlgorithm, recordList);
//
//        learningAlgorithm.close();
//
//        checkModelOutputhExsists();
//
//        CrossFoldLearner cfl = learningAlgorithm.getBest().getPayload().getLearner();
//
//        ModelSerializer.writeBinary(sgdModelPath, cfl);
//    }
//
//    @Override
//    public Record classify(final Record record) throws IOException {
//
//        ClassificationSet resultSet = new ClassificationSet();
//        for (NamedVector vector : vectorFactory.generateVectorsFromRecord(record)) {
//            resultSet.add(getClassification(record, vector));
//        }
//        Record classifiedRecord = record;
//        classifiedRecord.addClassificationSet(resultSet);
//        return classifiedRecord;
//    }
//
//    /**
//     * Checks that the output path for the SGD model already exists, if not, creates it.
//     */
//    private void checkModelOutputhExsists() {
//
//        File modelPath = new File(sgdModelPath);
//        if (!modelPath.getParentFile().exists()) {
//            File modelPathParent = new File(modelPath.getParent());
//            if (!modelPathParent.mkdirs()) {
//                System.err.println("Failed to create " + sgdModelPath);
//            }
//        }
//    }
//
//    /**
//     * Trains the learning algorithm supplied with each record in the record list.
//     * Learning behaviour is governed by the learning parameters configured in the properties file.
//     * @param learningAlgorithm {@link AdaptiveLogisticRegression} learner to be trained.
//     * @param recordList List of {@link Record}s containing the training data.
//     */
//    private void trainLearningAlgorithm(final AdaptiveLogisticRegression learningAlgorithm, final List<Record> recordList) {
//
//        int count = 0;
//        for (int j = 0; j < reps; j++) {
//            for (int i = 0; i < recordList.size(); i++) {
//
//                if (shuffle) {
//                    Collections.shuffle(recordList);
//                }
//
//                Record record = recordList.get(i);
//                String actual = record.getClassificationSets().get(0).getClassifications().iterator().next().getCode().getCodeAsString();
//
//                for (NamedVector v : vectorFactory.generateVectorsFromRecord(record)) {
//                    System.out.println(count + " " + v);
//                    learningAlgorithm.train(count, dictionary.intern(actual), v);
//                }
//                count++;
//            }
//        }
//    }
//
//    /**
//     * Creates an {@link AdaptiveLogisticRegression} object that will be used for training and classification.
//     * @return AdaptiveLogisticRegression object initialized with parameters specified in the properties file.
//     */
//    private AdaptiveLogisticRegression createLearningAlgorithm() {
//
//        int features = Integer.parseInt(properties.getProperty("numFeatures"));
//        int noOfOutputClasses = Integer.parseInt(properties.getProperty("numberOfOutputClasses"));
//        AdaptiveLogisticRegression learningAlgorithm = new AdaptiveLogisticRegression(noOfOutputClasses, features, new L1());
//
//        int interval = Integer.parseInt(properties.getProperty("sgd.interval"));
//        int window = Integer.parseInt(properties.getProperty("sgd.averagingWindow"));
//        learningAlgorithm.setInterval(interval);
//        learningAlgorithm.setAveragingWindow(window);
//        return learningAlgorithm;
//    }
//
//    /**
//     * Returns a {@link Classification} for a given record and NamedVector.
//     * @param record {@link Record} containing the vector to be classified.
//     * @param vector {@link NamedVector} to be classified.
//     * @return Classification the result of the SGD Classification on the record and vector.
//     * @throws IOException If an error occurs reading from disk, i.e. the serialised model.
//     */
//    private Classification getClassification(final Record record, final NamedVector vector) throws IOException {
//
//        Map<Integer, String> labelMap = getLabelIndex();
//
//        getModelFromDefaultLocation();
//
//        Vector result = classifier.classifyFull(vector);
//        int cat = result.maxValueIndex();
//        double score = result.maxValue();
//        double ll = classifier.logLikelihood(cat, vector);
//
//        ClassifierResult cr = new ClassifierResult(labelMap.get(cat), score, ll);
//
//        Code code = getCode(cat);
//
//        Provenance provenance = new Provenance(ClassifierList.SGD, record.getCleanedDescription(), record.getCleanedDescription());
//
//        double confidence = getConfidence(cr.getLogLikelihood());
//
//        Classification classification = new Classification(code, provenance, confidence);
//
//        return classification;
//    }
//
//    /**
//     * Reads the labelindex file specified in the properties file.
//     * If none exists then creates a labelindex.
//     * @return labelIndex mapping of Intergers to Labels
//     * @throws IOException If the labelIndex cannot be read.
//     */
//    private Map<Integer, String> getLabelIndex() throws IOException {
//
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("2100");
//
//        if (!labelindexPath.exists()) {
//            BayesUtils.writeLabelIndex(conf, labels, new Path(properties.getProperty("labelindex")));
//        }
//
//        Map<Integer, String> labelMap = BayesUtils.readLabelIndex(conf, new Path(properties.getProperty("labelindex")));
//        return labelMap;
//    }
//
//    /**
//     * Adds all the vectors in a bucket to an ArrayList.
//     * @param bucket bucket of records to add to lists
//     * @return listOfRecords Records from the bucket in list form
//     */
//    private List<Record> getRecordsAsList(final Bucket bucket) {
//
//        ArrayList<Record> listOfRecords = new ArrayList<Record>(bucket.size());
//        for (Record record : bucket) {
//            listOfRecords.add(record);
//        }
//        return listOfRecords;
//    }
//
//    private double getConfidence(final double logLikelihood) {
//
//        //TODO fix this - return real confidence
//        System.out.println("Real Confidence: " + logLikelihood);
//        if (logLikelihood > -1) { return 1; }
//        if (logLikelihood < -1 && logLikelihood > -1.5) { return 0.8; }
//        if (logLikelihood < -1.5 && logLikelihood > -2) { return 0.5; }
//        if (logLikelihood < -2 && logLikelihood > -3) { return 0.2; }
//        if (logLikelihood < -3 && logLikelihood > -3.5) { return 0.1; }
//
//        return 0.0;
//    }
//
//    /**
//     * Gets an Occ Code from the {@link CodeFactory}.
//     * @param resultOfClassification the string representation of the classification code
//     * @return Code code representation of the resultOfClassification
//     * @throws IOException if resultOfClassification is not a valid code or CodeFactory cannot read the code list.
//     */
//    private Code getCode(final int resultOfClassification) throws IOException {
//
//        Code code = CodeFactory.getInstance().getCode(resultOfClassification);
//        return code;
//    }
//
//    @Override
//    public void getModelFromDefaultLocation() {
//
//        try {
//            if (classifier == null) {
//                classifier = ModelSerializer.readBinary(new FileInputStream(sgdModelPath), CrossFoldLearner.class);
//            }
//        }
//        catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public Pair<Code, Double> classify(TokenSet tokenSet) throws IOException {
//
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//}
