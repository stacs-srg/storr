package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Uses a lookup table to return matches as classifications.
 * @author frjd2, jkc25
 *
 */
public class ExactMatchClassifier extends AbstractClassifier {

    private Map<TokenSet, Set<Classification>> lookupTable;
    private String modelFileName = "target/lookupTable";

    /**
     * Can be used to overwrite the default file path for model writing.
     * @param modelFileName new model path
     */
    public void setModelFileName(final String modelFileName) {

        this.modelFileName = modelFileName;
    }

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

        Set<Classification> result = lookupTable.get(new TokenSet(record.getDescription()));
        if (result == null) {
            return record;
        }
        else {
            record.addAllCodeTriples(result);
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
     * Adds each gold standard {@link Classification} in the records to the lookupTable.
     * @param record to add
     */
    private void addRecordToLookupTable(final Record record) {

        final Set<Classification> goldStandardCodes = record.getOriginalData().getGoldStandardCodeTriples();
        for (Classification t : goldStandardCodes) {
            final TokenSet description = new TokenSet(t.getTokenSet());
            Set<Classification> st = new HashSet<Classification>();
            st.add(t);
            lookupTable.put(description, st);
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

    protected AbstractClassifier readModel(final String fileName) throws ClassNotFoundException, IOException {

        //deserialize the .ser file
        InputStream file = new FileInputStream(fileName + ".ser");
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);
        try {

            Map<TokenSet, Set<Classification>> recoveredMap = (Map<TokenSet, Set<Classification>>) input.readObject();
            lookupTable = recoveredMap;

        }
        finally {
            closeStreams(file, input);

        }
        return this;
    }

    private void closeStreams(final InputStream file, final ObjectInput input) throws IOException {

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
    public AbstractClassifier getModelFromDefaultLocation() {

        AbstractClassifier classifier = null;
        try {
            classifier = readModel(modelFileName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return classifier;
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

    /**
     * Classifies a {@link TokenSet} to a set of {@link Classification}s using the classifiers lookup table.
     * @param tokenSet to classify
     * @return Set<CodeTripe> code triples from lookup table
     * @throws IOException Indicates an I/O error
     */
    public Set<Classification> classifyTokenSetToCodeTripleSet(final TokenSet tokenSet) throws IOException {

        Set<Classification> result = lookupTable.get(tokenSet);

        if (result != null) {
            result = setConfidenceLevels(result, 2.0);
            return result;

        }
        else {
            return null;
        }
    }

    private Set<Classification> setConfidenceLevels(final Set<Classification> result, final double i) {

        Set<Classification> newResults = new HashSet<Classification>();
        for (Classification codeTriple : result) {
            Classification newCodeT = new Classification(codeTriple.getCode(), codeTriple.getTokenSet(), i);
            newResults.add(newCodeT);
        }
        return newResults;
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        Set<Classification> result = lookupTable.get(tokenSet);

        if (result != null) {
            Classification current = result.iterator().next();
            return new Pair<Code, Double>(current.getCode(), current.getConfidence());

        }
        else {
            return null;
        }
    }
}
