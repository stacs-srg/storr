/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.nds.util.ErrorHandling;

/**
 * Generates sequential unique IDs. There is no built-in mechanism for persisting the last issued
 * ID across executions. If this is required, {@link #setId(int)} should be used to set the next
 * ID retrieved from a file, database etc.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class IDFactory {

    private static int id;

    static {
        try {
            resetId();
        }
        catch (final Throwable e) {
            ErrorHandling.exceptionError(e, "Cannot recover persistent id");
        }
    }

    /**
     * Gets the next ID.
     * @return the next ID
     */
    public static synchronized int getNextID() {

        return id++;
    }

    /**
     * Sets the next ID to one.
     */
    public static void resetId() {

        setId(1);
    }

    /**
     * Sets the next ID.
     * @param id the next ID to be allocated
     */
    public static synchronized void setId(final int id) {

        IDFactory.id = id;
    }
}
