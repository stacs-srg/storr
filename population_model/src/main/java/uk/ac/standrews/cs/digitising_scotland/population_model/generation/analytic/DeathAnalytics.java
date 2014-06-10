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

import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.util.Iterator;

/**
 * An analytic class to analyse the distribution of deaths.
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class DeathAnalytics {

    private final CompactPopulation population;
    private static final int MAX_AGE_AT_DEATH = 110;
    private static final int ONE_HUNDRED = 100;
    private final int[] age_at_death = new int[MAX_AGE_AT_DEATH]; // tracks age of death over population

    /**
     * Creates an analytic instance to analyse deaths in a population.
     * @param population - the population to analyse.
     */
    public DeathAnalytics(final CompactPopulation population) {

        this.population = population;
        analyseDeaths();
    }

    /**
     * Analyses deaths in the population.
     */
    private void analyseDeaths() {

        Iterator people = population.peopleIterator();

        while(people.hasNext()) {
            CompactPerson p = (CompactPerson)people.next();
            final int age_at_death_in_years = DateManipulation.differenceInYears(p.getDateOfBirth(), p.getDateOfDeath());
            if (age_at_death_in_years >= 0 && age_at_death_in_years < age_at_death.length) {
                age_at_death[age_at_death_in_years]++;
            }
        }
    }

    public void printAllAnalytics() {

        final int sum = ArrayManipulation.sum(age_at_death);

        System.out.println("Death distribution:");
        for (int i = 1; i < age_at_death.length; i++) {
            System.out.println("\tDeaths at age: " + i + " = " + age_at_death[i] + " = " + String.format("%.1f", age_at_death[i] / (double) sum * ONE_HUNDRED) + "%");
        }
    }
}
