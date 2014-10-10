package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.IOException;

/**
 * Created by al on 22/08/2014.
 */
public interface ILXPFactory<T extends ILXP> {
    /*
     * create an instance of a <T extends ILXP> from the reader
     */
    T create(int label_id, JSONReader reader) throws PersistentObjectException;

    /*
     * create an instance of a <T extends ILXP> from an ILXP
     */
    T convert( ILXP base );

    /*
     * return true if the factory can create and instance with the specified label and false otherwise
     */
    boolean checkConsistentWith( int label_id ) throws IOException, PersistentObjectException;
}
