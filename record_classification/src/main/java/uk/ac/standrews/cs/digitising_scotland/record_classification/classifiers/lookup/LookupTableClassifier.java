package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import java.io.Serializable;
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
public class LookupTableClassifier extends AbstractClassifier implements Serializable {

    private static final long serialVersionUID = 4756842760803378314L;
    private final Map<TokenSet, Code> lookupTable;

    /**
     * Creates a new {@link LookupTableClassifier} and creates an empty lookup table.
     */
    public LookupTableClassifier() {

        this.lookupTable = new HashMap<>();
    }

    /**
     * Creates a new {@link LookupTableClassifier} and creates and fills the lookup table with the contents of the bucket.
     * Equivalent to call @train().
     * @param bucket Bucket containing records to put in the lookup table.
     */
    public LookupTableClassifier(final Bucket bucket) {

        this();
        fillLookupTable(bucket);
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.parser.classifiers.AbstractClassifier#train(uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket)
     */
    @Override
    public void train(final Bucket bucket) throws Exception {

        fillLookupTable(bucket);
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) {

        throw new UnsupportedOperationException();
    }

    /**
     * Adds each {@link TokenSet} in the records {@link Classification} to the lookupTable.
     * @param record to add
     */
    protected void addRecordToLookupTable(final Record record) {

        for (Classification codeTriple : record.getOriginalData().getGoldStandardClassifications()) {
            lookupTable.put(codeTriple.getTokenSet(), codeTriple.getCode());
        }
    }

    /**
     * Classifies and {@link NGramSubstrings} object and return a set of {@link Classification}.
     *
     * @param grams the grams
     * @param record the record that the grams came from
     * @return the classification set
     */
    protected Set<Classification> classify(final NGramSubstrings grams, final Record record) {

        Set<Classification> classificationSet = new HashSet<>();
        for (TokenSet gram : grams) {
            Classification classifiedGram = classifyIndividualGram(new TokenSet(gram));
            if (classifiedGram != null) {
                classificationSet.add(classifiedGram);
            }
        }
        return classificationSet;
    }

    private void fillLookupTable(final Bucket bucket) {

        for (Record record : bucket) {
            addRecordToLookupTable(record);
        }
    }

    /**
     * Classifies an individual gram (String).
     *
     * @param gram the gram to classify
     * @return the classification set containing the results of the classification
     */
    private Classification classifyIndividualGram(final TokenSet gram) {

        Classification codeTriple = new Classification(lookupTable.get(gram), gram, 1.0);

        return codeTriple;
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
        LookupTableClassifier other = (LookupTableClassifier) obj;
        if (lookupTable == null) {
            if (other.lookupTable != null) { return false; }
        }
        else if (!lookupTable.equals(other.lookupTable)) { return false; }
        return true;
    }

}
