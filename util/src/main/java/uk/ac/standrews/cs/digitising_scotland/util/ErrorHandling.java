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

    /**
     * Outputs an error message on the event bus and to standard error, if enabled.
     *
     * @param msg a descriptive message
     */
    public static void error(final Object... msg) {

        outputError(false, Diagnostic.getMethodInCallChain(), null, msg);
    }

    /**
     * Outputs details of an exception, followed by a stack trace.
     * @param e the exception
     * @param msg a descriptive message
     */
    public static void exceptionError(final Throwable e, final Object... msg) {

        outputError(false, Diagnostic.getMethodInCallChain(), e, msg);
    }

    private ErrorHandling() {

    }

    private static void outputError(final boolean fatal, final String source, final Throwable e, final Object... messages) {

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
