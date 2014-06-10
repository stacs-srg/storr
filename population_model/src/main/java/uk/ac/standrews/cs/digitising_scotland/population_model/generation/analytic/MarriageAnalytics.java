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
 * An analytic class to analyse the distribution of marriages.
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class MarriageAnalytics {

    private final CompactPopulation population;
    private static final int MAX_MARRIAGES = 10;
    private static final int ONE_HUNDRED = 100;
    private final int[] count_marriages = new int[MAX_MARRIAGES]; // tracks marriage size

    /**
     * Creates an analytic instance to analyse marriages in a population.
     * @param population - the population to analyse.
     */
    public MarriageAnalytics(final CompactPopulation population) {

        this.population = population;
        analyseMarriages();
    }

    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(count_marriages);

        System.out.println("Male mariage sizes:");
        System.out.println("\t unmarried: " + count_marriages[0]);
        for (int i = 1; i < count_marriages.length; i++) {
            if (count_marriages[i] != 0) {
                System.out.println("\t Married " + i + " times: " + count_marriages[i] + " = " + String.format("%.1f", count_marriages[i] / (double) sum * ONE_HUNDRED) + "%");
            }
        }
    }

    /**
     * Analyses marriages for the population.
     */
    public void analyseMarriages() {

        Iterator people = new ArrayIterator(population.getPeopleArray());

        while(people.hasNext()) {
            CompactPerson p = (CompactPerson)people.next();

            if (p.isMale()) { // only look at Makes to avoid counting marriages twice. TODO is this OK? Not sure!
                final List<CompactPartnership> partnerships = p.getPartnerships();
                if (partnerships == null) {
                    count_marriages[0]++;
                } else {
                    count_marriages[partnerships.size()]++;
                }
            }
        }
    }
}
