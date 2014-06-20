package uk.ac.standrews.cs.digitising_scotland.parser.classifiers.lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.Pair;
import cc.mallet.classify.Classification;

/**
 * Uses a lookup table to return matches as classifications.
 * @author frjd2, jkc25
 *
 */
public class ExactMatchClassifier extends AbstractClassifier {

    private Map<TokenSet, CodeTriple> lookupTable;
    private String modelFileName = "target/lookupTable";

    /**
     * Creates a new {@link ExactMatchClassifier} and creates an empty lookup table.
     */
    public ExactMatchClassifier() {

        this.lookupTable = new HashMap<>();

    }

    /**
     * Creates a new {@link ExactMatchClassifier} and creates and fills the lookup table with the contents of the bucket.
     * Equivalent to call @train().
     * @param bucket Bucket containing records to put in the lookup table.
     * @throws IOException IO error if location to write model cannot be accessed
     */
    public ExactMatchClassifier(final Bucket bucket) throws IOException {

        this();
        fillLookupTable(bucket);
        writeModel(modelFileName);

    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#train(uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket)
     */
    @Override
    public void train(final Bucket bucket) throws Exception {

        fillLookupTable(bucket);
        writeModel(modelFileName);

    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record)
     */
    @Override
    public Record classify(final Record record) throws IOException {

        TokenSet cleanDescriptionTokenSet = new TokenSet(record.getCleanedDescription());
        CodeTriple result = lookupTable.get(cleanDescriptionTokenSet);
        if (result == null) {
            return record;
        }
        else {
            record.addCodeTriples(result);
            return record;

        }

    }

    /**
     * Classifies all the records in a {@link Bucket}.
     *
     * @param bucket Bucket to classify
     * @return the bucket with classified records.
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws IOException {

        Bucket classifiedBucket = new Bucket();
        for (Record record : bucket) {
            classifiedBucket.addRecordToBucket(classify(record));
        }

        return classifiedBucket;
    }

    /**
     * Adds each {@link Classification} in the records {@link ClassificationSet} to the lookupTable.
     * @param record to add
     */
    private void addRecordToLookupTable(final Record record) {

        for (CodeTriple codeTriple : record.getOriginalData().getGoldStandardCodeTriples()) {
            lookupTable.put(codeTriple.getTokenSet(), codeTriple);
        }

    }

    private void fillLookupTable(final Bucket bucket) {

        for (Record record : bucket) {
            addRecordToLookupTable(record);
        }
    }

    /**
     * Writes model to file. File name is fileName.ser
     *
     * @param fileName name of file to write model to
     * @throws IOException if model location cannot be read
     * */
    public void writeModel(final String fileName) throws IOException {

        FileOutputStream fos = new FileOutputStream(fileName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        write(oos);
    }

    protected void readModel(final String fileName) throws ClassNotFoundException, IOException {

        //deserialize the .ser file
        InputStream file = new FileInputStream(fileName + ".ser");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        try {

            Map<TokenSet, CodeTriple> recoveredMap = (Map<TokenSet, CodeTriple>) input.readObject();
            lookupTable = recoveredMap;
        }
        finally {
            closeStrams(file, input);

        }
    }

    private void closeStrams(final InputStream file, final ObjectInput input) throws IOException {

        if (input != null) {
            input.close();
            file.close();
        }
    }

    private void write(final ObjectOutputStream oos) throws IOException {

        oos.writeObject(lookupTable);
        oos.close();
    }

    @Override
    public void getModelFromDefaultLocation() {

        try {
            readModel(modelFileName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((lookupTable == null) ? 0 : lookupTable.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        ExactMatchClassifier other = (ExactMatchClassifier) obj;
        if (lookupTable == null) {
            if (other.lookupTable != null) { return false; }
        }
        else if (!lookupTable.equals(other.lookupTable)) { return false; }
        return true;
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        CodeTriple result = lookupTable.get(tokenSet);
        if (result != null) {
            return new Pair<Code, Double>(result.getCode(), result.getConfidence());

        }
        else {
            return null;
        }
    }
}
