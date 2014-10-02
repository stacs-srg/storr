package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * Created by fraserdunlop on 01/10/2014 at 15:31.
 */
public class ClosestMatchMapTest {


    @Test
    public void test(){
        Map<TokenSet, Set<Classification>> map = new HashMap<>();
        ClosestMatchMap<TokenSet, Set<Classification>> classifier = new ClosestMatchMap<>(new LevenshteinTokenSetSimilarityMetric(), map);
    }


}
