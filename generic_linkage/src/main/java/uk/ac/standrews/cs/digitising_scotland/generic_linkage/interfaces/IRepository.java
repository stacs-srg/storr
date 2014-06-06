package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;

import java.util.Iterator;

/**
 * Created by al on 11/05/2014.
 */
public interface IRepository {

    IBucket makeBucket(String name) throws RepositoryException;

    IIndexedBucket makeIndexedBucket(String name) throws RepositoryException;

    boolean bucketExists(String name);

    void deleteBucket(String name) throws RepositoryException;

    IBucket getBucket(String name) throws RepositoryException;

    IIndexedBucket getIndexedBucket(String name) throws RepositoryException;

    Iterator<IBucket> getIterator();

}
