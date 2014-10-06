package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.closestmatchmap;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public class StringSimilarityClassifier implements IClassifier {

    ClosestMatchMap<String, Pair<Code, Double>> map;

    public StringSimilarityClassifier(final ClosestMatchMap<String, Pair<Code, Double>> closestMatchMap) {

        map = closestMatchMap;
    }

    @Override
    public Pair<Code, Double> classify(final TokenSet tokenSet) {

        final Pair<Code, Double> pair = map.get(tokenSet.toString());
        if (pair == null) {
            String s = map.getClosestKey(tokenSet.toString());
            double similarity = map.getSimilarity(tokenSet.toString(), s);
            return new Pair<>(map.getClosestMatch(s).getLeft(), similarity);
        }
        else {
            return new Pair<>(pair.getLeft(), 1.0);

        }
    }
}
