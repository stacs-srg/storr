package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by al on 23/05/2014.
 */
public interface IBucketIndexOLD {

    Set<String> keySet() throws IOException;

    ILXPInputStreamTypedOld records(String value) throws IOException;

    List<Integer> values(String value) throws IOException;

    void add(ILXP record) throws IOException;
}
