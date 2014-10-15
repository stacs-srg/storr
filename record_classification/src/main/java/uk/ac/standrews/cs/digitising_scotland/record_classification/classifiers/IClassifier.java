package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers;

//TODO document!
public interface IClassifier<K, V> {

    public abstract V classify(K k) throws Exception;

}
