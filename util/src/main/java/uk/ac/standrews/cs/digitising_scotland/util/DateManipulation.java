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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Provides methods to manipulate dates represented as integers containing number of days elapsed since a fixed start date.
 *
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class DateManipulation {

    /**
     * Number of days in non-leap-year.
     */
    public static final int DAYS_IN_NON_LEAP_YEAR = 365;

    private static final int HOURS_AT_MIDDAY = 12;
    private static final long MILLIS_PER_DAY = 1000 * 60 * 60 * 24;
    private static final long START_IN_MILLIS;

    private static final Calendar CALENDAR;
    private static final Map<String, Integer> CALENDAR_MONTHS;

    public static final int START_YEAR = 1600;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    static {

        // We use the Calendar class for the tricky date manipulation, and it represents dates as elapsed milliseconds.

        // Get the millisecond representation of midday on the start date: 1st January of the start year.
        // Months are numbered starting from zero.

        // Can't set to millisecond precision, so the 3 least significant figures are determined at call time. It doesn't
        // matter what they are as they stay the same for all dates subsequently manipulated.

        CALENDAR = Calendar.getInstance();

        CALENDAR.set(START_YEAR, 0, 1, HOURS_AT_MIDDAY, 0, 0);

        CALENDAR_MONTHS = CALENDAR.getDisplayNames(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        START_IN_MILLIS = CALENDAR.getTimeInMillis();
    }


    /**
     * Calculates the number of days elapsed between 1st January of the {@link #START_YEAR} and the given date.
     *
     * @param year  the year
     * @param month the month of the given date, with 0 representing January
     * @param day   the day of the month
     * @return the number of days elapsed
     */
    public static synchronized int dateToDays(final int year, final int month, final int day) {

        // Get the millisecond representation of midday on the given date.
        CALENDAR.set(year, month, day, HOURS_AT_MIDDAY, 0, 0);
        return millisToDays();
    }

    /**
     * Calculates the number of days elapsed between 1st January of the {@link #START_YEAR} and the given date.
     *
     * @param date the date from which to calculate the days
     * @return the number of days elapsed
     */
    public static synchronized int dateToDays(final Date date) {

        CALENDAR.setTime(date);
        return millisToDays();
    }

    /**
     * Returns a java.sql.Date representation of the date represented by a String such as that created by {@link DateManipulation#daysToString}.
     *
     * @param date the string representation of the date
     * @return a java.sql.Date representation of the date
     * @throws ParseException if the date representation is not valid
     */
    public static synchronized java.sql.Date stringSQLToDate(final String date) throws ParseException {

        try {
            final StringTokenizer st = new StringTokenizer(date, " ");
            final int day = Integer.parseInt(st.nextToken());
            final int month = indexOfMonth(st.nextToken());
            final int year = Integer.parseInt(st.nextToken());

            CALENDAR.set(year, month, day);
            return new java.sql.Date(CALENDAR.getTimeInMillis());

        } catch (final UnknownMonthException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    private static int indexOfMonth(final String month_name) throws UnknownMonthException {

        final Integer index = CALENDAR_MONTHS.get(month_name);
        if (index != null) {
            return index;
        }

        throw new UnknownMonthException("Unknown month: " + month_name);
    }

    /**
     * Returns a text representation of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return a text representation of the date
     */
    public static synchronized String daysToString(final int days) {

        return formatDate(daysToDate(days), DATE_FORMAT);
    }

    public static synchronized String formatDate(final Date date, SimpleDateFormat formatter) {

        return formatter.format(date);
    }

    public static synchronized String formatDate(final Date date) {

        return formatDate(date, DATE_FORMAT);
    }

    public static synchronized Date parseDate(final String date_string, SimpleDateFormat formatter) throws ParseException {

        return formatter.parse(date_string);
    }

    /**
     * Returns a {@link Date} representation of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return a representation of the date
     */
    public static synchronized Date daysToDate(final int days) {

        setCalendarToDate(days);
        return CALENDAR.getTime();
    }

    /**
     * Returns a {@link Date} representation of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return a representation of the java.sql.Date
     */
    public static synchronized java.sql.Date daysToSQLDate(final int days) {

        setCalendarToDate(days);
        return new java.sql.Date(CALENDAR.getTimeInMillis());
    }

    public static synchronized java.sql.Date dateToSQLDate(final Date date) {

        return daysToSQLDate(dateToDays(date));
    }

    /**
     * Returns the day of the month of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return the day of the month, numbered from 1
     */
    public static synchronized int daysToDay(final int days) {

        setCalendarToDate(days);
        return CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns the month of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return the month, numbered from 0 for January
     */
    public static synchronized int daysToMonth(final int days) {

        setCalendarToDate(days);
        return CALENDAR.get(Calendar.MONTH);
    }

    /**
     * Returns the year of the date represented by the given number of days from 1st January of the {@link #START_YEAR}.
     *
     * @param days the number of elapsed days
     * @return the year
     */
    public static synchronized int daysToYear(final int days) {

        setCalendarToDate(days);
        return CALENDAR.get(Calendar.YEAR);
    }

    /**
     * Tests whether a given year is a leap year.
     *
     * @param year the year
     * @return true if the year is a leap year, which is the case if it is divisible by 400, or by 4 and not by 100.
     */
    public static synchronized boolean isLeapYear(final int year) {

        CALENDAR.set(Calendar.YEAR, year);
        return CALENDAR.getActualMaximum(Calendar.DAY_OF_YEAR) > DAYS_IN_NON_LEAP_YEAR;
    }

    /**
     * Adds a given number of years to a date.
     *
     * @param days  a date represented as the number of days elapsed since 1st January of the {@link #START_YEAR}
     * @param years a number of years
     * @return the date obtained from adding the number of years to the date, represented in the same way as the date parameter
     */
    public static synchronized int addYears(final int days, final int years) {

        setCalendarToDate(days);
        CALENDAR.set(Calendar.YEAR, CALENDAR.get(Calendar.YEAR) + years);
        return millisToDays();
    }

    public static synchronized int subtractYears(final int days, final int years) {

        return addYears(days, -years);
    }

    public static synchronized Date addDays(final Date date, final int days) {

        return daysToDate(dateToDays(date) + days);
    }

    public static synchronized int dateToDay(final Date date) {

        setCalendarToDate(date);
        return CALENDAR.get(Calendar.DAY_OF_MONTH);
    }

    public static synchronized int dateToMonth(final Date date) {

        setCalendarToDate(date);
        return CALENDAR.get(Calendar.MONTH);
    }

    public static synchronized int dateToYear(final Date date) {

        setCalendarToDate(date);
        return CALENDAR.get(Calendar.YEAR);
    }

    /**
     * Calculates the elapsed time between the given dates expressed in days, with the result in years. The result is positive if the first date given is earlier than the second date.
     *
     * @param days1 the first date
     * @param days2 the second date
     * @return the difference between the dates in years
     */
    public static synchronized int differenceInDays(final int days1, final int days2) {

        return days2 - days1;
    }

    public static synchronized int differenceInDays(final Date date1, final Date date2) {

        return differenceInDays(dateToDays(date1), dateToDays(date2));
    }

    public static synchronized int differenceInYears(final int days1, final int days2) {

        return daysToYear(days2) - daysToYear(days1);
    }

    public static synchronized int differenceInYears(final Date date1, final Date date2) {

        return differenceInYears(dateToDays(date1), dateToDays(date2));
    }

    private static synchronized void setCalendarToDate(final int days) {

        final long date_in_millis = START_IN_MILLIS + days * MILLIS_PER_DAY;
        CALENDAR.setTimeInMillis(date_in_millis);
    }

    private static synchronized void setCalendarToDate(final Date date) {

        CALENDAR.setTimeInMillis(date.getTime());
    }

    private static synchronized int millisToDays() {

        // Get the elapsed time in milliseconds.
        final long elapsed_time_in_millis = CALENDAR.getTimeInMillis() - START_IN_MILLIS;

        return (int) (elapsed_time_in_millis / MILLIS_PER_DAY);
    }
}
