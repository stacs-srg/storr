package uk.ac.standrews.cs.digitising_scotland.record_classification.legacy;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public class CarsonSimilarity<K> extends AbstractStringMetric {

    @Override
    public String getLongDescriptionString() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getShortDescriptionString() {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getSimilarity(String s1, String s2) {

        TokenSet ts1 = new TokenSet(s1);
        TokenSet ts2 = new TokenSet(s2);

        AbstractStringMetric metric = new Levenshtein();
        Map<String, Double> tokenScore = new TreeMap<>(new ValueComparator());

        for (String string1 : ts1) {
            double highestScore = 0;
            for (String string2 : ts2) {
                double currentScore = metric.getSimilarity(string1, string2);
                System.out.println("comparing " + s1 + " to " + s2 + ". Currecnt score is " + currentScore);
                if (currentScore >= highestScore) {
                    highestScore = currentScore;
                }
            }
            tokenScore.put(string1, highestScore);
        }

        for (Map.Entry<String, Double> entry : tokenScore.entrySet()) {
            System.out.println(entry.getValue());
        }
        return 0;
    }

    @Override
    public String getSimilarityExplained(String arg0, String arg1) {

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getSimilarityTimingEstimated(String arg0, String arg1) {

        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getUnNormalisedSimilarity(String arg0, String arg1) {

        // TODO Auto-generated method stub
        return 0;
    }

}

class ValueComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        Entry<String, Double> entry1 = (Entry<String, Double>) o1;
        Entry<String, Double> entry2 = (Entry<String, Double>) o2;
        return entry1.getValue().compareTo(entry2.getValue());
    }
}
