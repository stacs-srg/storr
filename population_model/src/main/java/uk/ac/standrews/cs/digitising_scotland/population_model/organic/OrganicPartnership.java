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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.*;
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
    
	// Tempoary logging variables
    public static int leftOverChildren = 0;
    public static int stopedHavingEarlyDeaths = 0;
    
    // Left over children protocol variables
    private static final int MAX_NUMBER_OF_CHILDREN_IN_FAMILY_TO_BE_ELLIGABLE_FOR_LEFT_OVER_CHILDREN = 6;
    private static final int VOLUME_OF_LEFT_OVER_CHILDREN_TO_BE_ALLOCATED = 1;

    // Universal partnership ditributions
    private static Random random = RandomFactory.getRandom();
    private static DivorceInstigatedByGenderDistribution divorceInstigatedByGenderDistribution = new DivorceInstigatedByGenderDistribution(random);
    private static DivorceAgeForMaleDistribution divorceAgeForMaleDistribution = new DivorceAgeForMaleDistribution(random);
    private static DivorceAgeForFemaleDistribution divorceAgeForFemaleDistribution = new DivorceAgeForFemaleDistribution(random);
    private static NumberOfChildrenDistribuition numberOfChildrenDistribution = new NumberOfChildrenDistribuition(random);
    private static NumberOfChildrenFromMaternitiesDistribution numberOfChildrenFromMaternitiesDistribution = new NumberOfChildrenFromMaternitiesDistribution(random);

    // Partnership instance required variables
    private Integer id;
    private Integer husband;
    private Integer wife;
    private int marriageDay;
    private List<Integer> childrenIds = new ArrayList<Integer>();
    
    // Partnership instance helper variables
    private OrganicTimeline timeline;
    private boolean on;
    private int numberOfChildrenToBeHadByCouple;
    private NormalDistribution timeBetweenMaternitiesDistrobution;
    
    /*
     * Factory and constructor
     */
    
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
        OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, marriageDay);
        // Contains OrganicPerson objects aka the children - if no children returns null
        OrganicPerson[] children = partnership.createPartnershipTimeline(husband, wife, currentDay);
        Object[] returns = new Object[children.length + 1];
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

        timeline = new OrganicTimeline(marriageDay);

        // Decide if/when relationship terminates
        setUpDivorceEvent(husband, wife);

        // Decide on a number of children for relationship
        return setUpBirthPlan(husband, wife, currentDay);
    }

    /*
     * SetUp methods
     */
    
    private OrganicPerson[] setUpBirthPlan(final OrganicPerson husband, final OrganicPerson wife, int currentDay) {
        numberOfChildrenToBeHadByCouple = numberOfChildrenDistribution.getSample();
        // FIXME Rewrite left over children protcol as not to make distribution squint - try swap size for size
        if (numberOfChildrenToBeHadByCouple < MAX_NUMBER_OF_CHILDREN_IN_FAMILY_TO_BE_ELLIGABLE_FOR_LEFT_OVER_CHILDREN 
                && leftOverChildren >= VOLUME_OF_LEFT_OVER_CHILDREN_TO_BE_ALLOCATED) {
            numberOfChildrenToBeHadByCouple += VOLUME_OF_LEFT_OVER_CHILDREN_TO_BE_ALLOCATED;
            leftOverChildren -= VOLUME_OF_LEFT_OVER_CHILDREN_TO_BE_ALLOCATED;
        }
        if (numberOfChildrenToBeHadByCouple == 0) {
            return new OrganicPerson[0];
        }
        int mean = 0;
        while (numberOfChildrenToBeHadByCouple > 0) {
            mean = getMeanForChildSpacingDistribution(husband, wife, currentDay);
            if (mean < PopulationLogic.getInterChildInterval() * OrganicPopulation.getDaysPerYear()) {
                leftOverChildren++;
                numberOfChildrenToBeHadByCouple --;
            } else {
                break;
            }
        }
        if (numberOfChildrenToBeHadByCouple != 0) {
            int standardDeviation = (mean - PopulationLogic.getInterChildInterval()) / 4;
            try {
                timeBetweenMaternitiesDistrobution = new NormalDistribution(mean , standardDeviation, random);
            } catch (NegativeDeviationException e) {
                return new OrganicPerson[0];
            }
            return setUpBirthEvent(husband, wife, currentDay);
        } else {
            return new OrganicPerson[0];
        }
    }

    private void setUpDivorceEvent(final OrganicPerson husband,
            final OrganicPerson wife) {
        switch (divorceInstigatedByGenderDistribution.getSample()) {
        case MALE:
            // get male age at divorce
            int maleDivorceAgeInDays;
            do {
                maleDivorceAgeInDays = divorceAgeForMaleDistribution.getSample() + husband.getBirthDay();
            }
            while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), marriageDay), maleDivorceAgeInDays) ||
                    !PopulationLogic.divorceNotAfterDeath(husband.getDeathDay(),maleDivorceAgeInDays) ||
                    !PopulationLogic.divorceNotAfterDeath(wife.getDeathDay(),maleDivorceAgeInDays));

            timeline.addEvent(maleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE));
            timeline.setEndDate(maleDivorceAgeInDays);
            break;
        case FEMALE:
            // get female age at divorce
            int femaleDivorceAgeInDays;
            do {
                femaleDivorceAgeInDays = divorceAgeForFemaleDistribution.getSample() + wife.getBirthDay();
            }
            while (!PopulationLogic.divorceNotBeforeMarriage(DateManipulation.differenceInDays(wife.getBirthDay(), marriageDay), femaleDivorceAgeInDays) ||
                    !PopulationLogic.divorceNotAfterDeath(wife.getDeathDay(),femaleDivorceAgeInDays) ||
                    !PopulationLogic.divorceNotAfterDeath(husband.getDeathDay(),femaleDivorceAgeInDays));

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
    }

    /**
     * Sets up next birth event on partnership timeline, checking the number of births still due and giving the possibility of multiple children in pregnancy.
     * 
     * @param husband An OrganicPerson object representing the father
     * @param wife An OrganicPerson object representing the mother
     * @param currentDay The current day of the simulation
     * @return An OrganicPerson array containing any children to be born in the birth event. Size zero if none.
     */
    public OrganicPerson[] setUpBirthEvent(OrganicPerson husband, OrganicPerson wife, int currentDay) {
        int numberOfChildrenInPregnacy = numberOfChildrenFromMaternitiesDistribution.getSample();
        if (numberOfChildrenInPregnacy > numberOfChildrenToBeHadByCouple - childrenIds.size()) {
            numberOfChildrenInPregnacy = numberOfChildrenToBeHadByCouple - childrenIds.size();
        }
        if(numberOfChildrenInPregnacy == 0) {
            return new OrganicPerson[0];
        } else {
            OrganicPerson[] children = new OrganicPerson[numberOfChildrenInPregnacy];

            int dayOfBirth = timeBetweenMaternitiesDistrobution.getSample().intValue();
            if (PopulationLogic.parentsHaveSensibleAgesAtChildBirth(husband.getBirthDay(), husband.getDeathDay(), 
                    wife.getBirthDay(), wife.getDeathDay(), dayOfBirth + marriageDay)) {
                timeline.addEvent(currentDay + dayOfBirth, new OrganicEvent(EventType.BIRTH));
                for (int i = 0; i < numberOfChildrenInPregnacy; i++) {
                    children[i] = new OrganicPerson(IDFactory.getNextID(), currentDay + dayOfBirth, wife.getPopulation(), false);
                    childrenIds.add(children[i].getId());
                }
                return children;
            } else {
                stopedHavingEarlyDeaths += (numberOfChildrenToBeHadByCouple - children.length);
                return new OrganicPerson[0];
            }
        }
    }
    
    /**
     * Instigates a divorce in the partnership.
     * 
     * @param husband An OrganicPartnership object representing the male.
     * @param wife An OrganicPartnership object representing the female.
     */
    public void divorce(OrganicPerson husband, OrganicPerson wife) {

        OrganicPopulationLogger.logDivorce();

        turnOff();
        
        husband.updateTimeline(EventType.DIVORCE);
        wife.updateTimeline(EventType.DIVORCE);
    }
    
    /*
     * Statistical birth helper methods
     */
    
    private int getMeanForChildSpacingDistribution(OrganicPerson husband, OrganicPerson wife, int currentDay) {
        int mean = getLastPossibleBirthDate(husband, wife) - currentDay;
        if (numberOfChildrenToBeHadByCouple == 0) {
            return -1;
        }
        return mean / numberOfChildrenToBeHadByCouple;
    }

    /*
     * Date helper methods
     */

    private int getLastPossibleBirthDate(OrganicPerson husband, OrganicPerson wife) {
        int lastEndDate = getEndDate();
        if (lastEndDate > wife.getBirthDay() + PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.getDaysPerYear()) {
            lastEndDate = wife.getBirthDay() + (int) (PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.getDaysPerYear());
        }
        if (lastEndDate > husband.getBirthDay() + PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.getDaysPerYear()) {
            lastEndDate = husband.getBirthDay() + (int) (PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.getDaysPerYear());
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
    
    /*
     * Getters and setters
     */

    /**
     * Returns the timeline of the partnership.
     * 
     * @return Returns the timeline of the partnership.
     */
    public OrganicTimeline getTimeline() {
        return timeline;
    }
    
    /**
     * Sets the timeline of the partnership.
     * 
     * @param timeline The timeline to be set to.
     */
    public void setTimeline(OrganicTimeline timeline) {
        this.timeline = timeline;
    }

    /**
     * Returns the end date of the relationship in days since 1/1/1600.
     * 
     * @return The end date of the relationship in days since 1/1/1600.
     */
    public int getEndDate() {
        return timeline.getEndDate();
    }
    
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

    /*
     * Interface Methods
     */
    
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
