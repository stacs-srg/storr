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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

import java.util.Random;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalAffairWithMarriedOrSingleDistribution;

/**
 * Provides an orderable queue element to be used in the affair waiting queue.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AffairWaitingQueueMember implements Comparable<AffairWaitingQueueMember> {

    private OrganicPerson person;
    private int affairDay;
    private boolean interMarital;

    private static TemporalAffairWithMarriedOrSingleDistribution affairWithMarriedOrSingleDistribution;

    /**
     * Initialises the distribution used to discern if the affair is to be with a single third party of a married third party.
     * 
     * @param population The OrganicPopulation instance.
     * @param distributionKey The key corresponding to the file path in the config file.
     * @param random The random number generator to be used.
     */
    public static void initialiseAffairWithMarrieadOrSingleDistribution(final OrganicPopulation population, final String distributionKey, final Random random) {
        affairWithMarriedOrSingleDistribution = new TemporalAffairWithMarriedOrSingleDistribution(population, distributionKey, random);
    }

    /**
     * Constructs an AffairWaitingQueueMember which can then then be placed into the affairs waiting queue.
     * 
     * @param person The instance of the relevant OrganicPerson.
     * @param affairDay The day on which the affair is to occur.
     */
    public AffairWaitingQueueMember(final OrganicPerson person, final int affairDay) {
        this.person = person;
        this.affairDay = affairDay;
        switch (affairWithMarriedOrSingleDistribution.getSample(affairDay)) {
            case SINGLE_AFFAIR:
                this.interMarital = false;
                break;
            case INTER_MARITAL_AFFAIR:
                this.interMarital = true;
                break;
            default:
                break;
        }
    }

    @Override
    public int compareTo(final AffairWaitingQueueMember o) {
        if (affairDay < o.affairDay) {
            return -1;
        } else if (affairDay == o.affairDay) {
            return 0;
        } else {
            return 1;
        }
    }

    public OrganicPerson getPerson() {
        return person;
    }

    public void setPerson(OrganicPerson person) {
        this.person = person;
    }

    public boolean isInterMarital() {
        return interMarital;
    }

    public void setInterMarital(boolean interMarital) {
        this.interMarital = interMarital;
    }

    public int getAffairDay() {
        return affairDay;
    }

    public void setAffairDay(int affairDay) {
        this.affairDay = affairDay;
    }

}
