package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * A Factory interface that permits typed views over LXPs
 * Instances of this factory may be passed into Buckets to provide a method of typing the buckets and also
 * constructing instances of some typed interface which may be passed to, for example, typed streams of objects.
 * Created by al on 22/08/2014.
 */
public interface ILXPFactory<T extends ILXP> {
    /*
     * create an instance of a <T extends ILXP> from the reader
     */
    T create(long persistent_object_id, JSONReader reader, IRepository repository, IBucket bucket) throws PersistentObjectException;

    /*
     * return the label id required by type T
     */
    long getTypeLabel();
}
