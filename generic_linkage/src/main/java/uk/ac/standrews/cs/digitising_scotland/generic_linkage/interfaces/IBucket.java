package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

import org.json.JSONException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.IOException;

/**
 * The interface for a Bucket (a repository of LXP records).
 * Each record in the repository is identified by id.
 */
public interface IBucket {

    /**
     *
     * @param id - the identifier of the LXP record for which a reader is required.
     * @return a JSONReader that will read the JSON encoded representation of an LXP record with the specified id.
     * @return null if the file cannot be found or an error in the id
     */
    JSONReader getReader(int id) throws IOException;

    /**
     *
     * @param id
     * @return the filepath corresponding to record with identifier id in this bucket (more public than it should be).
     */
    String filePath(int id);

    /**
     *
     * @return an input Stream containing all the LXP records in this Bucket
     */
    ILXPInputStream getInputStream();

    /**
     *
     * @return an output Stream which supports the writing of records to this Bucket
     */
    ILXPOutputStream getOutputStream();

    /**
     *
     * @return the name of the bucket
     */
    String getName();

    /**
     *
     * Writes the state of a record to a bucket
     * @param record whose state is to be written
     */
    void save( ILXP record ) throws IOException, JSONException;
}
