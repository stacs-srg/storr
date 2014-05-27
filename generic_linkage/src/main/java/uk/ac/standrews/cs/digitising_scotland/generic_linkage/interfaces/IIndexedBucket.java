package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;

/**
 * Created by al on 27/05/2014.
 */
public interface IIndexedBucket extends IBucket {

    void add_index(String label) throws IOException;

    IIndex get_index(String label);

}
