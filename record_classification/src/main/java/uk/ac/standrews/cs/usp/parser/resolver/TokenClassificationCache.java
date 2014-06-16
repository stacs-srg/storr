package uk.ac.standrews.cs.usp.parser.resolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import uk.ac.standrews.cs.usp.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;

public class TokenClassificationCache {

    private Map<TokenSet, Pair<Code, Double>> classifications;
    private AbstractClassifier classifier;

    public TokenClassificationCache(final AbstractClassifier classifier) {

        classifications = new HashMap<>();
        this.classifier = classifier;
    }

    private void addTokenSet(final TokenSet tokenSet) throws IOException {

        if (classifications.get(tokenSet) == null) {
            Pair<Code, Double> classification = classifier.classify(tokenSet);
            classifications.put(tokenSet, classification);
        }
    }

    public Pair<Code, Double> getClassification(final TokenSet tokenSet) throws IOException {

        addTokenSet(tokenSet);
        return classifications.get(tokenSet);
    }
}
