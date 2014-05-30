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

import uk.ac.standrews.cs.nds.util.DiagnosticLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling.DEFAULT_DATE_FORMAT_PATTERN;

/**
 * <p>Provides support for various diagnostic output.</p>
 *
 * <p>A global threshold diagnostic level may be set by the user; the default value
 * is NONE, the highest level. Each call to produce diagnostic output is
 * parameterised by a diagnostic level. The output is only actually generated if
 * the given level is higher than or equal to the current global threshold
 * level. For example, if the global threshold is set to FULL then all output
 * will be generated, while if the global threshold is set to NONE then only
 * calls that also specify the level NONE will produce output.</p>
 *
 * @author Alan Dearle (al@cs.st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public final class Diagnostic {

    private static final String RIGHT_TIMESTAMP_DELIMITER = "]";
    private static final String LEFT_TIMESTAMP_DELIMITER = "[";

    private static boolean use_timestamp = false;
    private static boolean use_timestamp_delimiter = true;

    private static volatile DateFormat date_format = new SimpleDateFormat(DEFAULT_DATE_FORMAT_PATTERN);

    private static final String SEPARATOR = " : ";

    private static final String DIAGNOSTIC_CLASS_NAME = Diagnostic.class.getName();
    private static final String ERROR_CLASS_NAME = ErrorHandling.class.getName();

    private static volatile DiagnosticLevel threshold = DiagnosticLevel.NONE;
    private static Set<String> ignored_packages = new HashSet<String>();

    /**
     * Prevent instantiation of utility class.
     */
    private Diagnostic() {

    }

    /**
     * Outputs trace information if the specified level is equal or higher to the current reporting threshold,
     * without including the source location.
     *
     * @param msg a descriptive message
     */
    public static void traceNoSource(final Object... msg) {

        outputTrace(threshold, true, false, msg);
    }

    /**
     * Returns information on the most recent user method in the current call chain.
     *
     * @return returns information on the most recent user method in the current call chain
     */
    public static String getMethodInCallChain() {

        // Get a stack trace.
        final StackTraceElement[] trace = new Exception().getStackTrace();

        // Ignore calls within Diagnostic or ErrorHandling class.
        // Start from 1 - depth 0 is this method.
        for (int i = 1; i < trace.length; i++) {

            final StackTraceElement call = trace[i];
            final String calling_class_name = call.getClassName();

            if (!calling_class_name.equals(DIAGNOSTIC_CLASS_NAME) && !calling_class_name.equals(ERROR_CLASS_NAME)) { return calling_class_name + "::" + call.getMethodName(); }
        }

        return "";
    }

    private static void outputTrace(final DiagnosticLevel level, final boolean new_line, final boolean display_source, final Object... messages) {

        if (level.meetsThreshold(threshold)) {

            final String calling_method_name = getMethodInCallChain();

            if (!methodInIgnoredPackage(calling_method_name)) {

                final StringBuilder buffer = new StringBuilder();

                if (use_timestamp) {

                    // Add current time to the error message.
                    if (use_timestamp_delimiter) {
                        buffer.append(LEFT_TIMESTAMP_DELIMITER);
                    }

                    final Date now = new Date();

                    // DateFormat instances are not thread-safe.
                    synchronized (date_format) {
                        buffer.append(date_format.format(now));
                    }

                    if (use_timestamp_delimiter) {
                        buffer.append(RIGHT_TIMESTAMP_DELIMITER);
                    }
                    buffer.append(" ");
                }

                if (display_source) {

                    buffer.append(calling_method_name);
                    buffer.append(SEPARATOR);
                }

                for (final Object message : messages) {
                    if (message != null) {
                        buffer.append(message);
                    }
                }

                final String buffer_as_string = buffer.toString();

                // Synchronise with respect to the ErrorHandling methods too.
                synchronized (ErrorHandling.SYNC) {

                    System.out.print(buffer_as_string);
                    if (new_line) {
                        System.out.println();
                    }
                    System.out.flush();
                }
            }
        }
    }

    private static boolean methodInIgnoredPackage(final String calling_method_name) {

        for (final String ignore : ignored_packages) {

            if (calling_method_name.startsWith(ignore)) { return true; }
        }

        return false;
    }
}
