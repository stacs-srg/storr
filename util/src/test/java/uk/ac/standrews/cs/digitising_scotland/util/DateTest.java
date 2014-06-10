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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static uk.ac.standrews.cs.digitising_scotland.util.DateManipulation.START_YEAR;

/**
 * Tests of date conversion.
 *         
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class DateTest {

    private static final int DAYS_IN_JANUARY = 31;
    private static final int DAYS_IN_NON_LEAP_FEBRUARY = 28;
    private final int number_of_leap_days_in_start_year = DateManipulation.isLeapYear(START_YEAR) ? 1 : 0;

    private int first_february_in_days, first_march_in_days, following_first_january_in_days;

    @Before
    public void setup() {

        first_february_in_days = DAYS_IN_JANUARY;
        first_march_in_days = DAYS_IN_JANUARY + DAYS_IN_NON_LEAP_FEBRUARY + number_of_leap_days_in_start_year;
        following_first_january_in_days = DateManipulation.DAYS_IN_NON_LEAP_YEAR + number_of_leap_days_in_start_year;
    }

    @Test
    public void dateToDays() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals(0, DateManipulation.dateToDays(START_YEAR, 0, 1));
        assertEquals(1, DateManipulation.dateToDays(START_YEAR, 0, 2));
        assertEquals(2, DateManipulation.dateToDays(START_YEAR, 0, 3));
        assertEquals(30, DateManipulation.dateToDays(START_YEAR, 0, 31));

        // 1st February of start year.
        assertEquals(first_february_in_days, DateManipulation.dateToDays(START_YEAR, 1, 1));

        // 1st March of start year.
        assertEquals(first_march_in_days, DateManipulation.dateToDays(START_YEAR, 2, 1));

        // 1st January of year after start year.
        assertEquals(following_first_january_in_days, DateManipulation.dateToDays(START_YEAR + 1, 0, 1));
    }


    @Test
    public void daysToString() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals("1 Jan " + START_YEAR, DateManipulation.daysToString(0));
        assertEquals("2 Jan " + START_YEAR, DateManipulation.daysToString(1));
        assertEquals("3 Jan " + START_YEAR, DateManipulation.daysToString(2));
        assertEquals("31 Jan " + START_YEAR, DateManipulation.daysToString(30));

        // 1st February of start year.
        assertEquals("1 Feb " + START_YEAR, DateManipulation.daysToString(first_february_in_days));

        // 1st March of start year.
        assertEquals("1 Mar " + START_YEAR, DateManipulation.daysToString(first_march_in_days));

        // 1st January of year after start year.
        assertEquals("1 Jan " + (START_YEAR + 1), DateManipulation.daysToString(following_first_january_in_days));
    }

    @Test
    public void daysToDay() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals(1, DateManipulation.daysToDay(0));
        assertEquals(2, DateManipulation.daysToDay(1));
        assertEquals(3, DateManipulation.daysToDay(2));
        assertEquals(31, DateManipulation.daysToDay(30));

        // 1st February of start year.
        assertEquals(1, DateManipulation.daysToDay(first_february_in_days));

        // 1st March of start year.
        assertEquals(1, DateManipulation.daysToDay(first_march_in_days));

        // 1st January of year after start year.
        assertEquals(1, DateManipulation.daysToDay(following_first_january_in_days));
    }

    @Test
    public void daysToMonth() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals(0, DateManipulation.daysToMonth(0));
        assertEquals(0, DateManipulation.daysToMonth(1));
        assertEquals(0, DateManipulation.daysToMonth(2));
        assertEquals(0, DateManipulation.daysToMonth(30));

        // 1st February of start year.
        assertEquals(1, DateManipulation.daysToMonth(first_february_in_days));

        // 1st March of start year.
        assertEquals(2, DateManipulation.daysToMonth(first_march_in_days));

        // 1st January of year after start year.
        assertEquals(0, DateManipulation.daysToMonth(following_first_january_in_days));
    }

    @Test
    public void daysToYear() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals(START_YEAR, DateManipulation.daysToYear(0));
        assertEquals(START_YEAR, DateManipulation.daysToYear(1));
        assertEquals(START_YEAR, DateManipulation.daysToYear(2));
        assertEquals(START_YEAR, DateManipulation.daysToYear(30));

        // 1st February of start year.
        assertEquals(START_YEAR, DateManipulation.daysToYear(first_february_in_days));

        // 1st March of start year.
        assertEquals(START_YEAR, DateManipulation.daysToYear(first_march_in_days));

        // 1st January of year after start year.
        assertEquals(START_YEAR + 1, DateManipulation.daysToYear(following_first_january_in_days));
    }

    @Test
    public void addYears() {

        // 1st, 2nd, 3rd, 31st January of start year.
        assertEquals(following_first_january_in_days, DateManipulation.addYears(DateManipulation.dateToDays(START_YEAR, 0, 1), 1));
        assertEquals(following_first_january_in_days + 1, DateManipulation.addYears(DateManipulation.dateToDays(START_YEAR, 0, 2), 1));
        assertEquals(following_first_january_in_days + 2, DateManipulation.addYears(DateManipulation.dateToDays(START_YEAR, 0, 3), 1));
        assertEquals(following_first_january_in_days + 30, DateManipulation.addYears(DateManipulation.dateToDays(START_YEAR, 0, 31), 1));

        // 70 years after 15th March 1875 should be 15th March 1945
        assertEquals(DateManipulation.dateToDays(1945, 2, 15), DateManipulation.addYears(DateManipulation.dateToDays(1875, 2, 15), 70));
    }

    @Test
    public void isLeapYear() {

        assertFalse(DateManipulation.isLeapYear(1779));
        assertTrue(DateManipulation.isLeapYear(1780));
        assertFalse(DateManipulation.isLeapYear(1781));
        assertFalse(DateManipulation.isLeapYear(1782));
        assertFalse(DateManipulation.isLeapYear(1783));
        assertTrue(DateManipulation.isLeapYear(1784));

        // Years divisible by 100 are only leap years if also divisible by 400.
        assertFalse(DateManipulation.isLeapYear(1799));
        assertFalse(DateManipulation.isLeapYear(1800));
        assertFalse(DateManipulation.isLeapYear(1801));

        assertFalse(DateManipulation.isLeapYear(1999));
        assertTrue(DateManipulation.isLeapYear(2000));
        assertFalse(DateManipulation.isLeapYear(2001));
    }
}
