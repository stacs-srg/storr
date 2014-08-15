package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Stores the name, number of features in the class and the percentage of total features that it contains.
 * 
 * @author jkc25
 */
public class ClassificationDistribution implements Comparable<ClassificationDistribution> {

    private String name;
    private int numberOfFeatures;
    private double percentage;

    /**
     * Constructs a new ClassificationDistribution.
     * 
     * @param name
     *            Name of the class
     * @param number
     *            Number of files/features in the class
     * @param perc
     *            percentage of total features
     */
    public ClassificationDistribution(final String name, final int number, final double perc) {

        this.name = name;
        this.numberOfFeatures = number;
        this.percentage = perc;
    }

    /**
     * Returns String representation of the class.
     * 
     * @return String representation of the class.
     */
    public String toString() {

        return name + "\t" + numberOfFeatures + "\t" + percentage;
    }

    /**
     * Returns the name of the class.
     * 
     * @return Name of the class.
     */
    public String getName() {

        return name;
    }

    /**
     * Returns the number of features for this class.
     * 
     * @return Number of features.
     */
    public int getNumberOfFeatures() {

        return numberOfFeatures;
    }

    /**
     * Returns the percentage of the total features that this class contains.
     * 
     * @return the percentage of the total features that this class contains.
     */
    public double getPercentage() {

        return percentage;
    }

    /**
     * Comparison between CLassificationDistributions based on % of total files.
     * 
     * @param o
     *            the CLassificationDistribution to compare to.
     * @return -1 if more, 1 if less, 0 if equal
     */
    @Override
    public int compareTo(final ClassificationDistribution o) {

        if (percentage == o.getPercentage()) { return 0; }

        if (percentage > o.getPercentage()) { return -1; }

        if (percentage < o.getPercentage()) { return 1; }

        return 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (!(obj instanceof ClassificationDistribution)) { return false; }

        ClassificationDistribution rhs = (ClassificationDistribution) obj;
        return new EqualsBuilder().append(percentage, rhs.percentage).isEquals();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31).append(percentage).append(name).append(numberOfFeatures).hashCode();
    }
}
