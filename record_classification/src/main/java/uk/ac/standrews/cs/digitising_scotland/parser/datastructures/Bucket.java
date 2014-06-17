package uk.ac.standrews.cs.digitising_scotland.parser.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains a set of records and metrics associated with that bucket.
 * @author jkc25, frjd2
 *
 */
public class Bucket implements Iterable<Record> {

    //TODO nail down bucket behaviour - specifically what should be used as the key in this map
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

        //TODO if bucket contains similar record consolidate records?
        //TODO if doing this return to using hashmaps as record ID in vectors?
        records.put(recordToInsert.getUid(), recordToInsert);
    }

    /**
     * Adds each {@link Record} to the Bucket's collection of records.
     * Bucket is iterable over {@link Record}s so can be used as an argument to this method.
     * @param records {@link Collection} of {@link Record}s to add.
     */
    public void addCollectionOfRecords(final Iterable<Record> records) {

        //TODO do we want this to be a merge type operation?
        for (Record record : records) {
            addRecordToBucket(record);
        }
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

    @Override
    public String toString() {

        return "Bucket [records=" + records + "]";
    }

}
