/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputFormatException is called in the instance of an input being malformed or of the wrong type.
 */
public class InputFormatException extends Exception {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 742441592450510717L;

    /**
     * Instantiates a new input format exception.
     *
     * @param errorMessage the error message
     * @param parent parent class name to enable logging of where error originated
     */
    public InputFormatException(final String errorMessage, final Class parent) {

        super(errorMessage);
        Logger logger = LoggerFactory.getLogger(parent);
        logger.error(this.toString());
        for (StackTraceElement trace : this.getStackTrace()) {
            logger.error(trace.toString());

        }
    }

}
