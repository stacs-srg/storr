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
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NumberOfChildrenDistribuition;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.NumberOfChildrenFromMaternitiesDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
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
    private static final int MAX_TIME_INTO_RELATIONSHIP_UNTIL_FIRST_BIRTH = 5;
    private static NumberOfChildrenDistribuition numberOfChildrenDistribution = new NumberOfChildrenDistribuition(random);
    private static NumberOfChildrenFromMaternitiesDistribution numberOfChildrenFromMaternitiesDistribution = new NumberOfChildrenFromMaternitiesDistribution(random);
    
    private NormalDistribution timeBetweenMaternitiesDistrobution;

    private Integer id;
    private Integer husband;
    private Integer wife;
    private OrganicTimeline timeline;
    private int marriageDay;
    private List<Integer> childrenIds = new ArrayList<Integer>();
    private boolean on;
    private int numberOfChildrenToBeHadByCouple;

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
     * @param id Specifies the ID of the OrganicPartnership.
     * @param husband The OrganicPerson object representing the husband.
     * @param wife The OrganicPerson object representing the wife.
     * @param marriageDay The marriage day specified in days since 1/1/1600
     * @return Returns an object array of size 2, where at index 0 can be found the newly constructed OrganicPartnership and at index 1 the partnerships child (if no child then value is null)
     */
    public static Object[] createOrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int marriageDay, int currentDay) {
        // Contains OrganicPartnership object
        OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, marriageDay);
        // Contains OrganicPerson object aka the child - if no child returns null
        OrganicPerson[] children = partnership.createPartnershipTimeline(husband, wife, currentDay);
        Object[] returns;
        returns = new Object[children.length + 1];
        int i = 1;
        for (OrganicPerson child : children) {
            returns[i++] = child;
        }
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

    private OrganicPerson[] createPartnershipTimeline(final OrganicPerson husband, final OrganicPerson wife, int currentDay) {

        // TODO Correctly populate timeline
        timeline = new OrganicTimeline(marriageDay);

        // Decide if/when relationship terminates
        switch (divorceInstigatedByGenderDistribution.getSample()) {
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
        numberOfChildrenToBeHadByCouple = numberOfChildrenDistribution.getSample();
//        System.out.println(numberOfChildrenToBeHadByCouple);
        if (numberOfChildrenToBeHadByCouple == 0) {
            return new OrganicPerson[0];
        }
        int mean = 0;
        while (numberOfChildrenToBeHadByCouple > 0) {
            mean = getMeanForChildSpacingDistribution(husband, wife, currentDay);
            if (mean < PopulationLogic.getInterChildInterval() * OrganicPopulation.DAYS_PER_YEAR) {
//                System.out.println("I'm Decrementing");
                numberOfChildrenToBeHadByCouple --;
            } else {
//                System.out.println("HERE AGAIN!");
                break;
            }
        }
        if (numberOfChildrenToBeHadByCouple != 0) {
            int standardDeviation = (mean - PopulationLogic.getInterChildInterval()) / 4;
            try {
                timeBetweenMaternitiesDistrobution = new NormalDistribution(mean , standardDeviation, random);
            } catch (NegativeDeviationException e) {
//                System.out.println("Uh oh - Exception");
                return new OrganicPerson[0];
            }
//            System.out.println("Heading to next level");
            return createBirthEvent(husband, wife, currentDay);
        } else {
//            System.out.println("Kids killed by decrementor");
            return new OrganicPerson[0];
        }
    }
    
    private int getMeanForChildSpacingDistribution(OrganicPerson husband, OrganicPerson wife, int currentDay) {
        int mean = getLastPossibleBirthDate(husband, wife) - currentDay;
        if (numberOfChildrenToBeHadByCouple == 0) {
            return -1;
        }
        return mean / numberOfChildrenToBeHadByCouple;
    }

    public OrganicPerson[] createBirthEvent(OrganicPerson husband, OrganicPerson wife, int currentDay) {
        int numberOfChildrenInPregnacy = numberOfChildrenFromMaternitiesDistribution.getSample();
        if (numberOfChildrenInPregnacy > numberOfChildrenToBeHadByCouple - childrenIds.size()) {
            numberOfChildrenInPregnacy = numberOfChildrenToBeHadByCouple - childrenIds.size();
        }
        if(numberOfChildrenInPregnacy == 0) {
            return new OrganicPerson[0];
        } else {
            OrganicPerson[] children = new OrganicPerson[numberOfChildrenInPregnacy];

            int dayOfBirth = timeBetweenMaternitiesDistrobution.getSample().intValue();
//            System.out.println("Current Day: " + currentDay);
//            System.out.println("Birth Day: " + dayOfBirth);
            if (PopulationLogic.parentsHaveSensibleAgesAtChildBirth(husband.getBirthDay(), husband.getDeathDay(), 
                    wife.getBirthDay(), wife.getDeathDay(), dayOfBirth + marriageDay)) {
                timeline.addEvent(currentDay + dayOfBirth, new OrganicEvent(EventType.BIRTH));
                for (int i = 0; i < numberOfChildrenInPregnacy; i++) {
                    children[i] = new OrganicPerson(IDFactory.getNextID(), currentDay + dayOfBirth, false);
                    childrenIds.add(children[i].getId());
                }
                return children;
            } else {
                return new OrganicPerson[0];
            }
        }

    }

    

    private int getLastPossibleBirthDate(OrganicPerson husband, OrganicPerson wife) {
        int lastEndDate = getEndDate();
        if (lastEndDate > wife.getBirthDay() + PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.DAYS_PER_YEAR) {
            lastEndDate = wife.getBirthDay() + (int) (PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.DAYS_PER_YEAR);
        }
        if (lastEndDate > husband.getBirthDay() + PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.DAYS_PER_YEAR) {
            lastEndDate = husband.getBirthDay() + (int) (PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.DAYS_PER_YEAR);
        }
        return lastEndDate;
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

    /**
     * Returns the end date of the relationship in days since 1/1/1600.
     * 
     * @return The end date of the relationship in days since 1/1/1600.
     */
    public int getEndDate() {
        return timeline.getEndDate();
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
    public String getMarriagePlace() {
        throw new RuntimeException("not implemented");
    }

     @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }
     
    @Override
    public int compareTo(final IPartnership o) {
        if (this.equals(o)) {
            return 0;
        }
        return 1;
    }
}
