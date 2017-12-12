/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.interfaces;

import uk.ac.standrews.cs.storr.impl.LXP;

/**
 * Provides filtering over an ILXPInputStream to populate an ILXPOutputStream containing records selected by the predicate select.
 * Created by al on 29/04/2014.
 */
public interface IFilter<T extends LXP> {

    /**
     * @return the ILXPInputStream over which filtering is being performed.
     */
    IInputStream<T> getInput();

    /**
     * @return the ILXPOutputStream to which selected records are being written
     */
    IOutputStream<T> getOutput();

    /**
     * Determines whether a record from the input stream should be written to the output stream
     *
     * @param record to be selected or otherwise
     * @return true if the record is to be chosen for copying to the output stream.
     */
    boolean select(T record);
}
