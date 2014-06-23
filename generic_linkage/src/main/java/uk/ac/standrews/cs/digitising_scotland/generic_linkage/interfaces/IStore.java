package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by al on 06/06/2014.
 */
public interface IStore {

    IRepository makeRepository(String name) throws RepositoryException;

    boolean repoExists(String name);

    void deleteRepo(String name) throws RepositoryException;

    IRepository getRepo(String name) throws RepositoryException;

    Iterator<IRepository> getIterator();

    int getNextFreePID();

    /**
     * @param id - the identifier of the LXP record for which a reader is required.
     * @return an LXP record with the specified id, or null if the record cannot be found
     * Mirrors bucket operation of the same name
     */
    ILXP get(int id) throws IOException, PersistentObjectException;
}
