package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;

/**
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucketLXP extends IBucketLXP {

    void addIndex(String label) throws IOException;

    IBucketIndexLXP getIndex(String label);
}
