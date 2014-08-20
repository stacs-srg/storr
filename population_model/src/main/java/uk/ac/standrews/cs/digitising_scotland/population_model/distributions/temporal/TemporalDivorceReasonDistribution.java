package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.organic.DivorceReason;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;

public class TemporalDivorceReasonDistribution extends TemporalDistribution<DivorceReason> {

    /**
     * Creates a divorce instigated by gender distribution.
     *
     * @param population The instance of the population which the distribution pertains to.
     * @param distributionKey The key specified in the config file as the location of the relevant file.
     * @param random the random number generator to be used
     */
    public TemporalDivorceReasonDistribution(final OrganicPopulation population, final String distributionKey, final Random random) {
        super(population, distributionKey, random, false);
    }

    @SuppressWarnings("magic numbers")
    @Override
    public DivorceReason getSample(final int date) {
        switch (getIntSample(date)) {
            case 0:
                return DivorceReason.ADULTERY;
            case 1:
                return DivorceReason.BEHAVIOUR;
            case 2:
                return DivorceReason.DESERTION;
            case 3:
                return DivorceReason.SEPARATION_WITH_CONSENT;
            case 4:
                return DivorceReason.SEPARATION;
            default:
                throw new RuntimeException("unexpected sample value");
        }
    }

    @Override
    public DivorceReason getSample() {
        return getSample(0);
    }

    @Override
    public DivorceReason getSample(final int date, final int earliestValue, final int latestValue) {
        return getSample(date);
    }

}