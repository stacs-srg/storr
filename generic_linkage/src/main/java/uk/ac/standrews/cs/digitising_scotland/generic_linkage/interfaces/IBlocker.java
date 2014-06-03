package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Provides blocking over an ILXPInputStream to put records selected into a bucket
 * Created by al on 29/04/2014.
 */
public interface IBlocker {

    /**
     * @return the ILXPInputStream over which blocking is being performed.
     */
    ILXPInputStream getInput();

    /*
     * Applies the blocking method @method determineBlockedBucketNamesForRecord to the input records
     * and assigns to the determined bucket
     */
    void apply();

    /**
     *
     * @param record the record to be assigned to a bucket (determined by @method determineBlockedBucketNamesForRecord
     */
    void assign(ILXP record);

    /**
     * @param record a record to be blocked
     * @return the names of the buckets into which the record should be blocked
     */
    String[] determineBlockedBucketNamesForRecord(ILXP record);
}
