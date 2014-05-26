package uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

import java.util.Iterator;

/**
 * An analytic class to analyse which members of the population have parents.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 */
public class ParentAnalytics {

    private final CompactPopulation population;
    private static final int DATE_GRAIN = 10;
    private int[] incomers_over_time_date_grain;

    /**
     * Creates an analytic instance to analyse parents of a population.
     * @param population - the population to analyse.
     */
    public ParentAnalytics(final CompactPopulation population) {

        this.population = population;
        analyseParents();
    }

    /**
     * Analyses the parents of a population.
     */
    private void analyseParents() {

        final int population_duration = DateManipulation.differenceInYears(population.getFirstDate(), population.getLastDate());
        final int num_buckets = population_duration / DATE_GRAIN; // number of DATE_GRAIN year blocks of time in duration
        incomers_over_time_date_grain = new int[num_buckets + 1]; // use these to store the incomers over time

        final Iterator<CompactPerson> people = population.iterator();

        while (people.hasNext()) {

            final CompactPerson p = people.next();
            if (!p.hasParents()) {
                // put them in appropriate bucket indexed from start of time
                incomers_over_time_date_grain[DateManipulation.differenceInYears(population.getFirstDate(), p.getDateOfBirth()) / DATE_GRAIN]++;
            }
        }
    }

    public void printAllAnalytics() {

        int date = population.getFirstDate();

        System.out.println("Number of incomers at 10 year granularity:");
        for (int i = 1; i < incomers_over_time_date_grain.length; i++) {
            System.out.println(DateManipulation.daysToYear(date) + ": " + incomers_over_time_date_grain[i]);
            date = DateManipulation.addYears(date, DATE_GRAIN);
        }
    }
}
