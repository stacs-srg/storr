package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.StandardTokenizerIterable;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Factory that allows us to create vectors from strings.
 * Created by fraserdunlop on 23/04/2014 at 19:34.
 */
public class VectorFactory implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5369887941319861994L;

    /** The index. */
    private CodeIndexer index;

    /** The vector encoder. */
    private SimpleVectorEncoder vectorEncoder;

    /**
     * Constructs an empty {@link VectorFactory} with number of features set to 0 and a new vectorEncoder.
     */
    public VectorFactory() {

        index = new CodeIndexer();
        vectorEncoder = new SimpleVectorEncoder();
    }

    /**
     * Constructs a new {@link VectorFactory} from the specified {@link Bucket}.
     *
     * @param bucket bucket
     * @param index the index
     */
    public VectorFactory(final Bucket bucket, final CodeIndexer index) {

        this.index = index;
        vectorEncoder = new SimpleVectorEncoder();
        updateDictionary(bucket);
    }

    /**
     * Updates the dictionary with the tokens for all the records in the given bucket.
     *
     * @param bucket the bucket
     */
    public void updateDictionary(final Bucket bucket) {

        for (Record record : bucket) {
            for (Classification c : record.getGoldStandardClassificationSet()) {
                updateDictionary(c.getTokenSet().toString());
            }
        }
        setNumFeatures();

    }

    /**
     * Sets the num features.
     */
    private void setNumFeatures() {

        int numFeatures = vectorEncoder.getDictionarySize();
        MachineLearningConfiguration.getDefaultProperties().setProperty("numFeatures", String.valueOf(numFeatures));
    }

    /**
     * Creates a {@link NamedVector} from the cleaned description of a {@link Record}.
     * If a gold standard coding exists this will be used for the name of the vector.
     * If no gold standard then "noGoldStandard" will be used.
     * A List<NamedVector> is returned as the record may have more than one line in the original description.
     * @param record Record to generate vector for.
     * @return List<NamedVector> List of {@link NamedVector} for this record
     */
    public List<NamedVector> generateVectorsFromRecord(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();
        Set<Classification> goldStandardClassificationSet = record.getGoldStandardClassificationSet();

        if (!goldStandardClassificationSet.isEmpty()) {
            vectors.addAll(createNamedVectorsWithGoldStandardCodes(record));
        }
        else {
            vectors.addAll(createUnNamedVectorsFromDescriptopn(record.getDescription()));
        }
        return vectors;
    }

    /**
     * Creates a new Vector object.
     *
     * @param description the description
     * @return the collection<? extends named vector>
     */
    private Collection<? extends NamedVector> createUnNamedVectorsFromDescriptopn(final List<String> description) {

        List<NamedVector> vectorList = new ArrayList<>();

        for (String string : description) {
            Vector v = createVectorFromString(string);
            vectorList.add(new NamedVector(v, "noGoldStandard"));
        }

        return vectorList;
    }

    /**
     * Creates a new Vector object.
     *
     * @param record the record
     * @return the list< named vector>
     */
    private List<NamedVector> createNamedVectorsWithGoldStandardCodes(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();
        Set<Classification> goldStandardCodeTriple = record.getGoldStandardClassificationSet();

        for (Classification codeTriple : goldStandardCodeTriple) {
            Integer id = index.getID(codeTriple.getCode());
            vectors.add(createNamedVectorFromString(codeTriple.getTokenSet().toString(), id.toString()));
        }

        return vectors;
    }

    /**
     * Creates a vector from a string using {@link SimpleVectorEncoder}
     * to encode the tokens and StandardTokenizerIterable to
     * tokenize the string.
     *
     * @param description the string to vectorize
     * @return a vector encoding of the string
     */
    public Vector createVectorFromString(final String description) {

        Vector vector = new RandomAccessSparseVector(getNumberOfFeatures());
        addFeaturesToVector(vector, description);
        return vector;
    }

    /**
     * Creates a named vector from a string using {@link SimpleVectorEncoder}
     * to encode the tokens and {StandardTokenizerIterable} to
     * tokenize the string.
     *
     * @param description the string to vectorize
     * @param name        name
     * @return a vector encoding of the string
     */
    public NamedVector createNamedVectorFromString(final String description, final String name) {

        Vector vector = createVectorFromString(description);
        return new NamedVector(vector, name);
    }

    /**
     * Adds the features to vector.
     *
     * @param vector the vector
     * @param description the description
     */
    private void addFeaturesToVector(final Vector vector, final String description) {

        StandardTokenizerIterable tokenStream = new StandardTokenizerIterable(Version.LUCENE_36, new StringReader(description));
        for (CharTermAttribute attribute : tokenStream) {
            vectorEncoder.addToVector(attribute.toString(), vector);
        }
    }

    /**
     * Adds all the tokens in the specified string to this {@link VectorFactory}'s dictionary.
     * @param description String to add
     */
    public void updateDictionary(final String description) {

        String descriptionLower = description.toLowerCase();
        StandardTokenizerIterable tokenStream = new StandardTokenizerIterable(Version.LUCENE_36, new StringReader(descriptionLower));
        for (CharTermAttribute attribute : tokenStream) {
            vectorEncoder.updateDictionary(attribute.toString());
        }
        setNumFeatures();
    }

    //
    //    /**
    //     * Write.
    //     *
    //     * @param outputStream the out
    //     * @throws IOException Signals that an I/O exception has occurred.
    //     */
    //    public void write(final ObjectOutputStream outputStream) throws IOException {
    //
    //        // vectorEncoder.write(outputStream);
    //        outputStream.writeObject(vectorEncoder);
    //        outputStream.writeObject(index);
    //
    //    }

    //    /**
    //     * Read fields.
    //     *
    //     * @param in the in
    //     * @throws IOException Signals that an I/O exception has occurred.
    //     * @throws ClassNotFoundException the class not found exception
    //     */
    //    public void readFields(final DataInputStream in) throws IOException, ClassNotFoundException {
    //
    //        vectorEncoder.readFields(in);
    //        ObjectInputStream objectInputStream = new ObjectInputStream(in);
    //        index = (CodeIndexer) objectInputStream.readObject();
    //    }

    /**
     * Gets the code indexer that was used to construct this vector factory.
     *
     * @return the code indexer
     */
    public CodeIndexer getCodeIndexer() {

        return index;
    }

    /**
     * Gets the number of features.
     *
     * @return the number of features
     */
    public int getNumberOfFeatures() {

        return vectorEncoder.getDictionarySize();
    }
}
