package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;

import java.util.Iterator;

/**
 * Classes implementing this interface is used to represent repositories.
 * See further comments in @class IStore
 * <p/>
 * Created by al on 11/05/2014.
 */
public interface IRepository {

    /**
     * This method creates a new bucket
     *
     * @param name - the name of the bucket to be created.
     * @param kind - the implementation kind of the bucket - see @class BucketKind
     * @return the newly created repository
     * @throws RepositoryException if a bucket with the name previously exists or if something goes wrong.
     */
    IBucket makeBucket(String name, BucketKind kind) throws RepositoryException;

    /**
     * This method creates a new bucket that is constrained to contain LXP records compatible with T.
     *
     * @param name     - the name of the bucket to be created.
     * @param kind     - the implementation kind of the bucket - see @class BucketKind
     * @param tFactory - a factory capable of creating instances of type @class T
     * @return the newly created repository
     * @throws RepositoryException if a bucket with the name previously exists or if something goes wrong.
     */
    <T extends ILXP> IBucket<T> makeBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException;

    /**
     * @param name - the bucket that is the subject of the enquiry.
     * @return true if a bucket with the given name exists in the repo.
     */
    boolean bucketExists(String name);

    /**
     * This method deletes the specified bucket
     *
     * @param name - the name of the bucket to be deleted.
     * @throws RepositoryException - if the bucket does not exist or something goes wrong
     */
    void deleteBucket(String name) throws RepositoryException;

    /**
     * @param name - the name of the bucket being looked up
     * @return the bucket with the given name, if it exists.
     * @throws RepositoryException if the repo does not exist or if something goes wrong.
     */
    IBucket getBucket(final String name) throws RepositoryException;

    /**
     * @param name     - the name of the bucket being looked up
     * @param tFactory - a factory capable of specifying instances of type @class T
     * @return the bucket with the given name, if it exists and is type compatibile
     * @throws RepositoryException if the repo does not exist or if something goes wrong.
     */
    <T extends ILXP> IBucket<T> getBucket(final String name, ILXPFactory<T> tFactory) throws RepositoryException;

    /**
     * @return the names of all the buckets in the repo
     * Note this returns strings and not buckets since they may be of different types
     * (i.e. either constrained by type (homogeneous) or not (heterogeneous).
     */
    Iterator<String> getBucketNameIterator();

    /**
     * Returns an iterator over those buckets that are constrained by T.
     *
     * @param tFactory - specifies the type constraining the bucket.
     * @param <T>      - the type of the required bucket content types.
     * @return an iterator over the appropriate buckets.
     */
    <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory);

    /**
     * @return the path to the repo - this is only used with in the bucket implementation.
     */
    String getRepo_path(); // return the repo path of this repository (not intended for public use).

    /**
     * @return the name of the repository
     */
    String getName();

}
