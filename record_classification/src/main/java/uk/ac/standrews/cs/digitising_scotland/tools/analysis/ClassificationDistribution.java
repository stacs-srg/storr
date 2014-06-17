/*
 public static <K, V extends Comparable<? super V>> Map<K, V>
        sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    } public static <K, V extends Comparable<? super V>> Map<K, V>
        sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list =
            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return (o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    } * | ______________________________________________________________________________________________ | Understanding
 * Scotland's People (USP) project. | | The aim of the project is to produce a linked pedigree for all publicly | |
 * available Scottish birth/death/marriage records from 1855 to the present day. | | | | Digitization of the records is
 * being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National
 * Records of Scotland. | | | | The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. | |
 * The other project members are Lee Williamson (also at the Longitudinal Studies Centre) | | Graham Kirby, Alan Dearle
 * and Jamie Carson at the School of Computer Science at St Andrews; | | and Eilidh Garret and Alice Reid at the
 * Department of Geography at Cambridge. | | | |
 * ______________________________________________________________________________________________
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
