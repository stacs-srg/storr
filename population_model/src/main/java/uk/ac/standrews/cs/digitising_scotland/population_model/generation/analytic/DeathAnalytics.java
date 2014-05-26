package uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic;

import uk.ac.standrews.cs.digitising_scotland.util.ArrayManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPerson;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

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

        for (CompactPerson p : population) {

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
