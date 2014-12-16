package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import java.io.IOException;

/**
 * Augments the functionality of a bucket with indexes.
 * Indexes are double indexes: label first and then key value associated with that label.
 * Thus if you have a bucket containing [name: string, age: int] tuples you can create an index
 * over the labels - say name and then getString the index of all people called "al".
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucket<T extends ILXP> extends IBucket<T> {

    /**
     * @param label - the label to add - for example "name" will add an index of names over records such as [name: string, age: int]
     * @throws IOException
     */
    void addIndex(String label) throws IOException;

    /**
     * @param label - the label over which you wish to acquire the index
     * @return the index associated with the label or null if there isn't one.
     */
    IBucketIndex<T> getIndex(String label);
}
