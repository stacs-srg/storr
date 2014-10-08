package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;

import java.io.IOException;

/**
 * Resolves hierarchies in the keys of a MultiValueMap. Moves ancestor key contents
 * into decendent key lists. Keys must implement Ancestorable<K> interface.
 * Created by fraserdunlop on 08/10/2014 at 15:07.
 */
public interface IHierarchyResolver<K extends AncestorAble<K>, V> {
    /**
     * Moves ancestor key contents to decendent key lists.
     * @param map MultiValueMap
     * @return new MultiValueMap with hierarchies in keys resolved
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public MultiValueMap<K, V> moveAncestorsToDescendantKeys(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException;
}
