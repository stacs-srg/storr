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
package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.util.Random;

/**
 * A general distribution of numbers between zero and one, the shape of which is controlled by a list of weights.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class WeightedDistribution implements Distribution<Double> {

    private final Random random;
    private final double[] cumulative_probabilities;
    private final double bucket_size;

    /**
     * This distribution provides samples from the range 0.0-1.0, selected from a number of equally sized buckets with various weightings.
     * The ranges of the buckets are inferred from the number of weights supplied. When a sample is required, one of the buckets is randomly
     * selected according to the weights. On each call, the bucket is selected by generating a random number between 0.0 and 1.0, and picking
     * the first bucket whose cumulative probability exceeds that number.
     * 
     * The sample returned is picked from a uniform random distribution within the selected bucket.
     * 
     * @param weights the weights for the buckets
     * @param random the random number generator to be used
     * @throws NegativeWeightException if any of the weights are negative
     */
    public WeightedDistribution(final int[] weights, final Random random) throws NegativeWeightException {

        this.random = random;
        bucket_size = 1.0 / weights.length;
        cumulative_probabilities = generateCumulativeProbabilities(weights);
    }

    /*
     The table below shows an example where the weights 1, 4 and 1 are supplied to the constructor. This gives 3 buckets of equal size.

     So in the example the first and third buckets are each selected on around 1/6 of calls, and the second bucket on 2/3 of calls.

     Range         Weight  Cumulative Probability
     0.0-  0.333   1       0.1667
     0.333-0.667   4       0.8333
     0.667-1.0     1       1.0
    */

    @Override
    public Double getSample() {

        final double bucket_selector = random.nextDouble();
        final int bucket_index = firstBucketExceeding(bucket_selector);

        final double position_within_selected_bucket = random.nextDouble();
        return (bucket_index + position_within_selected_bucket) * bucket_size;
    }

    // -------------------------------------------------------------------------------------------------------

    private double[] generateCumulativeProbabilities(final int[] weights) throws NegativeWeightException {

        int cumulative_weight = 0;
        final int total_weight = sum(weights);
        final double inverse_total_weight = 1.0 / total_weight;

        final double[] cumulative_probabilities = new double[weights.length];
        for (int i = 0; i < cumulative_probabilities.length; i++) {
            final int weight = weights[i];
            if (weight < 0) { throw new NegativeWeightException("negative weight: " + weight); }
            cumulative_weight += weights[i];
            cumulative_probabilities[i] = cumulative_weight * inverse_total_weight;
        }

        return cumulative_probabilities;
    }

    private int firstBucketExceeding(final double bucket_selector) {

        for (int i = 0; i < cumulative_probabilities.length; i++) {
            if (cumulative_probabilities[i] > bucket_selector) { return i; }
        }
        return cumulative_probabilities.length - 1;
    }

    protected static int sum(final int[] array) {

        int total = 0;
        for (final int weight : array) {
            total += weight;
        }
        return total;
    }
}
