/***************************************************************************
 *                                                                         *
 * digitising_scotland Library                                                             *
 * Copyright (C) 2005-2010 Distributed Systems Architecture Research Group *
 * University of St Andrews, Scotland                                      *
 * http://www-systems.cs.st-andrews.ac.uk/                                 *
 *                                                                         *
 * This file is part of digitising_scotland, a package of utility classes.                 *
 *                                                                         *
 * digitising_scotland is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation, either version 3 of the License, or       *
 * (at your option) any later version.                                     *
 *                                                                         *
 * digitising_scotland is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           *
 * GNU General Public License for more details.                            *
 *                                                                         *
 * You should have received a copy of the GNU General Public License       *
 * along with digitising_scotland.  If not, see <http://www.gnu.org/licenses/>.            *
 *                                                                         *
 ***************************************************************************/
package uk.ac.standrews.cs.digitising_scotland.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides support for various error message output.
 *
 * Based on code from the com.findnearyou package (C)
 * A. Dearle & R.Connor.
 *
 * @author Alan Dearle (al@cs.st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public final class ErrorHandling {

    private static final String SEPARATOR = " : ";
    private static final String HARD_ERROR_LABEL = "FATAL ERROR: ";

    private static boolean use_timestamp = false;

    protected static final Object SYNC = new Object(); // To allow Diagnostic calls to be synchronised with respect to these methods.

    protected static final String DEFAULT_DATE_FORMAT_PATTERN = "HHmm.ss.SSS yyyy-MM-dd";
    private static DateFormat dateformat = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);

    // -------------------------------------------------------------------------------------------------------

    /**
     * Sets whether timestamps should be used.
     * 
     * @param use_timestamp true if timestamps should be used
     */
    public static void setTimestampFlag(final boolean use_timestamp) {

        ErrorHandling.use_timestamp = use_timestamp;
    }

    /**
     * Returns the date format.
     * @return the date format
     */
    public static DateFormat getDateFormat() {

        return dateformat;
    }

    /**
     * Outputs an error message on the event bus and to standard error, if enabled.
     *
     * @param msg a descriptive message
     */
    public static void error(final Object... msg) {

        outputError(true, false, Diagnostic.getMethodInCallChain(), null, msg);
    }

    /**
     * Outputs an error message on the event bus and to standard error, if enabled.
     *
     * @param msg a descriptive message
     */
    public static void errorNoSource(final Object... msg) {

        outputError(true, false, null, null, msg);
    }

    /**
     * Outputs an error message to standard error, without sending on the event bus.
     *
     * @param msg a descriptive message
     */
    public static void errorNoEvent(final Object... msg) {

        outputError(false, false, Diagnostic.getMethodInCallChain(), null, msg);
    }

    /**
     * Outputs an error message to standard error regardless of whether local
     * reporting is enabled or not. The message is displayed without displaying
     * the source of the error, without the "ErrorHandling:" prefix and without sending
     * on the event bus.
     *
     * @param msg a descriptive message
     */
    public static void errorExplicitLocalReport(final Object... msg) {

        outputError(false, false, null, null, msg);
    }

    /**
     * Outputs an error message to standard error regardless of whether local
     * reporting is enabled or not, and performs a system exit. The message is
     * displayed without displaying the source of the error, without the
     * "ErrorHandling:" prefix and without sending on the event bus.
     *
     * @param msg a descriptive message
     */
    public static void hardErrorExplicitLocalReport(final Object... msg) {

        outputError(false, true, null, null, msg);
        hardExit();
    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Outputs an error message, and performs a system exit.
     *
     * @param msg a descriptive message
     */
    public static void hardError(final Object... msg) {

        outputError(true, true, Diagnostic.getMethodInCallChain(), null, msg);
        hardExit();
    }

    /**
     * Outputs an error message, and performs a system exit.
     *
     * @param msg a descriptive message
     */
    public static void hardErrorNoSource(final Object... msg) {

        outputError(true, true, null, null, msg);
        hardExit();
    }

    /**
     * Outputs an error message to standard error, without sending on the event bus, and performs a system exit.
     *
     * @param msg a descriptive message
     */
    public static void hardErrorNoEvent(final Object... msg) {

        outputError(false, true, Diagnostic.getMethodInCallChain(), null, msg);
        hardExit();
    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Outputs details of an exception, followed by a stack trace.
     * @param e the exception
     * @param msg a descriptive message
     */
    public static void exceptionError(final Throwable e, final Object... msg) {

        outputError(true, false, Diagnostic.getMethodInCallChain(), e, msg);
    }

    /**
     * Outputs details of an exception, followed by a stack trace, without sending on the event bus.
     * @param e the exception
     * @param msg a descriptive message
     */
    public static void exceptionErrorNoEvent(final Throwable e, final Object... msg) {

        outputError(false, false, Diagnostic.getMethodInCallChain(), e, msg);
    }

    /**
     * Outputs details of an exception, followed by a stack trace, and performs a system exit.
     * @param e the exception
     * @param msg a descriptive message
     */
    public static void hardExceptionError(final Throwable e, final Object... msg) {

        exceptionError(e, msg);
        hardExit();
    }

    /**
     * Outputs details of an exception, followed by a stack trace, without sending on the event bus, and performs a system exit.
     * @param e the exception
     * @param msg a descriptive message
     */
    public static void hardExceptionErrorNoEvent(final Throwable e, final Object... msg) {

        exceptionErrorNoEvent(e, msg);
        hardExit();
    }

    // -------------------------------------------------------------------------------------------------------

    private ErrorHandling() {

    }

    private static void hardExit() {

        System.exit(-1);
    }

    // -------------------------------------------------------------------------------------------------------

    private static void outputError(final boolean generate_event, final boolean fatal, final String source, final Throwable e, final Object... messages) {

        synchronized (SYNC) {

            final StringBuilder sb = new StringBuilder();
            if (fatal) {
                sb.append(HARD_ERROR_LABEL + " ");
            }

            if (use_timestamp) {
                //add current time to the error message
                sb.append("[");
                final Date now = new Date();
                final String formatted_date = dateformat.format(now);
                sb.append(formatted_date);
                sb.append("] ");
            }

            if (source != null && source.length() > 0) {
                sb.append(source);
                sb.append(SEPARATOR);
            }

            for (final Object message : messages) {
                /*if (message!= null)*/sb.append(message);
            }

            if (e != null) {
                sb.append(" Error: ");
                sb.append(e.getMessage());
                sb.append("\n");
                sb.append(getStackTrace(e));
            }

            final String buffer_as_string = sb.toString();

            System.err.println(buffer_as_string);
        }
    }

    private static String getStackTrace(final Throwable e) {

        final StringBuilder sb = new StringBuilder();

        sb.append(e);
        sb.append("\n");
        final StackTraceElement[] trace = e.getStackTrace();
        for (final StackTraceElement element : trace) {
            sb.append("\tat " + element + "\n");
        }

        return sb.toString();
    }
}
