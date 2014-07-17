package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.TokenizerCleaner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.OriginalData;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;

/**
 * The Class Record. Represents a Record and all associated data, including that which is supplied by NRS.
 */
public class Record {

    /** The u id. */
    private final int id;

    /** The original data. */
    private OriginalData originalData;

    /** The code triples. */
    private Set<CodeTriple> codeTriples;

    /**
     * Instantiates a new record.
     * @param id the unique id of this record
     * @param originalData the original data from the initial record.
     */
    public Record(final int id, final OriginalData originalData) {

        this.id = id;
        this.originalData = originalData;
        this.codeTriples = new LinkedHashSet<>();
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
    public String getDescription() {
//
//        if (cleanedDescription == null) {
//            cleanedDescription = TokenizerCleaner.clean(originalData.getDescription()).intern();
//        }

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
     * Returns the gold standard set of {@link CodeTriple} for this Record.
     * If no gold standard set exists then an empty {@link CodeTriple} will be returned.
     *
     * @return the gold standard classification set
     */
    public Set<CodeTriple> getGoldStandardClassificationSet() {

        return originalData.getGoldStandardCodeTriples();
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        return "Record [description=" + getDescription() + "\t codeTriples=" + codeTriples + "]\n";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((codeTriples == null) ? 0 : codeTriples.hashCode());
        result = prime * result + ((originalData == null) ? 0 : originalData.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Record other = (Record) obj;
        if (codeTriples == null) {
            if (other.codeTriples != null) { return false; }
        }
        else if (!codeTriples.equals(other.codeTriples)) { return false; }
        if (originalData == null) {
            if (other.originalData != null) { return false; }
        }
        else if (!originalData.equals(other.originalData)) { return false; }
        return true;
    }

    /**
     * Gets the Set of {@link CodeTriple}s contained in this record.
     *
     * @return the Set of CodeTriples.
     */
    public Set<CodeTriple> getCodeTriples() {

        return codeTriples;
    }

    /**
     * Adds a code triple to the set of {@link CodeTriple}s maintained by this record.
     * The CodeTriple is only added if it is non null.
     *
     * @param codeTriples the code triple to add
     */
    public void addCodeTriples(final CodeTriple codeTriples) {

        if (codeTriples != null) {
            this.codeTriples.add(codeTriples);
        }
    }

    /**
     * Adds all the code triples in the collection.
     * Null CodeTriples are not added.
     * 
     * @param codeTriples the  collection of code triples to add.
     */
    public void addAllCodeTriples(final Collection<CodeTriple> codeTriples) {

        for (CodeTriple codeTriple : codeTriples) {
            addCodeTriples(codeTriple);
        }
    }
}
