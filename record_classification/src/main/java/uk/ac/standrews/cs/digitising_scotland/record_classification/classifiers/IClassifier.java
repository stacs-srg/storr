package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * IClassifier went missing, replaced it.
 * Created by fraserdunlop on 06/10/2014 at 09:30.
 */
public  interface IClassifier {
    public abstract Pair<Code, Double> classify(TokenSet tokenSet);
}
