package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import java.io.IOException;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Deterministic mock classifier object. Used for testing.
 * Classify(tokenSet) will return the first code in the map with a confidence of 0.8 if
 * the tokenset has an even number of characters.
 * If odd the 2nd code in the codemap and confidence of 0.4.

 * @author jkc25
 *
 */
public class MockClassifier extends AbstractClassifier {

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#train(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket)
     */
    @Override
    public void train(final Bucket bucket) throws Exception {

        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record)
     */
    @Override
    public Record classify(final Record record) throws IOException {

        return record;
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet)
     */
    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        CodeDictionary codeDictionary = new CodeDictionary(null);
        if (tokenSet.size() % 2 == 0) {
            Pair<Code, Double> testvalue = null;
            try {
                testvalue = new Pair<>(codeDictionary.getCode("2100"), 0.8);
            }
            catch (CodeNotValidException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return testvalue;
        }
        else {

            Pair<Code, Double> testvalue = null;
            try {
                testvalue = new Pair<>(codeDictionary.getCode("2200"), 0.4);
            }
            catch (CodeNotValidException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return testvalue;
        }
    }

    /* (non-Javadoc)
     * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#getModelFromDefaultLocation()
     */
    @Override
    public AbstractClassifier getModelFromDefaultLocation() {

        // TODO Auto-generated method stub
        return this;
    }

}
