package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.closestmatchmap;

import java.util.Comparator;

/**
 * Similaritor is a factory which creates Comparators
 * which compare the similarity of objects to a reference object k.
 * If the metric supplied at construction larger values for more similar
 * objects then the comparator returned will sort a list so that the most
 * similar object is at the head of the list.
 * Created by fraserdunlop on 01/10/2014 at 16:23.
 */
public class Similaritor<K> {

    private final SimilarityMetric<K> metric;

    public Similaritor(SimilarityMetric<K> metric) {

        this.metric = metric;
    }

    public double getSimilarity(K o1, K o2) {

        return metric.getSimilarity(o1, o2);
    }

    /**
     * @param k reference object.
     * @return A Comparator for objects of type K.
     */
    public Comparator<K> getComparator(final K k) {

        return new Comparator<K>() {

            @Override
            public int compare(K o1, K o2) {

                double o1Score = metric.getSimilarity(o1, k);
                double o2Score = metric.getSimilarity(o2, k);
                if (o1Score == o2Score) return 0;
                if (o1Score > o2Score) return -1;
                return 1;
            }
        };
    }
}
