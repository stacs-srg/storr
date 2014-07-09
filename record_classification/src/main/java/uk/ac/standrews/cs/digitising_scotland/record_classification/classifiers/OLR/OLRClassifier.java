package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.OLR;

import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors.VectorFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Pair;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * OLRClassifier
 * //TODO some nice javadoc
 * <p/>
 * Created by fraserdunlop on 25/04/2014 at 15:21.
 */
public class OLRClassifier extends AbstractClassifier {

    /** The model. */
    private OLRCrossFold model = null;

    /** The properties. */
    private final Properties properties;

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
            trainingVectorList.addAll(vectorFactory.generateVectorsFromRecord(record));
        }

        Collections.shuffle(trainingVectorList);

        model = new OLRCrossFold(trainingVectorList, properties);

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
            System.out.println("Model has not been trained.");
            return record;
        }

        List<NamedVector> vectorList = vectorFactory.generateVectorsFromRecord(record);
        Integer classificationID;
        Code code;
        Set<CodeTriple> codeTripleSet = new HashSet<>();
        for (NamedVector vector : vectorList) {
            classificationID = model.classifyFull(vector).maxValueIndex();

            code = CodeFactory.getInstance().getCode(classificationID);
            double confidence = Math.exp(model.logLikelihood(classificationID, vector)); //TODO test
            codeTripleSet.add(new CodeTriple(code, new TokenSet(record.getCleanedDescription()), confidence));
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
            System.out.println("Model has not been trained.");
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
        Code code = CodeFactory.getInstance().getCode(classificationID);
        double confidence = Math.exp(model.logLikelihood(classificationID, vector));
        pair = new Pair<>(code, confidence);
        return pair;
    }

    /**
     * Allows serialization of the model to file.
     *
     * @param filename name of file to serialize model to
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void serializeModel(final String filename) throws IOException {

        DataOutputStream out = OLR.getDataOutputStream(filename);
        write(out);
        out.close();
    }

    private void write(final DataOutputStream out) throws IOException {

        vectorFactory.write(out);
        model.write(out);
    }

    private void readFields(final DataInputStream in) throws IOException {

        //  vectorFactory.readFields(in);
        model.readFields(in);
    }

    /**
     * Allows de-serialization of a model from a file. The de-serialized model is not trainable.
     *
     * @param filename name of file to de-serialize
     * @return {@link OLRClassifier} that has been read from disk. Does not contain all training vectors so can
     * only be used for classification
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static OLRClassifier deSerializeModel(final String filename) throws IOException {

        DataInputStream in = OLR.getDataInputStream(filename);
        VectorFactory vectorFactory = new VectorFactory();
        vectorFactory.readFields(in);
        OLRClassifier olrClassifier = new OLRClassifier(vectorFactory);
        olrClassifier.readFields(in);
        return olrClassifier;
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#getModelFromDefaultLocation()
     */
    @Override
    public void getModelFromDefaultLocation() {

        try {
            model = deSerializeModel(modelPath).model;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
