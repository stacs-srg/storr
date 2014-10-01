package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup;

/**
 * Similarity metric interface.
 * Created by fraserdunlop on 01/10/2014 at 17:04.
 */
public interface SimilarityMetric<K> {

    /**
     * Gets the similarity.
     * @param o1 object 1.
     * @param o2 object 2.
     * @return some measure of similarity between objects o1 and o2.
     */
    public abstract double getSimilarity(K o1, K o2);
}
