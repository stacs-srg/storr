package uk.ac.standrews.cs.digitising_scotland.util;

/**
 * 
 * Simple class for doing array manipulation.
 * I am sure this is somewhere in some library but I know not where!
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 *
 */
public class ArrayManipulation {

    /**
     * Sums an array of ints.
     * @return the sum of the array supplied as a parameter.
     * @param array - the array over which to sum.
     */
    public static int sum(final int[] array) {

        int count = 0;
        for (final int element : array) {
            count += element;
        }
        return count;
    }
}
