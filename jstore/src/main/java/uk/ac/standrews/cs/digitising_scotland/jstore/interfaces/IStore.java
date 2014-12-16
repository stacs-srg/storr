package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;

import java.util.Iterator;

/**
 * This interface is used to encode the type of a Store within the system.
 * The Stores are implemented as a Hierarchy in which there is one instance of the Store (per node).
 * Each Store can contain a multiplicity of Repositories (implementing @class IRepository).
 * Each repository can contain buckets (implementing at least @class IBucket).
 * Buckets contain LXP (labelled cross product) records.
 * Repositories (Repos) and buckets may be deleted.
 * The reason this model was adopted was to be permit buckets to be clustered together into local groups.
 * For example, buckets of blocked records may be clustered together into a single repo.
 * Another example is the types repo which contains 2 different buckets used to represent different aspects of the type system.
 * <p/>
 * Created by al on 06/06/2014.
 */
public interface IStore {

    /**
     * @param name - the name of the repository to be created
     * @return a new repository with the given name
     * @throws RepositoryException - if the repo exists already of if something goes wrong.
     */
    IRepository makeRepository(String name) throws RepositoryException;

    /**
     * @param name - the repo that is the subject of the enquiry.
     * @return true if a repository with the given name exists in the store.
     */
    boolean repoExists(String name);

    /**
     * This method deletes the specified repository
     *
     * @param name - the name of the repo to be deleted.
     * @throws RepositoryException - if the repo does not exist or something goes wrong
     */
    void deleteRepo(String name) throws RepositoryException;

    /**
     * @param name - the name of the repo being looked up
     * @return the repo with the given name, if it exists.
     * @throws RepositoryException if the repo does not exist or if something goes wrong.
     */
    IRepository getRepo(String name) throws RepositoryException;

    /**
     * @return the repositories contained in the store
     */
    Iterator<IRepository> getIterator();

    /**
     * @return the next free identifier in the store
     */
    long getNextFreePID();

    /**
     * @return the cache of loaded objects (in memory) in the system
     */
    IObjectCache getObjectCache();
}
