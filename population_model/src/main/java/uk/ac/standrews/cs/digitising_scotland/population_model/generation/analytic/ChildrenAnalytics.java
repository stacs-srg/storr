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
package uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic;

import uk.ac.standrews.cs.digitising_scotland.util.ArrayIterator;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.util.Iterator;
import java.util.List;

/**
 * An analytic class to analyse the distribution of children.
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 *
 */
public class ChildrenAnalytics {

    private final CompactPopulation population;
    private static final int MAX_CHILDREN = 10;
    private static final int ONE_HUNDRED = 100;
    private final int[] count_kids_per_marriage = new int[MAX_CHILDREN]; // tracks marriage size

    /**
     * Creates an analytic instance to analyse children in a population.
     * @param population - the population to analyse.
     */
    public ChildrenAnalytics(final CompactPopulation population) {

        this.population = population;
        analyseChildren();
    }

    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(count_kids_per_marriage);

        System.out.println("Chilren per marriage mariage sizes:");
        for (int i = 0; i < count_kids_per_marriage.length; i++) {
            if (count_kids_per_marriage[i] != 0) {
                System.out.println("\t" + count_kids_per_marriage[i] + " Marriages with " + i + " children" + " = " + String.format("%.1f", count_kids_per_marriage[i] / (double) sum * ONE_HUNDRED) + "%");
            }
        }
    }

    /**
     * Analyses Children of marriages for the population.
     */
    public void analyseChildren() {

        Iterator people = new ArrayIterator(population.getPeopleArray());

        while(people.hasNext()) {
            CompactPerson p = (CompactPerson)people.next();

            final List<CompactPartnership> partnerships = p.getPartnerships();
            if (partnerships != null) {

                for (final CompactPartnership partnership : partnerships) {
                    if (partnership.getMarriageDate() > -1 && partnership.getChildren() != null) {
                        final int len = partnership.getChildren().size();
                        count_kids_per_marriage[len]++;
                    }
                }
            }
        }
    }
}
