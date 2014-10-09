package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * Adapter for Converting Simmetrics string similarity metrics to conform
 * to our SimilarityMetric interface for use in ClosestMatchMaps
 * Created by fraserdunlop on 02/10/2014 at 16:50.
 */
public class SimilarityMetricFromSimmetricFactory {

    public SimilarityMetric<String> create(AbstractStringMetric stringMetric) {

        return new StringMetric(stringMetric);
    }

    private class StringMetric implements SimilarityMetric<String> {

        private final AbstractStringMetric stringMetric;

        @Override
        public double getSimilarity(String o1, String o2) {

            return stringMetric.getSimilarity(o1, o2);
        }

        public StringMetric(AbstractStringMetric stringMetric) {

            this.stringMetric = stringMetric;
        }

    }

}
