package uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces;

/**
 * Provides blocking over an ILXPInputStream to put records selected into a bucket
 * Created by al on 29/04/2014.
 */
public interface IBlocker {

    /**
     * @return the ILXPInputStream over which filtering is being performed.
     */
    ILXPInputStream getInput();

    /**
     * @param record a record to be blocked
     * @return the names of the buckets into which the record should be blocked
     */
    String[] determineBlockedBucketNamesForRecord(ILXP record);
}
