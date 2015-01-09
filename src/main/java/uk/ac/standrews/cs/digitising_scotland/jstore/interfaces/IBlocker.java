package uk.ac.standrews.cs.digitising_scotland.jstore.interfaces;

/**
 * Classes that implement this interface provides blocking over
 * an ILXPInputStream to makePersistent records selected into some bucket.
 * Created by al on 29/04/2014.
 */
public interface IBlocker<T extends ILXP> {

    /**
     * @return the ILXPInputStream over which blocking is being performed.
     */
    IInputStream<T> getInput();

    /*
     * Applies the blocking method @method determineBlockedBucketNamesForRecord to the input records
     * and assigns to the determined bucket
     */
    void apply();

    /**
     * Assins the record to the appropriate bucket
     *
     * @param record the record to be assigned to a bucket (determined by @method determineBlockedBucketNamesForRecord
     */
    void assign(T record);

    /**
     * Determins the names of the buckets into which a record should be placed.
     * The actual assignment is performed by @method assign.
     *
     * @param record a record to be blocked
     * @return the names of the buckets into which the record should be blocked
     */
    String[] determineBlockedBucketNamesForRecord(T record);

}
