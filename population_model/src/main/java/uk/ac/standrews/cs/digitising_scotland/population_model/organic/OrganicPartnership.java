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
package uk.ac.standrews.cs.digitising_scotland.population_model.organic;

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceAgeForFemaleDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceAgeForMaleDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.DivorceInstigatedByGenderDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.UniformDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.util.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by victor on 08/07/14.
 *
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class OrganicPartnership implements IPartnership {

    private static Random random = RandomFactory.getRandom();
    private static DivorceInstigatedByGenderDistribution divorceInstigatedByGenderDistribution = new DivorceInstigatedByGenderDistribution(random);
    private static DivorceAgeForMaleDistribution divorceAgeForMaleDistribution = new DivorceAgeForMaleDistribution(random);
    private static DivorceAgeForFemaleDistribution divorceAgeForFemaleDistribution = new DivorceAgeForFemaleDistribution(random);

    // TODO Make distributions for these
    private static final int MAX_NUMBER_CHILDREN = 5;
    private static final int MAX_TIME_INTO_RELATIONSHIP_UNTIL_FIRST_BIRTH = 5;
    private static UniformDistribution numberOfChildrenDistribution = new UniformDistribution(0, MAX_NUMBER_CHILDREN, random);
    private static UniformDistribution daysIntoPartnershipForBirthDistribution = new UniformDistribution(0, (int) (MAX_TIME_INTO_RELATIONSHIP_UNTIL_FIRST_BIRTH * OrganicPopulation.DAYS_PER_YEAR), random);

    private Integer id;
    private Integer husband;
    private Integer wife;
    private OrganicTimeline timeline;
    private int marriageDay;
    private List<Integer> childrenIds = new ArrayList<Integer>();
    private boolean on;

    private boolean isOn() {
        return on;
    }

    /**
     * Sets partnership on value to on indicating if the relationship has concluded.
     */
    public void turnOn() {
        on = true;
    }

    /**
     * Sets partnership on value to off indicating if the relationship has concluded.
     */
    public void turnOff() {
        on = false;
    }

    /**
     * Constructs partnership objects and returns both the partnership in the first field of the array and the partnerships first child in the second.
     *
     * @param id          Specifies the ID of the OrganicPartnership.
     * @param husband     The OrganicPerson object representing the husband.
     * @param wife        The OrganicPerson object representing the wife.
     * @param marriageDay The marriage day specified in days since 1/1/1600
     * @return Returns an object array of size 2, where at index 0 can be found the newly constructed OrganicPartnership and at index 1 the partnerships child (if no child then value is null)
     */
    public static Object[] createOrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int marriageDay) {
        Object[] returns = new Object[2];
        // Contains OrganicPartnership object
        OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, marriageDay);
        // Contains OrganicPerson object aka the child - if no child returns null
        returns[1] = partnership.createPartnershipTimeline(husband, wife);
        returns[0] = partnership;
        return returns;
    }

    private OrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int marriageDay) {
        this.id = id;
        this.husband = husband.getId();
        this.wife = wife.getId();
        this.marriageDay = marriageDay;
        this.turnOn();
    }

    private OrganicPerson createPartnershipTimeline(final OrganicPerson husband, final OrganicPerson wife) {

        // TODO Correctly populate timeline
        timeline = new OrganicTimeline(marriageDay);

        // Decide if/when relationship terminates
        switch (divorceInstigatedByGenderDistribution.getDefinedSample()) {
            case MALE:
                // get male age at divorce
                int maleDivorceAgeInDays;
                do {
                    maleDivorceAgeInDays = divorceAgeForMaleDistribution.getSample() + husband.getBirthDay();
                }
                while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), marriageDay), maleDivorceAgeInDays));
                timeline.addEvent(maleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE));
                timeline.setEndDate(maleDivorceAgeInDays);
                break;
            case FEMALE:
                // get female age at divorce
                int femaleDivorceAgeInDays;
                do {
                    femaleDivorceAgeInDays = divorceAgeForFemaleDistribution.getSample() + wife.getBirthDay();
                }
                while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), marriageDay), femaleDivorceAgeInDays));
                timeline.addEvent(femaleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE));
                timeline.setEndDate(femaleDivorceAgeInDays);
                break;
            case NO_DIVORCE:
                // If not then added earliest death date
                int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
                timeline.addEvent(firstPartnersDeathDate, new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH));
                timeline.setEndDate(firstPartnersDeathDate);
                break;
            default:
                break;
        }

        // Decide on a number of children for relationship
        // Will be from a distribution - just keeping things simple for now
        int numberOfChildren = 1;

        int dayOfBirth = daysIntoPartnershipForBirthDistribution.getSample();
        int lastChildDay = 0;
        OrganicPerson child = null;

        int count = 0;
        // FIXME Find a solution that dosn't need a magic number
        while (lastChildDay == 0 && count < 100) {
            if (PopulationLogic.earliestAcceptableBirthDate(marriageDay, 0) < dayOfBirth + marriageDay &&
                    PopulationLogic.parentsHaveSensibleAgesAtChildBirth(husband.getBirthDay(), husband.getDeathDay(),
                            wife.getBirthDay(), wife.getDeathDay(), dayOfBirth + marriageDay)) {
                lastChildDay = dayOfBirth + marriageDay;
                child = new OrganicPerson(IDFactory.getNextID(), lastChildDay, false);
                childrenIds.add(child.getId());
                timeline.addEvent(lastChildDay, new OrganicEvent(EventType.BIRTH));
            }

            count++;
        }


        return child;
    }

    private int dateOfFirstPartnersDeath(final int husbandDeath, final int wifeDeath) {
        if (husbandDeath < wifeDeath) {
            return husbandDeath;
        } else {
            return wifeDeath;
        }
    }

    /**
     * Returns the timeline of the partnership.
     *
     * @return Returns the timeline of the partnership
     */
    public OrganicTimeline getTimeline() {
        return timeline;
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getFemalePartnerId() {
        return wife;
    }

    @Override
    public int getMalePartnerId() {
        return husband;
    }

    @Override
    public int getPartnerOf(final int id) {

        if (id == husband) {
            return wife;
        } else if (id == wife) {
            return husband;
        } else {
            return -1;
        }
    }

    @Override
    public Date getMarriageDate() {
        return DateManipulation.daysToDate(marriageDay);
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }

    @Override
    public List<Integer> getPartnerIds() {
        return Arrays.asList(husband, wife);
    }

    @Override
    public int compareTo(final IPartnership o) {
        if (this.equals(o)) {
            return 0;
        }
        return 1;
    }
}
