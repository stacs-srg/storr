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

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.util.ArrayIterator;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.util.Iterator;
import java.util.List;

/**
 * An analytic class to analyse the entire population.
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class PopulationAnalytics {

    private final CompactPopulation population;
    private static final int ONE_HUNDRED = 100;

    /**
     * Creates an analytic instance to analyse the entire population.
     * @param population - the population to analyse.
     */
    public PopulationAnalytics(final CompactPopulation population) {

        this.population = population;
    }

    public void printAllAnalytics() {

        final int size = population.size();
        final int number_males = population.numberMales();
        final int number_females = population.numberFemales();

        System.out.println("Population size = " + size);
        System.out.println("Number of males = " + number_males + " = " + String.format("%.1f", number_males / (double) size * ONE_HUNDRED) + "%");
        System.out.println("Number of males = " + number_females + " = " + String.format("%.1f", number_females / (double) size * ONE_HUNDRED) + "%");

    }

    // -------------------------------------------------------------------------------------------------------

    /**
     * Prints the dates of birth of all people.
     */
    public void printAllDatesOfBirth() {

        final Iterator<IPerson> people = new ArrayIterator(population.getPeopleArray());

        while (people.hasNext()) {
            CompactPerson p = (CompactPerson)people.next();
           System.out.println(DateManipulation.daysToString(p.getDateOfBirth()));
        }
    }

    /**
     * Prints the dates of death of all people.
     */
    public void printAllDatesOfDeath() {

        final Iterator<IPerson> people = new ArrayIterator(population.getPeopleArray());

        while (people.hasNext()) {
            final CompactPerson p = (CompactPerson)people.next();
            if (p.getDateOfDeath() >= 0) {
                System.out.println(DateManipulation.daysToString(p.getDateOfDeath()));
            }
        }
    }

    /**
     * Prints the dates of birth of children in a partnership.
     * @param partnership the partnership
     */
    public void printChildren(final CompactPartnership partnership) {

        if (partnership.getChildren() != null) {
            for (final int child : partnership.getChildren()) {
                final CompactPerson kid = population.getPerson(child);
                System.out.println("\t\tChild born: " + DateManipulation.daysToString(kid.getDateOfBirth()));

            }
        }
    }

    /**
     * Prints the details of partnerships and children for a given person.
     */
    public void printMarriages(final int person_index) {

        final CompactPerson p = population.getPerson(person_index);
        final List<CompactPartnership> partnerships = p.getPartnerships();
        if (partnerships != null) {
            for (final CompactPartnership partnership : partnerships) {
                if (partnership.getMarriageDate() > -1) {
                    final int partner_index = partnership.getPartner(person_index);
                    final CompactPerson partner = population.getPerson(partner_index);
                    System.out.println("\tMarriage to " + partner.getSex() + " born: " + DateManipulation.daysToString(partner.getDateOfBirth()) + " on " + DateManipulation.daysToString(partnership.getMarriageDate()));
                    printChildren(partnership);
                }
            }
        }
    }

    /**
      * Prints all significant dates for the population.
      */
    public void printAllDates() {

        int index = 0;
        final Iterator<IPerson> people = new ArrayIterator(population.getPeopleArray());

        while (people.hasNext()) {
            final CompactPerson p = (CompactPerson)people.next();

            System.out.print(p.getSex() + " Born: " + DateManipulation.daysToString(p.getDateOfBirth()));
            if (p.getDateOfDeath() >= 0) {
                System.out.print(", Died: " + DateManipulation.daysToString(p.getDateOfDeath()));
            }
            System.out.println();
            printMarriages(index++);
        }
    }
}
