package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by al on 23/05/2014.
 */
public interface IBucketIndex<T extends ILXP> {

    Set<String> keySet() throws IOException;

    IInputStream<T> records(String value) throws IOException;

    List<Integer> values(String value) throws IOException;

    void add(T record) throws IOException;
}
