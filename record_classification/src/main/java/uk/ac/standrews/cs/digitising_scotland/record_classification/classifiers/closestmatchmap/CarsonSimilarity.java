package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
        Map<String, Double> tokenScore = new TreeMap<>();

        for (String string1 : ts1) {
            double highestScore = 0;
            for (String string2 : ts2) {
                double currentScore = metric.getSimilarity(string1, string2);
                // System.out.println("comparing " + s1 + " to " + s2 + ". Currecnt score is " + currentScore);
                if (currentScore >= highestScore) {
                    highestScore = currentScore;
                }
            }
            tokenScore.put(string1, highestScore);
        }
        double total = 0;
        double nonZero = 0;
        for (Map.Entry<String, Double> entry : tokenScore.entrySet()) {
            final Double score = entry.getValue();
            if (score != 0) {
                total += score;
                nonZero++;
            }
        }

        if (nonZero != 0) { return (float) (total / nonZero); }

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

    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {

        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {

                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }
        });
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

}
