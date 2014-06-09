package uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic;

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

        Iterator people = population.peopleIterator();

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
