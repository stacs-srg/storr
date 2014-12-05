package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;

import java.io.IOException;

/**
 * The interface for a Bucket (a repository of LXP records).
 * Each record in the repository is identified by id.
 */
public interface IBucket<T extends ILXP> {

    /**
     * Gets the LXP record with the specified id
     * @param id - the identifier of the LXP record for which a reader is required.
     * @return an LXP record with the specified id, or null if the record cannot be found
     * @throws BucketException if the record cannot be found or if something goes wrong.
     */
    T get(int id) throws BucketException;

    /**
     * Writes the state of a record to a bucket
     * The id of the record is used to determine its name in the bucket.
     * @param record whose state is to be written
     */
    void put(T record) throws BucketException;

    /**
     * @param id
     * @return the filepath corresponding to record with identifier id in this bucket (more public than it should be).
     */
    String filePath(int id);

    /**
     * @return an input Stream containing all the LXP records in this Bucket
     */
    IInputStream<T> getInputStream() throws BucketException;

    /**
     * @return an output Stream which supports the writing of records to this Bucket
     */
    IOutputStream<T> getOutputStream();

    /**
     * @return the name of the bucket
     */
    String getName();

    /**
     * @return the repository in which the bucket is located
     */
    IRepository getRepository();

    /**
     * A predicate to determine if a LXP with the given id is located in the bucker.
     * @param id - an id to lookup
     * @return true if the bucket contains the given id
     */
    boolean contains(int id);

    /**
     * @return the implementation kind of the bucket
     */
    BucketKind getKind();

    /**
     * Sets the type of the bucket contents.
     * @param id - the id of a type rep specifyling the content type of the bucket.
     * Such types are specified using a TypeFactory and are stored in the STORE's type repo.
     */
    void setTypeLabelID(int id) throws IOException;

}
