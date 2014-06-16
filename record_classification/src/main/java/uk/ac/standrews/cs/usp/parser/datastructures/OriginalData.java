package uk.ac.standrews.cs.usp.parser.datastructures;

import java.util.HashSet;
import java.util.Set;

import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;

/**
 * Represents original data from NRS. Cannot be changed once set.
 * @author jkc25, frjd2
 *
 */
public class OriginalData implements java.io.Serializable {

    private static final long serialVersionUID = -1453338613207961366L;
    private String description;
    private int year;
    private int imageQuality;
    private String fileName;
    private Set<CodeTriple> goldStandardClassification;

    /**
     * Describes original data from NRS.
     * @param description raw description as transcribed
     * @param year year from original record
     * @param imageQuality 0 or 1. 1 if bad, 0 is OK
     * @param fileName name of file containing original data in record
     * @throws InputFormatException If one or more of the inputs are null.
     */
    public OriginalData(final String description, final int year, final int imageQuality, final String fileName) throws InputFormatException {

        if (imageQuality < 0 || imageQuality > 1) { throw new NumberFormatException("image quality must be 0 or 1, currently: " + imageQuality + "\ndescription: " + description); }
        this.description = description;
        this.year = year;
        this.imageQuality = imageQuality;
        this.fileName = fileName;
        goldStandardClassification = new HashSet<CodeTriple>();
        checkNotNull();
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {

        return description;
    }

    /**
     * Gets the year.
     *
     * @return the year
     */
    public int getYear() {

        return year;
    }

    /**
     * Gets the image quality.
     *
     * @return the image quality
     */
    public int getImageQuality() {

        return imageQuality;
    }

    private void checkNotNull() throws InputFormatException {

        if (description == null) { throw new InputFormatException("description passed to constructor cannot be null"); }
        if (fileName == null) { throw new InputFormatException("file name passed to constructor cannot be null"); }

    }

    /**
     * Name of the file where this data came from.
     * @return name of original file containing this original data
     */
    public String getFileName() {

        return fileName;
    }

    @Override
    public String toString() {

        return "OriginalData [description=" + description + ", year=" + year + ", imageQuality=" + imageQuality + ", fileName=" + fileName + "]";
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((goldStandardClassification == null) ? 0 : goldStandardClassification.hashCode());
        result = prime * result + imageQuality;
        result = prime * result + year;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        OriginalData other = (OriginalData) obj;
        if (description == null) {
            if (other.description != null) { return false; }
        }
        else if (!description.equals(other.description)) { return false; }
        if (fileName == null) {
            if (other.fileName != null) { return false; }
        }
        else if (!fileName.equals(other.fileName)) { return false; }
        if (goldStandardClassification == null) {
            if (other.goldStandardClassification != null) { return false; }
        }
        else if (!goldStandardClassification.equals(other.goldStandardClassification)) { return false; }
        if (imageQuality != other.imageQuality) { return false; }
        if (year != other.year) { return false; }
        return true;
    }

    public Set<CodeTriple> getGoldStandardCodeTriples() {

        return goldStandardClassification;
    }

    public void setGoldStandardClassification(final Set<CodeTriple> goldStandardClassification) {

        this.goldStandardClassification = goldStandardClassification;
    }

}