package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import java.io.IOException;

/**
 * Created by al on 23/05/2014.
 */
public interface IIndex {

    public ILXPInputStream get_records( String value ) throws IOException;

    public void add( ILXP record ) throws IOException;


}
