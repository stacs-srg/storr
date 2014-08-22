/*
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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general;

import java.util.List;
import java.util.Random;

/**
 * A general distribution of numbers between zero and one, the shape of which is controlled by a list of weights.
 * 
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class WeightedDistribution extends RestrictedDistribution<Double> {

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
        minimumReturnValue = getMinimumReturnValue();
        maximumReturnValue = getMaximumReturnValue();
    }

    /**
     * This distribution provides samples from the range 0.0-1.0, selected from a number of equally sized buckets with various weightings.
     * The ranges of the buckets are inferred from the number of weights supplied. When a sample is required, one of the buckets is randomly
     * selected according to the weights. On each call, the bucket is selected by generating a random number between 0.0 and 1.0, and picking
     * the first bucket whose cumulative probability exceeds that number.
     * 
     * @param weights the weights for the buckets
     * @param random the random number generator to be used
     * @param handleNoPermissableValueAsZero If set as true then the distribution will view that when it throws a NoPermissableValueException that it is akin to returning a value of 0 to the balance of the distribution - however a NoPermissableValueException will still be thrown.
     * @throws NegativeWeightException if any of the weights are negative
     */
    public WeightedDistribution(final int[] weights, final Random random, final boolean handleNoPermissableValueAsZero) throws NegativeWeightException {
        this(weights, random);
        if (handleNoPermissableValueAsZero) {
            zeroCount = 0;
            zeroCap = 1 / (maximumReturnValue - minimumReturnValue);
        }
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

    @Override
    public Double getSample(final double smallestPermissableReturnValue, final double largestPermissableReturnValue) throws NoPermissableValueException {

        // Checks if the distribution can provide a value that falls in the permissible return range, if not throws a NoPermissablevalueException
        if (smallestPermissableReturnValue >= maximumReturnValue || largestPermissableReturnValue <= minimumReturnValue) {
            // If at initialisation it has been detailed that the distribution should treat returning a non permissible value as if it has returned zero.
            if (zeroCount != -1) {
                int i;
                //  then it should remove the first 0 from the unusedSamplesValues list to simulate a returned zero value.
                if (unusedSampleValues.size() != 0 && (i = getIndexOfFirstZero(unusedSampleValues)) != -1) {
                    unusedSampleValues.remove(i);
                }
                // Else if no zero value is found then increment zero count to allow for the next zero return value to be prevented. 
                else {
                    zeroCount++;
                }
            }
            throw new NoPermissableValueException();
        }
        // Else if distribution can return a value in the permissible range
        else {
            // If unused sample values exist
            if (unusedSampleValues.size() != 0) {
                // then for each unused sample value
                int j = 0;
                for (Double d : unusedSampleValues) {
                    // If treatment of NoPermissableValues as zero and the zero count is non zero and the considered unused value is of a zero value. 
                    if (zeroCount > 0 && d.compareTo(zeroCap) <= 0) {
                        // then remove unused value and decrement zero count.
                        unusedSampleValues.remove(j);
                        zeroCount--;
                    }
                    // Else if the given d is in range
                    else if (inRange(d, smallestPermissableReturnValue, largestPermissableReturnValue)) {
                        // then remove from unused values list and return as sample value.
                        unusedSampleValues.remove(j);
                        return d;
                    }
                    j++;
                }
            }
        }
        // On reaching here all unused values have been deemed unsuitable.
        // Samples for new value.
        Double v = getSample();

        // If value is a zero value where there is a positive zeroCount and NoPermissableValues are treated as zero.
        if (zeroCount > 0 && v.compareTo(zeroCap) <= 0) {
            // Then decrement zero count and take a new sample.
            zeroCount--;
            v = getSample();
        }
        // Tests if value is in range.
        while (!inRange(v, smallestPermissableReturnValue, largestPermissableReturnValue)) {
            // If value is a zero value where there is a positive zeroCount and NoPermissableValues are treated as zero.  
            if (zeroCount > 0 && v.compareTo(zeroCap) <= 0) {
                // Then decrement zero count and take a new sample.
                zeroCount--;
                v = getSample();
            } else {
                // If not in range then adds to unused sample values list.
                unusedSampleValues.add(v);
            }
            // Takes a new sample.
            v = getSample();
        }
        // When a suitable value has been reached exits from while loop and returns value.

        return v;
    }

    // -------------------------------------------------------------------------------------------------------

    private int getIndexOfFirstZero(final List<Double> list) {
        int i = 0;
        for (Double d : list) {
            if (d.compareTo(zeroCap) <= 0) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static boolean inRange(final double d, final double earliestReturnValue, final double latestReturnValue) {
        return earliestReturnValue <= d && d <= latestReturnValue;
    }

    private double getMinimumReturnValue() {
        int count = 0;
        for (double d : cumulative_probabilities) {
            if (d == 0) {
                count++;
            } else {
                break;
            }
        }
        return count * bucket_size;
    }

    private double getMaximumReturnValue() {
        int count = 0;
        for (int i = cumulative_probabilities.length - 1; i >= 0; i--) {
            if (cumulative_probabilities[i] == 1) {
                count++;
            } else {
                break;
            }
        }
        return 1 - count * bucket_size;
    }

    private double[] generateCumulativeProbabilities(final int[] weights) throws NegativeWeightException {

        int cumulative_weight = 0;
        final int total_weight = sum(weights);
        final double inverse_total_weight = 1.0 / total_weight;

        final double[] cumulative_probabilities = new double[weights.length];
        for (int i = 0; i < cumulative_probabilities.length; i++) {
            final int weight = weights[i];
            if (weight < 0) {
                throw new NegativeWeightException("negative weight: " + weight);
            }
            cumulative_weight += weights[i];
            cumulative_probabilities[i] = cumulative_weight * inverse_total_weight;
        }

        return cumulative_probabilities;
    }

    private int firstBucketExceeding(final double bucket_selector) {

        for (int i = 0; i < cumulative_probabilities.length; i++) {
            if (cumulative_probabilities[i] > bucket_selector) {
                return i;
            }
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
