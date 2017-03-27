package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;

import java.io.IOException;
import java.net.BindException;
import java.util.List;

/**
 * The interface for a Bucket (a repository of OID records).
 * Each record in the repository is identified by id.
 *
 * Operations in this class mirror those in JDO:
 *      T getObjectById(long id) throws BucketException;
 *      void makePersistent(T record) throws BucketException;
 * 
 */
public interface IBucket<T extends ILXP> {

    /**
     * Gets the OID record with the specified id
     *
     * @param id - the identifier of the OID record for which a reader is required.
     * @return an OID record with the specified id, or null if the record cannot be found
     * @throws BucketException if the record cannot be found or if something goes wrong.
     */
    T getObjectById(long id) throws BucketException;

    /**
     * Synchronously writes the state of a record to a bucket.
     * The id of the record is used to determine its name in the bucket.
     * When this operation returns data is stored resiliently.
     * @param record whose state is to be written.
     * @throws BucketException if an error occurs during the operation.
     */
    void makePersistent(T record) throws BucketException;

    /**
     * Updates the state of the specified record in the store.
     * Must be performed in the context of a transaction
     * @param record the record to be updated
     * @throws BucketException if an error occurs during the operation.
     */
    void update(T record) throws BucketException;

    /**
     * Delete the record with the specified oid
     * @param oid denoting the record to be deleted
     * @throws BucketException if an error occurs during the operation.
     */
    void delete(long oid) throws BucketException;

    /**
     * @param id - the id for which the file path is required, should be an id of an LXP stored in the bucket
     * @return the filepath corresponding to record with identifier id in this bucket (this is more public than it should be).
     */
    String filePath(long id);

    /**
     * @return an input Stream containing all the OID records in this Bucket
     * @throws BucketException if an error occurs during the operation.
     */
    IInputStream<T> getInputStream() throws BucketException;

    /**
     * @return an output Stream which supports the writing of records to this Bucket
     */
    IOutputStream<T> getOutputStream();

    /**
     * @return the oids of the records that are in this bucket
     */
    public List<Long> getOids();

    /**
     * @return the name of the bucket
     */
    String getName();

    /**
     * @return the repository in which the bucket is located
     */
    IRepository getRepository();

    /**
     * Returns the number of records stored in the bucket
     */
    long size() throws BindException, BucketException;

    /**
     * A predicate to determine if a OID with the given id is located in the bucket.
     * @param id - an id to lookup
     * @return true if the bucket contains the given id
     */
    boolean contains(long id);

    /**
     * @return the implementation kind of the bucket
     * see @class BucketKind
     */
    BucketKind getKind();

    /**
     * @return the factory associated with the bucket if there is one and null if there is not.
     */
    ILXPFactory<T> getFactory();

    /**
     * Sets the type of the bucket contents.
     * @param id - the id of a type rep specifyling the content type of the bucket.
     * Such types are specified using a TypeFactory and are stored in the STORE's type repo.
     * @throws IOException if one occurs during the underlying operations.
     */
    void setTypeLabelID(long id) throws IOException;

    /**
     * @return the cache of loaded objects (in memory) in the system
     */
    IObjectCache getObjectCache();

    /**
     * Used by transaction API only.
     * @param oid - the oid to swizzle
     */
     void swizzle(long oid);

    /**
     * Used by transaction API only.
     * @param oid - the oid to be cleaned up
     */
    void cleanup(long oid);

    /**
     * Used by transaction API only.
     * Tidies up transaction data that may be left over following a crash
     */
    void tidyUpTransactionData();

    /**
     * Used to invalidate cached information when updates to underlying datastruuctures are updated
     */
    void invalidateCache();
}
