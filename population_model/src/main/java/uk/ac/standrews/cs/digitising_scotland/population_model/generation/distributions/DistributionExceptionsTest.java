package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

import org.junit.Test;

/**
 * Tests of distribution exceptions.
 *         
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class DistributionExceptionsTest {

    @Test(expected = NegativeDeviationException.class)
    public void negativeDeviation() throws NegativeDeviationException {

        new NormalDistribution(0, -1, new Random());
    }

    @Test(expected = NegativeWeightException.class)
    public void negativeWeight() throws NegativeWeightException {

        new WeightedDistribution(new int[]{1, -1, 1}, new Random());
    }
}
