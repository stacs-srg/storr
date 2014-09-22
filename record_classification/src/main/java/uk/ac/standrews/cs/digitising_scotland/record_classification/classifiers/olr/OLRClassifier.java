package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.olr;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * OLRClassifier class that provides methods for training and classifying records.
 * This classifier utilises the {@link OLRCrossFold} objects in order to build the best possible models.
 * More information can be found on the project algorithms page:
 *  <a href="http://digitisingscotland.cs.st-andrews.ac.uk/record_classification/algorithms-information.html"> Algorithms Page</a>
 * <p/>
 * @author frjd2, jkc25
 * 
 */
public class OLRClassifier extends AbstractClassifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(OLRClassifier.class);
    private OLRCrossFold model = null;
    private final Properties properties;
    private CodeIndexer index;

    /** The Constant MODELPATH. Default is target/olrModelPath, but can be overwritten. */
    private static String modelPath = "target/olrModelPath";

    /**
     * Overrides the default path and sets to the path provided.
     * @param modelPath New path to write model to
     */
    public static void setModelPath(final String modelPath) {

        OLRClassifier.modelPath = modelPath;
    }

    /**
     * Constructor.
     * @param vectorFactory vector factory
     */
    public OLRClassifier(final VectorFactory vectorFactory) {

        super(vectorFactory);
        index = vectorFactory.getCodeIndexer();
        model = new OLRCrossFold();
        properties = MachineLearningConfiguration.getDefaultProperties();
    }

    /**
     * Constructor.
     *
     * @param customProperties custom properties file
     * @param vectorFactory vector factory
     */
    public OLRClassifier(final String customProperties, final VectorFactory vectorFactory) {

        super(vectorFactory);
        MachineLearningConfiguration mlc = new MachineLearningConfiguration();
        properties = mlc.extendDefaultProperties(customProperties);
    }

    /**
     * Trains an OLRCrossfold model on a bucket.
     *
     * @param bucket bucket to train on
     * @throws InterruptedException the interrupted exception
     */
    @Override
    public void train(final Bucket bucket) throws InterruptedException {

        ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();

        for (Record record : bucket) {
            final List<NamedVector> listOfVectors = vectorFactory.generateVectorsFromRecord(record);
            trainingVectorList.addAll(listOfVectors);
        }

        Collections.shuffle(trainingVectorList);

        model = new OLRCrossFold(trainingVectorList, properties);

        model.train();

        writeModel();
    }

    public void train(final Bucket bucket, final Matrix betaMatrix) throws InterruptedException {

        ArrayList<NamedVector> trainingVectorList = new ArrayList<NamedVector>();

        for (Record record : bucket) {
            final List<NamedVector> listOfVectors = vectorFactory.generateVectorsFromRecord(record);
            trainingVectorList.addAll(listOfVectors);
        }

        Collections.shuffle(trainingVectorList);

        model = new OLRCrossFold(trainingVectorList, properties, betaMatrix);

        model.train();

        writeModel();
    }

    /**
     * Write model.
     */
    private void writeModel() {

        try {
            serializeModel(modelPath);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record)
     */
    @Override
    public Record classify(final Record record) {

        if (model == null) {
            LOGGER.error("Model has not been trained.");
            return record;
        }

        List<NamedVector> vectorList = vectorFactory.generateVectorsFromRecord(record);
        Set<Classification> codeTripleSet = new HashSet<>();

        for (NamedVector vector : vectorList) {
            Integer classificationID = model.classifyFull(vector).maxValueIndex();
            Code code = index.getCode(classificationID);
            double confidence = Math.exp(model.logLikelihood(classificationID, vector)); //TODO test
            codeTripleSet.add(new Classification(code, new TokenSet(record.getDescription()), confidence));
        }

        record.addAllCodeTriples(codeTripleSet);

        return record;
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket)
     */
    @Override
    public Bucket classify(final Bucket bucket) throws IOException {

        if (model == null) {
            LOGGER.error("Model has not been trained.");
            return bucket;
        }
        return super.classify(bucket);
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        Pair<Code, Double> pair;
        NamedVector vector = vectorFactory.createNamedVectorFromString(tokenSet.toString(), "unknown");
        Vector classifyFull = model.classifyFull(vector);
        int classificationID = classifyFull.maxValueIndex();
        Code code = index.getCode(classificationID);
        double confidence = Math.exp(model.logLikelihood(classificationID, vector));
        pair = new Pair<>(code, confidence);
        return pair;
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#getModelFromDefaultLocation()
     */
    @Override
    public OLRClassifier getModelFromDefaultLocation() {

        OLRClassifier olr = null;
        try {
            olr = deSerializeModel(modelPath);
            model = olr.model;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return olr;
    }

    /**
     * Allows serialization of the model to file.
     *
     * @param filename name of file to serialize model to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void serializeModel(final String filename) throws IOException {

        index.writeCodeFactory(new File(filename + "CodeFactory"));
        DataOutputStream out = OLR.getDataOutputStream(filename);
        write(out);
        out.close();
    }

    private void write(final DataOutputStream outputStream) throws IOException {

        vectorFactory.write(outputStream);
        model.write(outputStream);
    }

    private void readFields(final DataInputStream inputStream) throws IOException {

        vectorFactory.readFields(inputStream);
        model.readFields(inputStream);
    }

    /**
     * Allows de-serialization of a model from a file. The de-serialized model is not trainable.
     *
     * @param filename name of file to de-serialize
     * @return {@link OLRClassifier} that has been read from disk. Does not contain all training vectors so can
     * only be used for classification
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public OLRClassifier deSerializeModel(final String filename) throws IOException {

        DataInputStream in = OLR.getDataInputStream(filename);
        VectorFactory vectorFactory = new VectorFactory();
        //  vectorFactory.readFields(in);
        OLRClassifier olrClassifier = new OLRClassifier(vectorFactory);
        olrClassifier.readFields(in);

        return olrClassifier;
    }

}
