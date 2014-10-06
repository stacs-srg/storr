package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public interface IClassifier {

    Pair<Code, Double> classify(TokenSet tokenSet);

}
