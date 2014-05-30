package uk.ac.standrews.cs.digitising_scotland.population_model.generation.distributions;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;

/**
 * A distribution of strings controlled by specified probabilities.
 * 
 * @author Alan Dearle (alan.dearle@st-andrews.ac.uk)
 * @author Graham Kirby (graham.kirby@st-andrews.ac.uk)
 */
public class EnumeratedDistribution implements Distribution<String> {

    private static final double ALLOWABLE_TOTAL_WEIGHT_DISCREPANCY = 0.001;
    private static final Comparator<? super StringWithCumulativeProbability> ITEM_COMPARATOR = new ItemComparator();

    private Random random;
    private StringWithCumulativeProbability[] items = null;

    protected EnumeratedDistribution(final Random random) {
        this.random = random;
    }

    public EnumeratedDistribution(final Map<String, Double> item_probabilities, final Random random) throws IOException, InconsistentWeightException {

        this(random);
        configureProbabilities(item_probabilities);
    }

    protected void configureProbabilities(final Map<String, Double> item_probabilities) throws InconsistentWeightException {

        items = new StringWithCumulativeProbability[item_probabilities.size()];
        double cumulative_probability = 0.0;
        int i = 0;

        for (Map.Entry<String, Double> entry : item_probabilities.entrySet()) {

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
