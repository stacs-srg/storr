package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * TODO
 * Created by fraserdunlop on 01/10/2014 at 15:42.
 */
public class LevenshteinTokenSetSimilarity implements SimilarityMetric<TokenSet>{


    @Override
    public double getSimilarity(TokenSet o1, TokenSet o2) {
        return 0;
    }
}
