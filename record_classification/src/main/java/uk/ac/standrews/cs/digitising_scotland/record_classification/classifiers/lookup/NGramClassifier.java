package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import org.apache.lucene.analysis.TokenStream;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * NGram Lookup table classifier. During the train() phase the classifier builds
 * a lookup table containing the classifications for each input string. During
 * the classify() phase the description in the record is broken down into nGrams
 * and each nGram is matched against the lookup table.
 * 
 * @author frjd2, jkc25
 */
public class NGramClassifier extends AbstractClassifier implements Serializable {

    private static final long serialVersionUID = 832228090104039831L;
    private LookupTableClassifier lookupTableClassifier;
    private String defaultModelLocation = "target/nGramModel";

    /**
     * Constructs a new {@link NGramClassifier} with an empty lookup table. To
     * construct the lookup table the user needs to call train().
     */
    public NGramClassifier() {

        this(new Bucket());
    }

    /**
     * Constructs a new {@link NGramClassifier} and uses the Bucket suppled to
     * build the look up table. This is the equivalent of calling train().
     * 
     * @param bucket
     *            {@link Bucket} of training records
     */
    public NGramClassifier(final Bucket bucket) {

        lookupTableClassifier = new LookupTableClassifier(bucket);
        try {
            writeModel(defaultModelLocation);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void train(final Bucket bucket) throws Exception {

        for (Record record : bucket) {
            lookupTableClassifier.addRecordToLookupTable(record);
        }
        writeModel(defaultModelLocation);
    }

    @Override
    public Record classify(final Record record) throws IOException {

        Set<CodeTriple> resultSet = classifyGrams(record);
        record.addAllCodeTriples(resultSet);
        return record;
    }

    /**
     * Classifies the {@link NGramSubstrings} of a Description using a
     * {@link LookupTableClassifier}.
     * 
     * @param record
     *            - The Description of the record to be classified.
     * @return Set of {@link CodeTriple} of the {@link NGramSubstrings} of the
     *         Description.
     * @throws IOException
     *             if a failure occurs while reading tokens from the
     *             {@link TokenStream}. Error handling should be refactored.
     */
    private Set<CodeTriple> classifyGrams(final Record record) throws IOException {

        NGramSubstrings grams = new NGramSubstrings(record.getDescription());
        return lookupTableClassifier.classify(grams, record);
    }

    /**
     * Writes model to file. File name is fileName.ser
     * 
     * @param fileName
     *            name of file to write model to
     * @throws IOException
     *             if model location cannot be read
     */
    public void writeModel(final String fileName) throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        write(oos);
    }

    private void write(final ObjectOutputStream oos) throws IOException {

        oos.writeObject(lookupTableClassifier);
        oos.close();
    }

    protected void readModel(final String fileName) throws IOException, ClassNotFoundException {

        ObjectInput input = null;
        try (InputStream file = new FileInputStream(fileName + ".ser")) {
            InputStream buffer = new BufferedInputStream(file);
            input = new ObjectInputStream(buffer);
            lookupTableClassifier = (LookupTableClassifier) input.readObject();
        }
        finally {
            closeStream(input);
        }
    }

    private void closeStream(final ObjectInput stream) throws IOException {

        if (stream != null) {
            stream.close();
        }
    }

    @Override
    public void getModelFromDefaultLocation() {

        try {
            readModel(defaultModelLocation);
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultModelLocation == null) ? 0 : defaultModelLocation.hashCode());
        result = prime * result + ((lookupTableClassifier == null) ? 0 : lookupTableClassifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        NGramClassifier other = (NGramClassifier) obj;
        if (defaultModelLocation == null) {
            if (other.defaultModelLocation != null) { return false; }
        }
        else if (!defaultModelLocation.equals(other.defaultModelLocation)) { return false; }
        if (lookupTableClassifier == null) {
            if (other.lookupTableClassifier != null) { return false; }
        }
        else if (!lookupTableClassifier.equals(other.lookupTableClassifier)) { return false; }
        return true;
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        // TODO Auto-generated method stub
        return null;
    }

}
