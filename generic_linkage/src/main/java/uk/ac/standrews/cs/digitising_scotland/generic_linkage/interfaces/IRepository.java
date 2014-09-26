package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.RepositoryException;

import java.util.Iterator;

/**
 * Created by al on 11/05/2014.
 */
public interface IRepository {

    <T extends ILXP> IBucketTypedOLD<T> makeBucket(String name,ILXPFactory<T> tFactory) throws RepositoryException;

    <T extends ILXP> IBucketTypedOLD<T> makeIndirectBucket(String name,ILXPFactory<T> tFactory) throws RepositoryException;

    <T extends ILXP> IIndexedBucketTypedOLD<T> makeIndexedBucket(String name,ILXPFactory<T> tFactory) throws RepositoryException;   // todo type parameterise

    boolean bucketExists(String name);

    void deleteBucket(String name) throws RepositoryException;

    <T extends ILXP> IBucketTypedOLD<T> getBucket(final String name, ILXPFactory<T> tFactory) throws RepositoryException;

    IIndexedBucketTypedOLD getIndexedBucket(String name) throws RepositoryException;

    Iterator<IBucketTypedOLD> getIterator();

}
