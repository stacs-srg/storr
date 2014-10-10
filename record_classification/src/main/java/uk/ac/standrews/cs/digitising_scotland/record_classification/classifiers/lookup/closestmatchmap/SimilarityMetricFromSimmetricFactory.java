package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.closestmatchmap;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;

/**
 * Adapter for Converting Simmetrics string similarity metrics to conform
 * to our SimilarityMetric interface for use in ClosestMatchMaps
 * Created by fraserdunlop on 02/10/2014 at 16:50.
 */
public class SimilarityMetricFromSimmetricFactory {

    public SimilarityMetric<String> create(final AbstractStringMetric stringMetric) {

        return new StringMetric(stringMetric);
    }

    private class StringMetric implements SimilarityMetric<String> {

        private final AbstractStringMetric stringMetric;

        @Override
        public double getSimilarity(final String o1, final String o2) {

            Float sim = stringMetric.getSimilarity(o1, o2);
            if (sim.isNaN()) {
                System.out.println("NaN returned by similarity metric :- o1: " + o1 + " o2: " + o2);
                return 0;
            }
            return sim;
        }

        public StringMetric(final AbstractStringMetric stringMetric) {

            this.stringMetric = stringMetric;
        }

    }

}
