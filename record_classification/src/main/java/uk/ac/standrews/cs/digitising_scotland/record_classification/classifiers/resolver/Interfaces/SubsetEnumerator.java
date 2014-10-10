package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces;

import com.google.common.collect.Multiset;
import java.io.IOException;

/**
 *
 * Created by fraserdunlop on 09/10/2014 at 09:55.
 */
public interface SubsetEnumerator<A> {
    public Multiset<A> enumerate(final A a) throws IOException;
}
