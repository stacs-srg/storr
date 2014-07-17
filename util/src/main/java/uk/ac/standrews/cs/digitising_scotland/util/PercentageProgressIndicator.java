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
 * Created by graham on 28/05/2014.
 */
public class PercentageProgressIndicator extends ProgressIndicator {

    private static final int HUNDRED_PERCENT = 100;

    public PercentageProgressIndicator(final int number_of_progress_updates) {
        super(number_of_progress_updates);
    }

    public void indicateProgress(final double proportion_complete) {

        Diagnostic.traceNoSource(Math.round(proportion_complete * HUNDRED_PERCENT) + "%");
    }
}
