package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records;

import java.util.*;

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
     * Gets the original data. Original data is the data supplied on records.
     *
     * @return the original data
     */
    public OriginalData getOriginalData() {

        return originalData;
    }

    /**
     * Gets the cleaned description.The cleaned description is the original description with punctuation etc removed.
     *
     * @return the cleaned description
     */
    public List<String> getDescription() {

        return originalData.getDescription();
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

    @Override
    public String toString() {

        return "Record [id=" + id + ", goldStandardTriples=" + originalData.getGoldStandardClassifications() + ", codeTriples=" + getCodeTriples() + "]";
    }


    /**
     * Gets the Set of {@link Classification}s contained in this record.
     *
     * @return the Set of CodeTriples.
     */
    public Set<Classification> getCodeTriples() {
        return new HashSet<>(listOfClassifications.values());
    }

    public HashMultimap<String, Classification> getListOfClassifications() {

        return listOfClassifications;
    }

    public void setListOfClassifications(final HashMultimap<String, Classification> listOfClassifications) {

        this.listOfClassifications = listOfClassifications;
    }
}
