package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;
import java.io.IOException;

/**
 *
 * Created by fraserdunlop on 08/10/2014 at 13:15.
 */
public interface IBelowThresholdRemover<K, V extends Comparable<Threshold>, Threshold> {
    public MultiValueMap<K, V> removeBelowThreshold(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException;
}
