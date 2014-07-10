package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.vectors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.machinelearning.tokenizing.StandardTokenizerIterable;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.tools.configuration.MachineLearningConfiguration;

/**
 * Factory that allows us to create vectors from strings.
 * Created by fraserdunlop on 23/04/2014 at 19:34.
 */
public class VectorFactory {

    private SimpleVectorEncoder vectorEncoder;
    private int numFeatures;

    /**
     * Constructs an empty {@link VectorFactory} with number of features set to 0 and a new vectorEncoder.
     */
    public VectorFactory() {

        vectorEncoder = new SimpleVectorEncoder();
        numFeatures = 0;
    }

    /**
     * Constructs a new {@link VectorFactory} from the specified {@link Bucket}.
     *
     * @param bucket bucket
     */
    public VectorFactory(final Bucket bucket) {

        vectorEncoder = new SimpleVectorEncoder();
        for (Record record : bucket) {
            updateDictionary(record.getCleanedDescription());
        }
        setNumFeatures();
    }

    private void setNumFeatures() {

        numFeatures = vectorEncoder.getDictionarySize();
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
        Set<CodeTriple> goldStandardClassificationSet = record.getGoldStandardClassificationSet();

        if (!goldStandardClassificationSet.isEmpty()) {
            vectors.addAll(createNamedVectorsWithGoldStandardCodes(record));
        }
        else {
            System.out.println("Record has no gold standard: " + record.getCleanedDescription()); //FIXME debug only
            vectors.add(createNamedVectorFromString(record.getCleanedDescription(), "noGoldStandard"));
        }
        return vectors;
    }

    private List<NamedVector> createNamedVectorsWithGoldStandardCodes(final Record record) {

        List<NamedVector> vectors = new ArrayList<>();
        Set<CodeTriple> goldStandardCodeTriple = record.getGoldStandardClassificationSet();

        for (CodeTriple codeTriple : goldStandardCodeTriple) {
            Integer id = codeTriple.getCode().getID();
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

        Vector vector = new RandomAccessSparseVector(numFeatures);
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

    /**
     * Write.
     *
     * @param out the out
     * @throws java.io.IOException Signals that an I/O exception has occurred.
     */
    public void write(final DataOutputStream out) throws IOException {

        out.writeInt(numFeatures);
        vectorEncoder.write(out);
    }

    /**
     * Read fields.
     *
     * @param in the in
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void readFields(final DataInputStream in) throws IOException {

        numFeatures = in.readInt();
        vectorEncoder.readFields(in);
    }
}
