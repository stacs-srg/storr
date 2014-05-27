package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by al on 23/05/2014.
 */
public interface IIndex {

    Set<String> keySet() throws IOException;

    public ILXPInputStream records( String value ) throws IOException;

    public List<Integer> values( String value ) throws IOException;

    public void add( ILXP record ) throws IOException;


}
