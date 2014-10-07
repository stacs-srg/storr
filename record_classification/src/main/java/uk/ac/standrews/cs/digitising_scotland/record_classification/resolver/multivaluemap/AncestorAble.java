package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

/**
 * Implementing class should have a well ancestral hierarchy
 * over its instances. T should be set to the implementing class
 * by the implementing class. isAncestor may then be implemented
 * such that it forms a boolean operator between two T objects.
 * i.e. A.isAncestor(B) returns true if and only if A is an ancestor
 * of B.
 * Created by fraserdunlop on 06/10/2014 at 12:44.
 */
public interface AncestorAble<T> {
    public boolean isAncestor(T t);
}
