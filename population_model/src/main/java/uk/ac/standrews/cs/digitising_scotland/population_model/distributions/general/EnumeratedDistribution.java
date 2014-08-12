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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.StringWithCumulativeProbability;

/**
 * A distribution of strings controlled by specified probabilities.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class EnumeratedDistribution implements Distribution<String> {

    private static final double ALLOWABLE_TOTAL_WEIGHT_DISCREPANCY = 0.001;
    private static final Comparator<? super StringWithCumulativeProbability> ITEM_COMPARATOR = new ItemComparator();

    private final Random random;
    private StringWithCumulativeProbability[] items = null;

    protected EnumeratedDistribution(final Random random) {
        this.random = random;
    }

    /**
     * Creates an Enumerated distribution.
     * 
     * @param item_probabilities Take in a map of strings and double pairings to be used in the creation of the distribution.
     * @param random Takes in random for use in creation of distribution.
     * @throws IOException Thrown in the event of an IOException.
     * @throws InconsistentWeightException Thrown when the weights in the underlying distribution are found to be inconsistent.
     */
    public EnumeratedDistribution(final Map<String, Double> item_probabilities, final Random random) throws IOException, InconsistentWeightException {

        this(random);
        configureProbabilities(item_probabilities);
    }

    protected void configureProbabilities(final Map<String, Double> item_probabilities) throws InconsistentWeightException {

        items = new StringWithCumulativeProbability[item_probabilities.size()];
        double cumulative_probability = 0.0;
        int i = 0;

        for (final Map.Entry<String, Double> entry : item_probabilities.entrySet()) {

            cumulative_probability += entry.getValue();
            items[i++] = new StringWithCumulativeProbability(entry.getKey(), cumulative_probability);
        }

        if (Math.abs(cumulative_probability - 1) > ALLOWABLE_TOTAL_WEIGHT_DISCREPANCY) {
            throw new InconsistentWeightException();
        }
    }

    @Override
    public String getSample() {

        final double dice_throw = random.nextDouble();

        int sample_index = Arrays.binarySearch(items, new StringWithCumulativeProbability("", dice_throw), ITEM_COMPARATOR);

        // If the exact cumulative probability isn't matched - and it's very unlikely to be - the result of binarySearch() is (-(insertion point) - 1).
        if (sample_index < 0) {
            sample_index = -sample_index - 1;
        }
        if (sample_index >= items.length) {
            sample_index = items.length - 1;
        }

        return items[sample_index].getItem();
    }

    private static class ItemComparator implements Comparator<StringWithCumulativeProbability>, Serializable {

        @Override
        public int compare(final StringWithCumulativeProbability o1, final StringWithCumulativeProbability o2) {
            return o1.getCumulativeProbability().compareTo(o2.getCumulativeProbability());
        }
    }
}
