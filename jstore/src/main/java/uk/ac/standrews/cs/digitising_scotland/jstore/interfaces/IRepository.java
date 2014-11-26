package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.RepositoryException;

import java.util.Iterator;

/**
 * Created by al on 11/05/2014.
 */
public interface IRepository {

    IBucket makeBucket(String name, BucketKind kind) throws RepositoryException;

    <T extends ILXP> IBucket<T> makeBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException;

    boolean bucketExists(String name);

    void deleteBucket(String name) throws RepositoryException;

    IBucket getBucket(final String name) throws RepositoryException;

    <T extends ILXP> IBucket<T> getBucket(final String name, ILXPFactory<T> tFactory) throws RepositoryException;

    Iterator<String> getBucketNameIterator();

    <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory);

    String getRepo_path(); // return the repo path of this repository (not intended for public use).

    String getName();

}
