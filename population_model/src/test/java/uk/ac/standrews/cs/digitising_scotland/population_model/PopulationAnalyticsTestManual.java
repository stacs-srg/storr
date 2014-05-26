package uk.ac.standrews.cs.digitising_scotland.population_model;

import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.ChildrenAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.DeathAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.MarriageAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.ParentAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.analytic.PopulationAnalytics;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.CompactPopulation;

/**
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * 
 * Tests of population operations.
 */
public class PopulationAnalyticsTestManual {

    public static void main(final String[] args) throws NegativeDeviationException, NegativeWeightException {

        final int population_size = 1000;
        final CompactPopulation population = new CompactPopulation(population_size);

        new PopulationAnalytics(population).printAllAnalytics();
        new MarriageAnalytics(population).printAllAnalytics();
        new ChildrenAnalytics(population).printAllAnalytics();
        new DeathAnalytics(population).printAllAnalytics();
        new ParentAnalytics(population).printAllAnalytics();
    }
}
