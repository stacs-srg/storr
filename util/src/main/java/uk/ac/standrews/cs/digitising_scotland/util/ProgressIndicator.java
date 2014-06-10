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
/**
 * Test test XXXXXXX XXXXXXXXXXX test
 */
/**
 * Test test test
 */
/**
 * This file is part of util.
 *
 * util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with util.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util;

/**
 * Created by graham on 14/05/2014.
 */
public abstract class ProgressIndicator {

    private final int number_of_updates;
    private int number_of_steps_since_last_update;
    private int total_steps;
    private int number_of_steps_completed;
    private int number_of_steps_per_update;

    private double proportion_complete;

    public ProgressIndicator(final int number_of_updates) {

        this.number_of_updates = number_of_updates;
        number_of_steps_since_last_update = 0;
    }

    public void setTotalSteps(final int total_steps) {

        this.total_steps = total_steps;
        number_of_steps_per_update = total_steps / number_of_updates;
    }

    public void progressStep() {

        number_of_steps_completed++;
        number_of_steps_since_last_update++;

        if (number_of_steps_since_last_update >= number_of_steps_per_update) {

            proportion_complete = (double) number_of_steps_completed / (double) total_steps;
            number_of_steps_since_last_update = 0;

            indicateProgress(proportion_complete);
        }
    }

    public double getProportionComplete() {

        return proportion_complete;
    }

    public abstract void indicateProgress(final double proportion_complete);
}
