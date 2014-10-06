package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 16:35.
 */
public interface ValidityAssessor<C, T> {
    public boolean assess(C c, T t);
}