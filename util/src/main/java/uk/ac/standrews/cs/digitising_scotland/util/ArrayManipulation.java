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
 * Simple class for doing array manipulation.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class ArrayManipulation {

    /**
     * Sums an array of ints.
     *
     * @param array - the array over which to sum.
     * @return the sum of the array supplied as a parameter.
     */
    public static int sum(final int[] array) {

        int count = 0;
        for (final int element : array) {
            count += element;
        }
        return count;
    }
}
