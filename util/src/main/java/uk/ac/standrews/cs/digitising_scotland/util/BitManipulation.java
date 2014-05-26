package uk.ac.standrews.cs.digitising_scotland.util;

/**
 * Utility class implementing reading and writing bits within a byte.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class BitManipulation {

    /**
     * Reads a specified bit within a byte.
     * @param b the byte
     * @param position the bit position, from 0-7
     * @return the bit
     */
    public static boolean readBit(final byte b, final int position) {

        return (b & 1 << position) != 0;
    }

    /**
     * Writes a specified bit within a byte.
     * @param b the byte
     * @param bit the bit value to be written
     * @param position the bit position, from 0-7
     * @return the resulting byte
     */
    public static byte writeBit(final byte b, final boolean bit, final int position) {

        final byte mask = (byte) (1 << position);
        return (byte) (bit ? b | mask : b & ~mask);
    }
}
