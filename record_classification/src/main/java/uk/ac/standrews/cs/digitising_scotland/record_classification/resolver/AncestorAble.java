package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 12:44.
 */
public interface AncestorAble<T> {
    public boolean isAncestor(T t);
}
