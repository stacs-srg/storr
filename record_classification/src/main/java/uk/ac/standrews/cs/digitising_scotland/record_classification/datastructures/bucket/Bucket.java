package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * A Bucket is an iterable collection of {@link Record} objects. Buckets can be manipulated using the {@link BucketUtils}
 * and {@link BucketFilter} classes. Methods for checking if a bucket contains a given record, checking it's size etc are provided.
 * @author jkc25, frjd2
 *
 */
public class Bucket implements Iterable<Record> {

    /** The records. */
    private Map<String, Record> records;

    /**
     * Instantiates a new empty bucket.
     */
    public Bucket() {

        records = new LinkedHashMap<String, Record>();
    }

    /**
     * Instantiates a new bucket from a list of {@link Record}s.
     *
     * @param listOfRecords the list of {@link Record}s
     */
    public Bucket(final List<Record> listOfRecords) {

        this();
        for (Record record : listOfRecords) {
            addRecordToBucket(record);
        }
    }

    /**
     * Adds a single {@link Record} to a bucket.
     * @param recordToInsert the record to insert.
     */
    public void addRecordToBucket(final Record recordToInsert) {

        records.put(recordToInsert.getUid(), recordToInsert);
    }

    /**
     * Adds each {@link Record} to the Bucket's collection of records.
     * Bucket is iterable over {@link Record}s so can be used as an argument to this method.
     * @param records {@link Collection} of {@link Record}s to add.
     */
    public void addCollectionOfRecords(final Iterable<Record> records) {

        for (Record record : records) {
            addRecordToBucket(record);
        }
    }

    /**
     * Checks if the specified record is in this bucket. Will return true if and only if the record is a member of the bucket.
     * @param record record to check for membership.
     * @return true if this record is a member, false otherwise
     */
    public boolean contains(final Record record) {

        return records.containsValue(record);
    }

    /**
     * Removes the record from this bucket if it is present (optional operation).
     *  More formally, if this map contains a mapping from key k to value v such that (key==null ? k==null : key.equals(k)), that mapping is removed.
     *  (The map can contain at most one such mapping.)
     *
     *  Returns the value to which this map previously associated the key, or null if the map contained no mapping for the key.
     * @param record Record to remove
     * @return the previous record associated with the record, or null if there was no record for the record.
     */
    public Record remove(final Record record) {

        return records.remove(record.getUid());
    }

    /**
     * Returns the number of {@link Record}s in the bucket.
     *
     * @return the number of records in the bucket
     */
    public int size() {

        return records.size();
    }

    /**
     * Iterator A {@link Record} itereator that allows iteration though all the records in
     * the bucket.
     *
     * @return iterator of type Iterator<Record>.
     */
    @Override
    public Iterator<Record> iterator() {

        return records.values().iterator();
    }

    /**
     * Returns a single record that matches the String UID supplied.
     * @param uID hashCode of the vector we are looking for
     * @return Record with matching hashCode
     */
    public Record getRecord(final String uID) {

        return records.get(uID);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Bucket [records=" + records + "]";
    }

}
