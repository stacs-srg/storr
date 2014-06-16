package uk.ac.standrews.cs.usp.parser.classifiers;

import java.io.IOException;

import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.usp.parser.resolver.Pair;

/**
 * Deterministic mock classifier object. Used for testing.
 * Classify(tokenSet) will return the first code in the map with a confidence of 0.8 if
 * the tokenset has an even number of characters.
 * If odd the 2nd code in the codemap and confidence of 0.4.

 * @author jkc25
 *
 */
public class MockClassifier extends AbstractClassifier {

    @Override
    public void train(final Bucket bucket) throws Exception {

        // TODO Auto-generated method stub

    }

    @Override
    public Record classify(final Record record) throws IOException {

        return record;
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) throws IOException {

        if ((tokenSet.size() % 2) == 0) {
            Pair<Code, Double> testvalue;
            testvalue = new Pair<>(CodeFactory.getInstance().getCode(0), 0.8);
            return testvalue;
        }
        else {

            Pair<Code, Double> testvalue;
            testvalue = new Pair<>(CodeFactory.getInstance().getCode(1), 0.4);
            return testvalue;
        }
    }

    @Override
    public void getModelFromDefaultLocation() {

        // TODO Auto-generated method stub

    }

}
