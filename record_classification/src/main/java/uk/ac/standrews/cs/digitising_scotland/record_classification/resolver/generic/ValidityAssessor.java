package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

/**
 * Assesses the validity of c given criterion t.
 * Created by fraserdunlop on 06/10/2014 at 16:35.
 */
public interface ValidityAssessor<C, T> {
    /**
     * Assesses the validity of c given criterion t.
     * @param c object whose validity is to be assessed.
     * @param t criterion for validity assessment.
     * @return boolean, true if valid, false if not.
     */
    public boolean assess(C c, T t);
}