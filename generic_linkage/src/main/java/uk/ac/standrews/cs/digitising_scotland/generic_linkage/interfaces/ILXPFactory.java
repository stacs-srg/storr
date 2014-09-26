package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

/**
 * Created by al on 22/08/2014.
 */
public interface ILXPFactory<T extends ILXP> {
    T create(int id, JSONReader reader) throws PersistentObjectException;
}
