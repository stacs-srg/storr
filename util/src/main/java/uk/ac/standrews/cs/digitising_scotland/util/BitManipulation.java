/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
