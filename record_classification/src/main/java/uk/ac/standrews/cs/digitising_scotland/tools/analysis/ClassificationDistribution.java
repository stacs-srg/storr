/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
