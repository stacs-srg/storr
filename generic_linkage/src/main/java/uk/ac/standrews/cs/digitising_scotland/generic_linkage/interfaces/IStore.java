package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;

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
}
