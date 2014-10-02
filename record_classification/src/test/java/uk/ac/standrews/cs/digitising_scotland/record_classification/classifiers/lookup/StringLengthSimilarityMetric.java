package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

/**
 * Basic string similarity based on string
* Created by fraserdunlop on 02/10/2014 at 11:21.
*/
class StringLengthSimilarityMetric implements SimilarityMetric<String> {

    @Override
    public double getSimilarity(String o1, String o2) {
        return 1/(1+ getLengthDiff(o1,o2));
    }

    protected int getLengthDiff(String o1, String o2) {
        return Math.abs(o1.length()-o2.length());
    }
}
