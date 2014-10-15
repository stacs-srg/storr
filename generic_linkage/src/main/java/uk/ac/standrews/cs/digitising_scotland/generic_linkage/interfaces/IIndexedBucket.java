package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;

/**
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucket<T extends ILXP> extends IBucket<T> {

    void addIndex(String label) throws IOException;

    IBucketIndex<T> getIndex(String label);
}
