package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import org.json.JSONException;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;

/**
 * The interface for a Bucket (a repository of LXP records).
 * Each record in the repository is identified by id.
 */
public interface IBucketLXP {

    /**
     * @param id - the identifier of the LXP record for which a reader is required.
     * @return an LXP record with the specified id, or null if the record cannot be found
     */
    ILXP get(int id) throws IOException, PersistentObjectException;

    /**
     * Writes the state of a record to a bucket
     *
     * @param record whose state is to be written
     */
    void put(ILXP record) throws IOException, JSONException;

    /**
     * @param id
     * @return the filepath corresponding to record with identifier id in this bucket (more public than it should be).
     */
    String filePath(int id);

    /**
     * @return an input Stream containing all the LXP records in this Bucket
     */
    LXPInputStream getInputStream();

    /**
     * @return an output Stream which supports the writing of records to this Bucket
     */
    ILXPOutputStreamUnTypedNEW getOutputStream();

    /**
     * @return the name of the bucket
     */
    String getName();

    /**
     * @param id - an id to lookup
     * @return true if the bucket contains the given id
     */
    boolean contains(int id);

    /**
    * @return the kind of the bucket
    */
    BucketKind kind();

    ITypeLabel getBucketContentType();

}
