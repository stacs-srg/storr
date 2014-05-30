package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;

/**
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucket extends IBucket {

    void addIndex(String label) throws IOException;

    IIndex getIndex(String label);
}
