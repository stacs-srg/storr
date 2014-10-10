package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public class StringSimilarityClassifier implements IClassifier<TokenSet, Classification> {

    ClosestMatchMap<String, Classification> map;

    public StringSimilarityClassifier(final ClosestMatchMap<String, Classification> closestMatchMap) {

        map = closestMatchMap;
    }

    @Override
    public Classification classify(final TokenSet tokenSet) {

        final Classification classification = map.get(tokenSet.toString());
        if (classification == null) {
            String s = map.getClosestKey(tokenSet.toString());
            double similarity = map.getSimilarity(tokenSet.toString(), s);
            Classification mostSimilar = map.getClosestMatch(s);
            return new Classification(mostSimilar.getCode(), mostSimilar.getTokenSet(), similarity);
        }
        return classification;
    }
}
