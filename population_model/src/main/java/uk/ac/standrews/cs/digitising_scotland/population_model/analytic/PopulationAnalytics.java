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
package uk.ac.standrews.cs.digitising_scotland.population_model.analytic;

import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;
import java.util.List;

/**
 * An analytic class to analyse the entire population.
 *
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class PopulationAnalytics {

    private final IPopulation population;
    private static final int ONE_HUNDRED = 100;

    /**
     * Creates an analytic instance to analyse the entire population.
     *
     * @param population the population to analyse
     */
    public PopulationAnalytics(final IPopulation population) {

        this.population = population;
    }

    /**
     * Prints out all analyses.
     *
     * @throws Exception if the population size cannot be accessed
     */
    public void printAllAnalytics() throws Exception {

        final int size = population.getNumberOfPeople();
        final int number_males = countMales();
        final int number_females = countFemales();

        System.out.println("Population size = " + size);
        System.out.println("Number of males = " + number_males + " = " + String.format("%.1f", number_males / (double) size * ONE_HUNDRED) + '%');
        System.out.println("Number of females = " + number_females + " = " + String.format("%.1f", number_females / (double) size * ONE_HUNDRED) + '%');

        printAllBirthDates();
        printAllDeathDates();
        printAllDates();
    }

    private int countMales()  {

        int count = 0;
        for (final IPerson person : population.getPeople()) {
            if (person.getSex() == IPerson.MALE) {
                count++;
            }
        }
        return count;
    }

    private int countFemales()  {

        int count = 0;
        for (final IPerson person : population.getPeople()) {
            if (person.getSex() == IPerson.FEMALE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Prints the dates of birth of all people.
     */
    public void printAllBirthDates()  {

        for (final IPerson person : population.getPeople()) {
            printBirthDate(person);
            System.out.println();
        }
    }

    private static void printBirthDate(final IPerson person) {

        System.out.print(DateManipulation.formatDate(person.getBirthDate()));
    }

    /**
     * Prints the dates of death of all people.
     */
    public void printAllDeathDates() {

        for (final IPerson person : population.getPeople()) {
            printDeathDate(person);
            System.out.println();
        }
    }

    private static void printDeathDate(final IPerson person) {

        final Date death_date = person.getDeathDate();
        if (death_date != null) {
            System.out.print(DateManipulation.formatDate(death_date));
        }
    }

    /**
     * Prints the dates of birth of child_ids in a partnership.
     *
     * @param partnership the partnership
     */
    public void printChildren(final IPartnership partnership) {

        if (partnership.getChildIds() != null) {
            for (final int child_index : partnership.getChildIds()) {

                final IPerson child = population.findPerson(child_index);
                System.out.println("\t\tChild born: " + DateManipulation.formatDate(child.getBirthDate()));
            }
        }
    }

    /**
     * Prints the details of partnerships and child_ids for a given person.
     * @param person the person
     */
    @SuppressWarnings("FeatureEnvy")
    public void printPartnerships(final IPerson person)  {

        final List<Integer> partnership_ids = person.getPartnerships();
        if (partnership_ids != null) {
            for (final int partnership_id : partnership_ids) {

                final IPartnership partnership = population.findPartnership(partnership_id);

                final int partner_id = partnership.getPartnerOf(person.getId());
                final IPerson partner = population.findPerson(partner_id);
                System.out.println("\tPartner born: " + DateManipulation.formatDate(partner.getBirthDate()));

                final Date marriage_date = partnership.getMarriageDate();
                if (marriage_date != null) {
                    System.out.println("\tMarriage on " + DateManipulation.formatDate(marriage_date));
                }

                printChildren(partnership);
            }
        }
    }

    /**
     * Prints all significant dates for the population.
     */
    public void printAllDates()  {

        for (final IPerson person : population.getPeople()) {

            System.out.print(person.getSex() + " Born: ");
            printBirthDate(person);
            System.out.print(", Died: ");
            printDeathDate(person);
            System.out.println();
            printPartnerships(person);
        }
    }
}
