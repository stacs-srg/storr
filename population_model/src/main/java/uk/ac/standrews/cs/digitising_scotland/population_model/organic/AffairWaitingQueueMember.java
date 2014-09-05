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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalEnumDistribution;

/**
 * Provides an orderable queue element to be used in the affair waiting queue.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class AffairWaitingQueueMember implements Comparable<AffairWaitingQueueMember> {

    private OrganicPerson person;
    private int affairDay;
    private boolean interMarital;

    private static final Enum<?>[] AFFAIR_WITH_MARRIED_OR_SINGLE_FAMILY_TYPE_ARRAY = { FamilyType.SINGLE_AFFAIR, FamilyType.INTER_MARITAL_AFFAIR };
    private static TemporalEnumDistribution<FamilyType> affairWithMarriedOrSingleDistribution;

    /**
     * Initialises the distribution used to discern if the affair is to be with a single third party of a married third party.
     * 
     * @param population The OrganicPopulation instance.
     * @param distributionKey The key corresponding to the file path in the config file.
     * @param random The random number generator to be used.
     */
    public static void initialiseAffairWithMarrieadOrSingleDistribution(final OrganicPopulation population, final String distributionKey, final Random random) {
        affairWithMarriedOrSingleDistribution = new TemporalEnumDistribution<FamilyType>(population, distributionKey, random, AFFAIR_WITH_MARRIED_OR_SINGLE_FAMILY_TYPE_ARRAY);
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

    /**
     * Returns the OrganicPerson.
     * 
     * @return The OrganicPerson.
     */
    public OrganicPerson getPerson() {
        return person;
    }

    /**
     * Sets the OrganicPerson to the given person.
     * 
     * @param person The given person.
     */
    public void setPerson(final OrganicPerson person) {
        this.person = person;
    }

    /**
     * Checks if the affair is to be inter marital.
     * 
     * @return Boolean value indicating if the affair is to be inter marital.
     */
    public boolean isInterMarital() {
        return interMarital;
    }

    /**
     * Sets the boolean to the given value.
     * 
     * @param interMarital The given value.
     */
    public void setInterMarital(final boolean interMarital) {
        this.interMarital = interMarital;
    }

    /**
     * Returns the day on which the affair will occur.
     * 
     * @return The day on which the affair will occur.
     */
    public int getAffairDay() {
        return affairDay;
    }

    /**
     * Sets the affair day to the given day.
     * 
     * @param affairDay The given day.
     */
    public void setAffairDay(final int affairDay) {
        this.affairDay = affairDay;
    }

}
