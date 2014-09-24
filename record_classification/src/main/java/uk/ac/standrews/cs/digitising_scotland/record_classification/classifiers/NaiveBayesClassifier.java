package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.Writer;
import org.apache.hadoop.io.Text;
import org.apache.mahout.classifier.ClassifierResult;
import org.apache.mahout.classifier.naivebayes.AbstractNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.BayesUtils;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.vectorizer.encoders.Dictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.CustomVectorWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Class has methods for training and classifying using the Mahout Naive Bayes
 * Classifier. The most two important methods are train() and classify().
 * 
 * @author jkc25
 * 
 */
public class NaiveBayesClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(NaiveBayesClassifier.class);

    /** The {@link Configuration}. */
    private Configuration conf;

    /** The {@link FileSystem}. */
    private FileSystem fs;

    /** The properties. */
    private Properties properties = MachineLearningConfiguration.getDefaultProperties();

    /** The model. */
    private NaiveBayesModel model = null;

    private static final double STATIC_CONFIDENCE = 0.1;

    VectorFactory vectorFactory;

    CodeIndexer index;

    /**
     * Create Naive Bayes classifier with default properties.
     * 
     * @param vectorFactory
     *            This is the vector factory used when creating vectors for each
     *            record.
     */
    public NaiveBayesClassifier() {

        index = new CodeIndexer();
        vectorFactory = new VectorFactory();
        try {
            conf = new Configuration();
            fs = FileSystem.get(conf);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Create Naive Bayes classifier with custom properties.
     * 
     * @param customProperties
     *            Customised properties file.
     * @param vectorFactory
     *            This is the vector factory used when creating vectors for each
     *            record.
     */
    public NaiveBayesClassifier(final String customProperties) {

        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
        properties = mlc.extendDefaultProperties(customProperties);
    }

    public void train(final Bucket trainingBucket) throws Exception {

        index = new CodeIndexer(trainingBucket);
        vectorFactory = new VectorFactory(trainingBucket, index);
        File trainingVectorsDirectory = new File(properties.getProperty("trainingVectorsDirectory"));
        String trainingVectorFile = trainingVectorsDirectory.getAbsolutePath() + "/part-m-00000";
        String naiveBayesModelPath = properties.getProperty("naiveBayesModelPath");

        CustomVectorWriter vectorWriter = createVectorWriter(trainingVectorFile);
        writeTrainingVectorsToDisk(trainingBucket, vectorWriter);
        vectorWriter.close();
        writeLabelIndex(trainingBucket);
        model = trainNaiveBayes(trainingVectorFile, naiveBayesModelPath);
    }

    /**
     * Write label index.
     *
     * @param trainingBucket the training bucket
     */
    private void writeLabelIndex(final Bucket trainingBucket) {

        Collection<String> seen = new HashSet<String>();

        for (Record record : trainingBucket) {
            Set<Classification> codeTriple = record.getOriginalData().getGoldStandardClassifications();
            for (Classification codeTriple2 : codeTriple) {
                String theLabel = codeTriple2.getCode().getCodeAsString();
                if (!seen.contains(theLabel)) {
                    Utils.writeToFile(theLabel + ", ", "labelindex.csv", true);
                    seen.add(theLabel);
                }
            }

        }

    }

    /**
     * Writes each vector in the bucket using {@link CustomVectorWriter}
     * vectorWriter.
     * 
     * @param bucket
     *            {@link Bucket} containing the vectors you want to write.
     * @param vectorWriter
     *            {@link CustomVectorWriter} instantiated with output path and
     *            types.
     * @throws IOException
     *             IO Error
     */
    private void writeTrainingVectorsToDisk(final Bucket bucket, final CustomVectorWriter vectorWriter) throws IOException {

        for (Record record : bucket) {

            List<NamedVector> vectors = vectorFactory.generateVectorsFromRecord(record);
            for (Vector vector : vectors) {
                vectorWriter.write(vector);
            }

        }
    }

    /**
     * Creates a {@link CustomVectorWriter} to write vectors to disk.
     * {@link CustomVectorWriter} writes out {@link Text},
     * {@link VectorWritable} pairs into the file specified.
     * 
     * @param trainingVectorLocation
     *            location to write vectors to.
     * @return {@link Writer} with trainingVectorLocation as it's output path
     *         and {@link Text}, {@link VectorWritable} as its types.
     * @throws IOException
     *             I/O Error
     */
    private CustomVectorWriter createVectorWriter(final String trainingVectorLocation) throws IOException {

        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, new Path(trainingVectorLocation), Text.class, VectorWritable.class);
        return new CustomVectorWriter(writer);
    }

    /**
     * Gets the classification.
     *
     * @param record the record
     * @param namedVector the named vector
     * @return the classification
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private Classification getClassification(final Record record, final NamedVector namedVector) throws IOException {

        Dictionary dictionary = buildDictionaryFromLabelMap(properties.getProperty("labelindex"));

        AbstractNaiveBayesClassifier classifier = getClassifier();

        Vector resultVector = classifier.classifyFull(namedVector);

        ClassifierResult cr = getClassifierResult(resultVector, dictionary);

        Code code = getCode(resultVector.maxValueIndex());

        double confidence = getConfidence(cr.getLogLikelihood());

        return new Classification(code, new TokenSet(record.getDescription()), confidence);
    }

    /**
     * Builds a {@link ClassifierResult} from a result vector and the dictionary
     * of interger/classifications.
     * 
     * @param result
     *            result vector from the Naive Bayes Classifier.
     * @param dictionary
     *            Dictionary containing the mapping of Integers to
     *            classifications (labels)
     * @return {@link ClassifierResult} containing the details of the
     *         classification
     */
    private ClassifierResult getClassifierResult(final Vector result, final Dictionary dictionary) {

        int categoryOfClassification = result.maxValueIndex();
        return new ClassifierResult(dictionary.values().get(categoryOfClassification));
    }

    /**
     * Builds a {@link StandardNaiveBayesClassifier}.
     * 
     * @return StandardNaiveBayesClassifier built from the model stored in
     *         "target/naiveBayesModelPath"
     * @throws IOException
     *             if the model cannot be read
     */
    private AbstractNaiveBayesClassifier getClassifier() throws IOException {

        if (model == null) {
            model = getModel();
        }

        return new StandardNaiveBayesClassifier(model);

    }

    /**
     * Reads and returns the Naive Bayes model from the disk.
     * 
     * @return {@link NaiveBayesModel} from disk
     * @throws IOException
     *             if the model cannot be read
     */
    private NaiveBayesModel getModel() throws IOException {

        Configuration configuration = new Configuration();
        String modelLocation = "target/naiveBayesModelPath";
        return NaiveBayesModel.materialize(new Path(modelLocation), configuration);
    }

    /**
     * Builds a {@link Dictionary} from a label map. Usually used to read the
     * labelindex written by Mahout {@link BayesUtils}.
     *
     * @param labelMapPath            location of the label index written by the mahout
     *            trainNaiveByaes job return dictionary {@link Dictionary}
     *            containing all label/class mappings
     * @return the dictionary
     */
    private Dictionary buildDictionaryFromLabelMap(final String labelMapPath) {

        Configuration configuration = new Configuration();
        Dictionary dictionary = new Dictionary();
        Map<Integer, String> labelMap = BayesUtils.readLabelIndex(configuration, new Path(labelMapPath));

        for (int i = 0; i < labelMap.size(); i++) {
            dictionary.intern(labelMap.get(i).trim());
        }

        return dictionary;
    }

    /**
     * TODO - finish writing this method Generates a confidence value from a log
     * likelihood.
     *
     * @param logLikelihood the log likelihood
     * @return confidence score
     */
    private double getConfidence(final double logLikelihood) {

        // TODO FIXME return real confidence... might be hard as this is Bayes
        // double confidence = Math.pow(logLikelihood, 2);
        // confidence = Math.sqrt(confidence);
        //return STATIC_CONFIDENCE;
        return Math.exp(logLikelihood);
    }

    /**
     * Gets an Occ Code from the {@link CodeIndexer}.
     * 
     * @param resultOfClassification
     *            the string representation of the classification code
     * @return Code code representation of the resultOfClassification
     * @throws IOException
     *             if resultOfClassification is not a valid code or CodeFactory
     *             cannot read the code list.
     */
    private Code getCode(final int resultOfClassification) throws IOException {

        return vectorFactory.getCodeIndexer().getCode(resultOfClassification);

    }

    /**
     * Trains a Naive Bayes model with the vectors supplied.
     *
     * @param trainingVectorLocation the training vector location
     * @param naiveBayesModelPath the naive bayes model path
     * @return model {@link NaiveBayesModel} the trained model
     * @throws Exception the exception
     */
    private NaiveBayesModel trainNaiveBayes(final String trainingVectorLocation, final String naiveBayesModelPath) throws Exception {

        // -i = training vector location, -el = extract label index, -li- place
        // to store labelindex
        // -o = model output path, -ow = overwrite existing vectors
        String[] args = new String[]{"-i", trainingVectorLocation, "-o", naiveBayesModelPath, "-el", "-li", properties.getProperty("labelindex"), "-ow"};

        TrainNaiveBayesJob.main(args);

        return getModel();
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#getModelFromDefaultLocation()
     */
    public NaiveBayesClassifier getModelFromDefaultLocation() {

        try {
            model = getModel();
        }
        catch (IOException e) {
            LOGGER.error("Could not get model from default location. IOExcepion has occured\n" + e.getMessage(), e);
        }
        return this;
    }

    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        Pair<Code, Double> pair;
        NamedVector vector = vectorFactory.createNamedVectorFromString(tokenSet.toString(), "unknown");
        int classificationID = getClassifier().classifyFull(vector).maxValueIndex();
        Code code = vectorFactory.getCodeIndexer().getCode(classificationID);
        // double confidence =
        // Math.exp(getClassifier().logLikelihood(classificationID, vector));
        // TODO THIS WONT WORK - Baysian classifier don't give real confidence measures - Need to fudge it

        pair = new Pair<>(code, STATIC_CONFIDENCE);
        return pair;
    }
}
