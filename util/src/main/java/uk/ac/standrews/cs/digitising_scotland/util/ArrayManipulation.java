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

import java.util.Arrays;
import java.util.List;

/**
 * Various array manipulation utilities.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
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

    public static <T> int binarySplit(final List<T> array, final SplitComparator<T> comparator) {

        int low = 0;
        int high = array.size() - 1;

        while (low <= high) {

            final int mid = low + (high - low) / 2;
            final T mid_element = array.get(mid);

            final int check = comparator.check(mid_element);

            if (check < 0) {
                high = mid - 1;

            } else if (check > 0) {
                low = mid + 1;

            } else {
                return mid;
            }
        }

        return -1;
    }

    public static <T> int binarySplit(final T[] array, final SplitComparator<T> comparator) {

        return binarySplit(Arrays.asList(array), comparator);
    }
}
