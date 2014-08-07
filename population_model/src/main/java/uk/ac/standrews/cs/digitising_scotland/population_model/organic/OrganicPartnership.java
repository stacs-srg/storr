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
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class OrganicPartnership implements IPartnership {

    // Adjusted number of children variables
    private static ArrayList<Integer>[] adjustedNumberOfChildren = null;
    
    // Universal partnership ditributions
    private static Random random = RandomFactory.getRandom();
    private static DivorceInstigatedByGenderDistribution divorceInstigatedByGenderDistribution = new DivorceInstigatedByGenderDistribution(random);
    private static DivorceAgeForMaleDistribution divorceAgeForMaleDistribution = new DivorceAgeForMaleDistribution(random);
    private static DivorceAgeForFemaleDistribution divorceAgeForFemaleDistribution = new DivorceAgeForFemaleDistribution(random);
    private static Distribution<Integer> numberOfChildrenDistribution = new NumberOfChildrenDistribuition(random);
    private static NumberOfChildrenFromMaternitiesDistribution numberOfChildrenFromMaternitiesDistribution = new NumberOfChildrenFromMaternitiesDistribution(random);
    private static TimeFromCohabitationToMarriageDistribution timeFromCohabitationToMarriageDistribution = TimeFromCohabitationToMarriageDistribution.TimeFromCohabitationToMarriageDistributionFactory(random);
    private static CohabitationLengthDistribution cohabitationLengthDistribution = new CohabitationLengthDistribution(random);
    private static DivorceReasonMaleDistribution divorceReasonMaleDistribution = new DivorceReasonMaleDistribution(random);
    private static DivorceReasonFemaleDistribution divorceReasonFemaleDistribution = new DivorceReasonFemaleDistribution(random);
    private static AffairsNumberOfDistribution affairsNumberOfDistribution = new AffairsNumberOfDistribution(random);
    private static Distribution<Integer> affairNumberOfChildrenDistribution = new AffairNumberOfChildrenDistribution(random);
    private static final int STANDARD_DEVIATION_FACTOR = 4;

    // Partnership instance required variables
    private Integer id;
    private Integer husband;
    private Integer wife;
    private int partnershipDay;
    private List<Integer> childrenIds = new ArrayList<Integer>();
    private String location = "St Andrews";
    private String familyName = null;

    // Partnership instance helper variables
    private boolean cohabiting;
    private boolean married;
    
    private OrganicPopulation population;
    
    private int cohabThenMarriageMarriageDay = -1;
    
    private OrganicTimeline timeline;
    private boolean on;
    private int numberOfChildrenToBeHadByCouple;
    private NormalDistribution timeBetweenMaternitiesDistrobution;
    private DivorceReason divorceReason;

    /*
     * Factory and constructor
     */

    /**
     * Constructs partnership objects and returns both the partnership in the first field of the array and the partnerships first child in the second.
     *
     * @param id          Specifies the ID of the OrganicPartnership.
     * @param husband     The OrganicPerson object representing the husband.
     * @param wife        The OrganicPerson object representing the wife.
     * @param partnershipDay The marriage day specified in days since 1/1/1600.
     * @param currentDay  The current day of the simulation in days since 1/1/1600.
     * @param familyType  The type of family as defined in the enum {@link FamilyType}
     * @return Returns an object array of size 2, where at index 0 can be found the newly constructed OrganicPartnership and at index 1 the partnerships child (if no child then value is null)
     */
    public static Object[] createOrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int partnershipDay, final int currentDay, final FamilyType familyType, final OrganicPopulation population) {
        // Handle children appropriately
    	
    	OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, partnershipDay, familyType, population);
        if (adjustedNumberOfChildren == null) {
            setUpArray();
        }
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

    private OrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int partnershipDay, final FamilyType familyType, final OrganicPopulation population) {
        this.id = id;
        this.husband = husband.getId();
        this.wife = wife.getId();
        this.partnershipDay = partnershipDay;
        this.population = population;
        if (familyType == FamilyType.MARRIAGE) {
            familyName = husband.getSurname();
        }
        if (familyType == FamilyType.COHABITATION_THEN_MARRIAGE) {
        	cohabThenMarriageMarriageDay = partnershipDay + timeFromCohabitationToMarriageDistribution.getIntSample();
        }
        this.turnOn();
        setCohabMarriageFlags(familyType);
    }

    /*
     * High level methods
     */

	private OrganicPerson[] createPartnershipTimeline(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
        timeline = new OrganicTimeline(partnershipDay);
        // Decide if/when partnership terminates
        if (!cohabiting && !married) {
        	// Single / lone parent family
        	// model affairs in here
        	// Nothing to do here I think, end needs to be set once child births have been decided.
        	setUpAffairEndEvent(male, female, currentDay);
        } else if (cohabiting && !married) {
        	// cohabiting
        	setUpCohabitationEndEvent(male, female, currentDay);
        } else if (cohabiting && married) {
        	// cohab then marriage / marriage
        	setUpDivorceEvent(male, female);
        }

        // Decide on a number of children for relationship
        return setUpBirthPlan(male, female, currentDay);
    }

    /*
     * Adjusted number of children methods
     */

    private static void setUpArray() {
        adjustedNumberOfChildren = new ArrayList[NumberOfChildrenDistribuition.MAXIMUM_NUMBER_OF_CHILDREN + 1];
        for (int i = 0; i < NumberOfChildrenDistribuition.MAXIMUM_NUMBER_OF_CHILDREN + 1; i++) {
            adjustedNumberOfChildren[i] = new ArrayList<Integer>();
        }
    }

    private static int checkForFamilySize(final int assignedNumberOfChilren) {
        if (adjustedNumberOfChildren[assignedNumberOfChilren].size() != 0) {
        	OrganicPopulationLogger.decLeftOverChildren(adjustedNumberOfChildren[assignedNumberOfChilren].get(0) - assignedNumberOfChilren);
            return adjustedNumberOfChildren[assignedNumberOfChilren].remove(0);
        }
        return assignedNumberOfChilren;
    }

    private static void addUndersizedFamily(final int intendedNumberOfChildren, final int actualNumberOfChildren) {
        adjustedNumberOfChildren[actualNumberOfChildren].add(intendedNumberOfChildren);
        OrganicPopulationLogger.incLeftOverChildren(intendedNumberOfChildren - actualNumberOfChildren);
    }

    /*
     * SetUp methods
     */

    private OrganicPerson[] setUpBirthPlan(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
    	if (!cohabiting && !married) {
        	// Single / lone parent family
    		numberOfChildrenToBeHadByCouple = affairNumberOfChildrenDistribution.getSample();
        } else if (cohabiting && !married) {
        	// cohabiting
        	numberOfChildrenToBeHadByCouple = numberOfChildrenDistribution.getSample();
        	numberOfChildrenToBeHadByCouple = checkForFamilySize(numberOfChildrenToBeHadByCouple);
        } else if (cohabiting && married) {
        	// cohab then marriage / marriage
        	numberOfChildrenToBeHadByCouple = numberOfChildrenDistribution.getSample();
        	numberOfChildrenToBeHadByCouple = checkForFamilySize(numberOfChildrenToBeHadByCouple);
        }
    	
        
        int intendedNumberOfChildren = numberOfChildrenToBeHadByCouple;

        if (numberOfChildrenToBeHadByCouple == 0) {
            return new OrganicPerson[0];
        }
        int mean = 0;
        while (numberOfChildrenToBeHadByCouple > 0) {
            mean = getMeanForChildSpacingDistribution(husband, wife, currentDay);
            if (mean < PopulationLogic.getInterChildInterval() * OrganicPopulation.getDaysPerYear()) {
                numberOfChildrenToBeHadByCouple--;
            } else {
                break;
            }
        }
        if (numberOfChildrenToBeHadByCouple != 0) {
            int standardDeviation = (mean - PopulationLogic.getInterChildInterval()) / STANDARD_DEVIATION_FACTOR;
            try {
                timeBetweenMaternitiesDistrobution = new NormalDistribution(mean, standardDeviation, random);
            } catch (NegativeDeviationException e) {
                if (!cohabiting && !married) {
                	setUpAffairEndEvent(husband, wife, currentDay);
                }
            	return new OrganicPerson[0];
            }
            if (intendedNumberOfChildren != numberOfChildrenToBeHadByCouple && cohabiting) {
                addUndersizedFamily(intendedNumberOfChildren, numberOfChildrenToBeHadByCouple);
            }
            return setUpBirthEvent(husband, wife, currentDay);
        } else {
            if (intendedNumberOfChildren != numberOfChildrenToBeHadByCouple && cohabiting) {
                addUndersizedFamily(intendedNumberOfChildren, numberOfChildrenToBeHadByCouple);
            }
            if (!cohabiting && !married) {
            	setUpAffairEndEvent(husband, wife, currentDay);
            }
            return new OrganicPerson[0];
        }
    }
    
    private void setUpAffairEndEvent(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
    	timeline.addEvent(currentDay + 1, new OrganicEvent(EventType.END_OF_AFFAIR, this, currentDay + 1));
    }
    
    private void setUpCohabitationEndEvent(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
    	int lengthOfCohab = cohabitationLengthDistribution.getSample();
    	int endDayOfCohab = currentDay + lengthOfCohab;
    	if (PopulationLogic.dateBeforeDeath(endDayOfCohab, male.getDeathDay())) {
    		if (PopulationLogic.dateBeforeDeath(endDayOfCohab, female.getDeathDay())) {
    			timeline.addEvent(endDayOfCohab, new OrganicEvent(EventType.END_OF_COHABITATION, this, endDayOfCohab));
    			timeline.setEndDate(endDayOfCohab);
    		} else {
    			if (female.getDeathDay() < currentDay) {
    			}
    			timeline.addEvent(female.getDeathDay(), new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, female.getDeathDay()));
    			timeline.setEndDate(female.getDeathDay());
    		}
    	} else {
    		if (male.getDeathDay() < currentDay) {
			}
			timeline.addEvent(male.getDeathDay(), new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, male.getDeathDay()));
			timeline.setEndDate(male.getDeathDay());
		}
    	
    }

    private void setUpDivorceEvent(final OrganicPerson husband, final OrganicPerson wife) {
    	int actualMarriageDay = partnershipDay;
    	if (cohabThenMarriageMarriageDay != -1) {
    		actualMarriageDay = cohabThenMarriageMarriageDay;
    	}
        switch (divorceInstigatedByGenderDistribution.getSample()) {
        case MALE:
            // get male age at divorce
            int maleDivorceAgeInDays;
            do {
                maleDivorceAgeInDays = divorceAgeForMaleDistribution.getSample() + husband.getBirthDay();
            }
            while (PopulationLogic.divorceAfterMarriage(DateManipulation.differenceInDays(husband.getBirthDay(), actualMarriageDay), maleDivorceAgeInDays) ||
                    PopulationLogic.dateBeforeDeath(husband.getDeathDay(), maleDivorceAgeInDays) ||
                    PopulationLogic.dateBeforeDeath(wife.getDeathDay(), maleDivorceAgeInDays));
            
            // TODO handles only the adultery special case - could be used to enforce geographical movement to support seperation.
            divorceReason = divorceReasonMaleDistribution.getSample();
            timeline.addEvent(husband.getBirthDay() + maleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE, this, husband.getBirthDay() + maleDivorceAgeInDays));
            timeline.setEndDate(maleDivorceAgeInDays);
            if (divorceReason == DivorceReason.ADULTERY) {
            	setupAffair(wife);
            }
            break;
        case FEMALE:
            // get female age at divorce
            int femaleDivorceAgeInDays;
            do {
                femaleDivorceAgeInDays = divorceAgeForFemaleDistribution.getSample() + wife.getBirthDay();
            }
            while (PopulationLogic.divorceAfterMarriage(DateManipulation.differenceInDays(wife.getBirthDay(), actualMarriageDay), femaleDivorceAgeInDays) ||
                    PopulationLogic.dateBeforeDeath(wife.getDeathDay(), femaleDivorceAgeInDays) ||
                    PopulationLogic.dateBeforeDeath(husband.getDeathDay(), femaleDivorceAgeInDays));

            
            // TODO handles only the adultery special case - could be used to enforce geographical movement to support seperation.
            divorceReason = divorceReasonFemaleDistribution.getSample();
            timeline.addEvent(wife.getBirthDay() + femaleDivorceAgeInDays, new OrganicEvent(EventType.DIVORCE, this, wife.getBirthDay() + femaleDivorceAgeInDays));
            timeline.setEndDate(femaleDivorceAgeInDays);
            if (divorceReason == DivorceReason.ADULTERY) {
            	setupAffair(husband);
            }
            
            
            break;
        case NO_DIVORCE:
            // If not then added earliest death date
            int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
            timeline.addEvent(firstPartnersDeathDate, new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, firstPartnersDeathDate));
            timeline.setEndDate(firstPartnersDeathDate);
            break;
        default:
            break;
        }
    }

    private void setupAffair(OrganicPerson marriedPerson) {
    	int numberOfAffairs = affairsNumberOfDistribution.getSample();
    	AffairDistribution affairDistribution = AffairDistribution.AffairDistributionFactory(marriedPerson, random);
    	for (int i = 0; i < numberOfAffairs; i++) {
    		int day = affairDistribution.getIntSample();
    		marriedPerson.getPopulation().addPersonToAffairsWaitingQueue(marriedPerson, day);
    		marriedPerson.getTimeline().addEvent(day, new OrganicEvent(EventType.AFFAIR, this, day));
    		if (marriedPerson.getSex() == 'M') {
    			timeline.addEvent(day, new OrganicEvent(EventType.MALE_BEGINS_AFFAIR, this, day));
    		} else {
    			timeline.addEvent(day, new OrganicEvent(EventType.FEMALE_BEGINS_AFFAIR, this, day));
    		}
    		
    	}
	}

	/**
     * Sets up next birth event on partnership timeline, checking the number of births still due and giving the possibility of multiple children in pregnancy.
     *
     * @param husband    An OrganicPerson object representing the father
     * @param wife       An OrganicPerson object representing the mother
     * @param currentDay The current day of the simulation
     * @return An OrganicPerson array containing any children to be born in the birth event. Size zero if none.
     */
    public OrganicPerson[] setUpBirthEvent(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
        int numberOfChildrenInPregnacy = numberOfChildrenFromMaternitiesDistribution.getSample();
        if (numberOfChildrenInPregnacy > numberOfChildrenToBeHadByCouple - childrenIds.size()) {
            numberOfChildrenInPregnacy = numberOfChildrenToBeHadByCouple - childrenIds.size();
        }
        if (numberOfChildrenInPregnacy == 0) {
        	if (!cohabiting && !married && lastChildBorn()) {
            	setUpAffairEndEvent(husband, wife, currentDay);
            }
            return new OrganicPerson[0];
        } else {
            OrganicPerson[] children = new OrganicPerson[numberOfChildrenInPregnacy];

            int dayOfBirth = timeBetweenMaternitiesDistrobution.getSample().intValue();
            if (husband != null && wife != null && PopulationLogic.parentsHaveSensibleAgesAtChildBirth(husband.getBirthDay(), husband.getDeathDay(),
                    wife.getBirthDay(), wife.getDeathDay(), dayOfBirth + currentDay)) {
                timeline.addEvent(currentDay + dayOfBirth, new OrganicEvent(EventType.BIRTH, this, currentDay + dayOfBirth));
                for (int i = 0; i < numberOfChildrenInPregnacy; i++) {
                    children[i] = new OrganicPerson(IDFactory.getNextID(), currentDay + dayOfBirth, id, wife.getPopulation(), false);
                    childrenIds.add(children[i].getId());
                }
                if (!cohabiting && !married && lastChildBorn()) {
                	setUpAffairEndEvent(husband, wife, dayOfBirth + 1);
                }
                return children;
            } else {
                OrganicPopulationLogger.incStopedHavingEarlyDeaths(numberOfChildrenToBeHadByCouple - children.length);
                if (!cohabiting && !married && lastChildBorn()) {
                	setUpAffairEndEvent(husband, wife, currentDay);
                }
                return new OrganicPerson[0];
            }
        }
    }
    
    public boolean lastChildBorn() {
    	if (childrenIds.size() >= numberOfChildrenToBeHadByCouple) {
    		return true;
      	}
    	return false;
    }

    /**
     * Instigates a divorce in the partnership.
     *
     * @param husband An OrganicPartnership object representing the male.
     * @param wife    An OrganicPartnership object representing the female.
     */
    public void divorce(final OrganicPerson husband, final OrganicPerson wife) {

        OrganicPopulationLogger.logDivorce();

        turnOff();

        husband.updateTimeline(EventType.DIVORCE);
        wife.updateTimeline(EventType.DIVORCE);
    }

    /*
     * Statistical birth helper methods
     */

    private int getMeanForChildSpacingDistribution(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
        int mean = getLastPossibleBirthDate(husband, wife) - currentDay;
        if (numberOfChildrenToBeHadByCouple == 0) {
            return -1;
        }
        return mean / numberOfChildrenToBeHadByCouple;
    }

    /*
     * Date helper methods
     */

    private int getLastPossibleBirthDate(final OrganicPerson husband, final OrganicPerson wife) {
    	int lastEndDate;
    	if (!married && !cohabiting) {
    		lastEndDate = wife.getBirthDay() + (int) (PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.getDaysPerYear());
    	} else {
    		lastEndDate = getEndDate();
    	}
        
        if (lastEndDate > wife.getBirthDay() + PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.getDaysPerYear()) {
            lastEndDate = wife.getBirthDay() + (int) (PopulationLogic.getMaximumMotherAgeAtChildBirth() * OrganicPopulation.getDaysPerYear());
        }
        if (lastEndDate > husband.getBirthDay() + PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.getDaysPerYear()) {
            lastEndDate = husband.getBirthDay() + (int) (PopulationLogic.getMaximumFathersAgeAtChildBirth() * OrganicPopulation.getDaysPerYear());
        }
        return lastEndDate;
    }

    /**
     * Family Name getter.
     *
     * @return familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Family Name setter.
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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
     * Returns the adjusted number of children array.
     * 
     * @return The adjusted number of children array.
     */
    public static ArrayList<Integer>[] getAdjustedNumberOfChildren() {
    	ArrayList<Integer>[] temp = adjustedNumberOfChildren.clone();
    	temp = adjustedNumberOfChildren;
    	return temp;
    }
    
    public OrganicPopulation getPopulation() {
    	return population;
    }

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
    public void setTimeline(final OrganicTimeline timeline) {
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

    /**
     * Returns the On boolean flag for the partnership.
     * 
     * @return The On boolean value.
     */
    public boolean isOn() {
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
        return DateManipulation.daysToDate(partnershipDay);
    }

    @Override
    public String getMarriagePlace() {
        return location;
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }
    
    private void setCohabMarriageFlags(FamilyType familyType) {
		switch (familyType) {
		case SINGLE:
		case LONE_MOTHER:
		case LONE_FATHER:
			cohabiting = false;
			married = false;			
			break;
		case COHABITATION:
			cohabiting = true;
			married = false;
			break;
		case COHABITATION_THEN_MARRIAGE:
		case MARRIAGE:
			cohabiting = true;
			married = true;
			break;
		default:
			break;
		}
		
	}

    @Override
    public int compareTo(final IPartnership o) {
        if (this.equals(o)) {
            return 0;
        } else {
        	return 1;
        }
    }
}
