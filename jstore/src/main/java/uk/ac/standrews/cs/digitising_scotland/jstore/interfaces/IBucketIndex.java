package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * This class implements a index over indexed buckets.
 * Instances of this class are returned by the @methods getIndex of the classes implementing the  @interface IIndexedBucket
 * Created by al on 23/05/2014.
 */
public interface IBucketIndex<T extends ILXP> {

    /**
     * @return the key set of this index
     * @throws IOException
     */
    Set<String> keySet() throws IOException;

    /**
     * @param value - the value to ge extracted from the index (e.g. "al" from a name labelled field)
     * @return a stream of records which (exactly) contain the specified value.
     * @throws IOException
     */
    IInputStream<T> records(String value) throws IOException;

    /**
     * @param value
     * @return the list of record ids that (exactly) contain the specified value e.g. "al" from a set of names
     * @throws IOException
     */
    List<Integer> values(String value) throws IOException;

    /**
     * Adds a record to the index.
     *
     * @param record
     * @throws IOException
     */
    void add(T record) throws IOException;
}
