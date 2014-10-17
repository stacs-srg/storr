package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces;

import java.io.IOException;

import com.google.common.collect.Multiset;

/**
 *
 * Created by fraserdunlop on 09/10/2014 at 09:55.
 */
public interface SubsetEnumerator<A> {

    Multiset<A> enumerate(final A a) throws IOException;
}
