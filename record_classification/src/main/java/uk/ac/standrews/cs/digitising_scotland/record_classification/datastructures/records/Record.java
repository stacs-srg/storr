package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;

import com.google.common.collect.HashMultimap;

/**
 * The Class Record. Represents a Record and all associated data, including that which is supplied by NRS.
 */
public class Record {

    /** The u id. */
    private final int id;

    /** The original data. */
    private OriginalData originalData;

    /** The code triples. */
    private HashMultimap<String, Classification> listOfClassifications;

    /**
     * Instantiates a new record.
     * @param id the unique id of this record
     * @param originalData the original data from the initial record.
     */
    public Record(final int id, final OriginalData originalData) {

        this.id = id;
        this.originalData = originalData;
        listOfClassifications = HashMultimap.create();

    }

    /**
     * Copy the current records original attributes.
     * @param source
     */
    public Record copyOfOriginalRecord(Record source) {

        return new Record(source.id, source.originalData);

    }

    /**
     * Gets the original data. Original data is the data supplied on records.
     *
     * @return the original data
     */
    public OriginalData getOriginalData() {

        return originalData;
    }

    /**
     * Gets the description from the record's original data object.
     *
     * @return the cleaned description
     */
    public List<String> getDescription() {

        return originalData.getDescription();
    }

    /**
     * Updates a specific line of the description to a new value.
     * @param oldDescription line to update
     * @param newDescription new value
     * @return true if replacement successful
     */
    public boolean updateDescription(final String oldDescription, final String newDescription) {

        int index = originalData.getDescription().indexOf(oldDescription);
        if (index != -1) {
            originalData.getDescription().set(index, newDescription);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Gets the unique ID of the record.
     *
     * @return unique ID
     */
    public int getid() {

        return id;
    }

    /**
     * Returns the gold standard set of {@link Classification} for this Record.
     * If no gold standard set exists then an empty {@link Classification} will be returned.
     *
     * @return the gold standard classification set
     */
    public Set<Classification> getGoldStandardClassificationSet() {

        return originalData.getGoldStandardClassifications();
    }

    /**
     * Returns true is this record is of the subType CoDRecord.
     * @return true if cause of death record
     */
    public boolean isCoDRecord() {

        String thisClassName = originalData.getClass().getName();
        String[] split = thisClassName.split("\\.");
        if (split[split.length - 1].equals("CODOrignalData")) { return true;

        }
        return false;
    }

    /**
     * Adds a {@link Classification} to the list of classifications that this record has. The classification's tokenSet is used as the key.
     * @param classification to add.
     * @return true if the method increased the size of the multimap, or false if the multimap already contained the key-value pair
     */
    public boolean addClassification(final Classification classification) {

        return listOfClassifications.put(classification.getTokenSet().toString(), classification);
    }

    /**
     * Adds a {@link Classification} to the list of classifications that this record has. The description given is used as the key.
     * @param classification to add.
     * @return true if the method increased the size of the multimap, or false if the multimap already contained the key-value pair
     */
    public boolean addClassification(final String description, final Classification classification) {

        return listOfClassifications.put(description, classification);
    }

    /**
     * Gets the Set of {@link Classification}s contained in this record.
     *
     * @return the Set of CodeTriples.
     */
    public Set<Classification> getClassifications() {

        return new HashSet<>(listOfClassifications.values());
    }

    public HashMultimap<String, Classification> getListOfClassifications() {

        return listOfClassifications;
    }

    public void setListOfClassifications(final HashMultimap<String, Classification> listOfClassifications) {

        this.listOfClassifications = listOfClassifications;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((listOfClassifications == null) ? 0 : listOfClassifications.hashCode());
        result = prime * result + ((originalData == null) ? 0 : originalData.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Record other = (Record) obj;
        if (id != other.id) { return false; }
        if (listOfClassifications == null) {
            if (other.listOfClassifications != null) { return false; }
        }
        else if (!listOfClassifications.equals(other.listOfClassifications)) { return false; }
        if (originalData == null) {
            if (other.originalData != null) { return false; }
        }
        else if (!originalData.equals(other.originalData)) { return false; }
        return true;
    }

    @Override
    public String toString() {

        return "Record [id=" + id + ", originalData=" + originalData + ", listOfClassifications=" + listOfClassifications + "]";
    }

}
