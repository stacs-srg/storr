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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.AffairSpacingDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NoPermissableValueException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalEnumDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.LoggingControl;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * The OrganicPartnership class models the different types of possibly partnership in the model.
 * 
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public final class OrganicPartnership implements IPartnership {

    // Universal partnership distributions
    private static Random random = RandomFactory.getRandom();

    /*
     * -------------------------------- Distributions --------------------------------
     */
    
    private static final Enum<?>[] DIVORCE_INSTIGATED_BY_ARRAY = { DivorceInstigation.MALE, DivorceInstigation.FEMALE, DivorceInstigation.NO_DIVORCE };
    private static TemporalEnumDistribution<DivorceInstigation> temporalDivorceInstigatedByGenderDistribution;
    private static TemporalIntegerDistribution temporalDivorceAgeForMaleDistribution;
    private static TemporalIntegerDistribution temporalDivorceAgeForFemaleDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInCohabDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInCohabThenMarriageDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInMarriageDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInMaternityDistribution;
    private static TemporalIntegerDistribution temporalCohabitationLengthDistribution;
    private static TemporalIntegerDistribution temporalAffairNumberOfDistribution;
    private static TemporalIntegerDistribution temporalAffairNumberOfChildrenDistribution;

    private static final Enum<?>[] DIVORCE_REASON_ARRAY = { DivorceReason.ADULTERY, DivorceReason.BEHAVIOUR, DivorceReason.DESERTION, DivorceReason.SEPARATION_WITH_CONSENT, DivorceReason.SEPARATION };
    private static TemporalEnumDistribution<DivorceReason> temporalDivorceReasonMaleDistribution;
    private static TemporalEnumDistribution<DivorceReason> temporalDivorceReasonFemaleDistribution;

    private static TemporalIntegerDistribution temporalCohabitaitonToMarriageTimeDistribution;

    private static final int STANDARD_DEVIATION_FACTOR = 4;
    private static final int MINUMUM_DIVORCE_WINDOW = 25;

    // Partnership instance required variables
    private Integer id;
    private Integer husband;
    private Integer wife;
    private int partnershipDay;
    private List<Integer> childrenIds = new ArrayList<Integer>();
    private String location = "St Andrews";
    private String familyName = null;
    private int endDay;

    // Partnership instance helper variables
    private boolean cohabiting;
    private boolean married;

    private int cohabThenMarriageMarriageDay = -1;

    private int numberOfChildrenToBeHadByCouple;
    private NormalDistribution timeBetweenMaternitiesDistrobution;
    private DivorceReason divorceReason;

    /*
     * -------------------------------- Setup and constructors --------------------------------
     */

    /**
     * Setups the classes temporal distributions as runtime.
     * 
     * @param population The instance of the population to which the distributions are to pertain.
     */
    public static void setupTemporalDistributionsInOrganicPartnershipClass(final OrganicPopulation population) {
        temporalChildrenNumberOfInCohabDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_cohab_distributions_filename", random, true);
        temporalChildrenNumberOfInCohabThenMarriageDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_cohab_then_marriage_distributions_filename", random, true);
        temporalChildrenNumberOfInMarriageDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_marriage_distributions_filename", random, true);
        temporalDivorceInstigatedByGenderDistribution = new TemporalEnumDistribution<DivorceInstigation>(population, "divorce_instigated_by_gender_distributions_filename", random, DIVORCE_INSTIGATED_BY_ARRAY);
        temporalDivorceAgeForMaleDistribution = new TemporalIntegerDistribution(population, "divorce_age_for_male_distributions_data_filename", random, false);
        temporalDivorceAgeForFemaleDistribution = new TemporalIntegerDistribution(population, "divorce_age_for_female_distributions_data_filename", random, false);
        temporalChildrenNumberOfInMaternityDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_maternity_distributions_data_filename", random, false);
        temporalCohabitationLengthDistribution = new TemporalIntegerDistribution(population, "cohabitation_length_distributions_data_filename", random, false);
        temporalDivorceReasonMaleDistribution = new TemporalEnumDistribution<DivorceReason>(population, "divorce_reason_male_distributions_data_filename", random, DIVORCE_REASON_ARRAY);
        temporalDivorceReasonFemaleDistribution = new TemporalEnumDistribution<DivorceReason>(population, "divorce_reason_female_distributions_data_filename", random, DIVORCE_REASON_ARRAY);
        temporalAffairNumberOfDistribution = new TemporalIntegerDistribution(population, "affair_number_of_distributions_data_filename", random, false);
        temporalAffairNumberOfChildrenDistribution = new TemporalIntegerDistribution(population, "affair_number_of_children_distributions_data_filename", random, true);
        temporalCohabitaitonToMarriageTimeDistribution = new TemporalIntegerDistribution(population, "cohabitation_to_marriage_time_distributions_data_filename", random, false);
    }

    /**
     * Constructs partnership objects and returns both the partnership in the first field of the array and the partnerships first child in the second.
     *
     * @param id          Specifies the ID of the OrganicPartnership.
     * @param husband     The OrganicPerson object representing the husband.
     * @param wife        The OrganicPerson object representing the wife.
     * @param partnershipDay The marriage day specified in days since 1/1/1600.
     * @param currentDay  The current day of the simulation in days since 1/1/1600.
     * @param familyType  The type of family as defined in the enum {@link FamilyType}
     * @param population  The instance of the population of which the partnership is a member.
     * @return Returns an object array of size 2, where at index 0 can be found the newly constructed OrganicPartnership and at index 1 the partnerships child (if no child then value is null)
     */
    public static Object[] createOrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int partnershipDay, final int currentDay, final FamilyType familyType) {
        // Handle children appropriately
        OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, partnershipDay, familyType);
        // Contains OrganicPerson objects aka the children - if no children returns null
        OrganicPerson[] children = partnership.setupPartnership(husband, wife, currentDay);
        Object[] returns = new Object[children.length + 1];
        int i = 1;
        for (OrganicPerson child : children) {
            returns[i++] = child;
        }
        returns[0] = partnership;
        return returns;
    }

    private OrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int partnershipDay, final FamilyType familyType) {
        this.id = id;
        this.husband = husband.getId();
        this.wife = wife.getId();
        this.partnershipDay = partnershipDay;
        familyName = husband.getSurname();
        if (familyType == FamilyType.COHABITATION_THEN_MARRIAGE) {
            int latestDay = husband.getDeathDay();
            if (wife.getDeathDay() < latestDay) {
                latestDay = wife.getDeathDay();
            }
            try {
                cohabThenMarriageMarriageDay = partnershipDay + temporalCohabitaitonToMarriageTimeDistribution.getSample(OrganicPopulation.getCurrentDay(), 0, latestDay);
            } catch (NoPermissableValueException e) {
                cohabThenMarriageMarriageDay = partnershipDay;
            } catch (NotSetUpAtClassInitilisationException e) {
                System.err.println("Non restrited distribution called with restricted values");
            }
            if (OrganicPopulation.logging) {
                LoggingControl.timeFromCohabToMarriageDistributionLogger.log(OrganicPopulation.getCurrentDay(), cohabThenMarriageMarriageDay - partnershipDay);
            }
        }
        setCohabMarriageFlags(familyType);
    }

    /*
     * -------------------------------- SetUp methods --------------------------------
     */

    private OrganicPerson[] setupPartnership(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
        // Decide if/when partnership terminates
        if (cohabiting && !married) {
            // cohabiting
            setUpCohabitationEndEvent(male, female, currentDay);
        } else if (married) {
            // cohab then marriage / marriage
            setUpDivorceEvent(male, female);
        }
        // Decide on a number of children for relationship
        return setUpBirthPlan(male, female, currentDay);
    }

    private OrganicPerson[] setUpBirthPlan(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
        int maxPossibleChildren = (int) ((getLastPossibleBirthDate(husband, wife) - OrganicPopulation.getCurrentDay()) / (PopulationLogic.getInterChildInterval() * OrganicPopulation.getDaysPerYear()));

        try {
            if (!cohabiting && !married) {
                // Single / lone parent family
                numberOfChildrenToBeHadByCouple = temporalAffairNumberOfChildrenDistribution.getSample(OrganicPopulation.getCurrentDay(), 0, maxPossibleChildren);
                if (OrganicPopulation.logging) {
                    LoggingControl.numberOfChildrenFromAffairsDistributionLogger.log(OrganicPopulation.getCurrentDay(), numberOfChildrenToBeHadByCouple);
                }
            } else if (cohabiting && !married) {
                // cohabiting
                numberOfChildrenToBeHadByCouple = temporalChildrenNumberOfInCohabDistribution.getSample(OrganicPopulation.getCurrentDay(), 0, maxPossibleChildren);
                if (OrganicPopulation.logging) {
                    LoggingControl.numberOfChildrenFromCohabitationDistributionLogger.log(OrganicPopulation.getCurrentDay(), maxPossibleChildren);
                }
            } else if (cohabiting && married) {
                // cohab then marriage
                numberOfChildrenToBeHadByCouple = temporalChildrenNumberOfInCohabThenMarriageDistribution.getSample(OrganicPopulation.getCurrentDay(), 0, maxPossibleChildren);
                if (OrganicPopulation.logging) {
                    LoggingControl.numberOfChildrenFromCohabThenMarriageDistributionLogger.log(OrganicPopulation.getCurrentDay(), numberOfChildrenToBeHadByCouple);
                }
            } else if (!cohabiting && married) {
                // marriage
                numberOfChildrenToBeHadByCouple = temporalChildrenNumberOfInMarriageDistribution.getSample(OrganicPopulation.getCurrentDay(), 0, maxPossibleChildren);
                if (OrganicPopulation.logging) {
                    LoggingControl.numberOfChildrenFromMarriagesDistributionLogger.log(OrganicPopulation.getCurrentDay(), numberOfChildrenToBeHadByCouple);
                }
            }
        } catch (NoPermissableValueException e) {
            numberOfChildrenToBeHadByCouple = 0;
        } catch (NotSetUpAtClassInitilisationException e) {
            System.err.println("Non restrited distribution called with restricted values");
        }

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
                return new OrganicPerson[0];
            }
            return setUpBirthEvent(husband, wife, currentDay);
        } else {
            return new OrganicPerson[0];
        }

    }

    private void setUpCohabitationEndEvent(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
        int lengthOfCohab = temporalCohabitationLengthDistribution.getSample(OrganicPopulation.getCurrentDay());
        int endDayOfCohab = currentDay + lengthOfCohab;
        if (PopulationLogic.dateBeforeDeath(endDayOfCohab, male.getDeathDay())) {
            if (PopulationLogic.dateBeforeDeath(endDayOfCohab, female.getDeathDay())) {
                new OrganicEvent(EventType.END_OF_COHABITATION, this, male, female, endDayOfCohab);
                endDay = endDayOfCohab;
            } else {
                new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, male, female, female.getDeathDay());
                endDay = female.getDeathDay();
            }
        } else {
            new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, male, female, male.getDeathDay());
            endDay = male.getDeathDay();
        }

    }

    private void setUpDivorceEvent(final OrganicPerson husband, final OrganicPerson wife) {
        int actualMarriageDay = partnershipDay;
        if (cohabThenMarriageMarriageDay != -1) {
            actualMarriageDay = cohabThenMarriageMarriageDay;
        }
        if (PopulationLogic.dateBeforeDeath(actualMarriageDay + MINUMUM_DIVORCE_WINDOW, husband.getDeathDay()) && PopulationLogic.dateBeforeDeath(actualMarriageDay + MINUMUM_DIVORCE_WINDOW, wife.getDeathDay())) {
            int divorceAgeInDays;
            DivorceInstigation instigatedBy;
            try {
                switch (instigatedBy = temporalDivorceInstigatedByGenderDistribution.getSample(OrganicPopulation.getCurrentDay())) {
                    case MALE:
                        divorceAgeInDays = husband.getBirthDay() + temporalDivorceAgeForMaleDistribution.getSample(OrganicPopulation.getCurrentDay(), actualMarriageDay - husband.getBirthDay(), dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay()) - husband.getBirthDay());
                        new OrganicEvent(EventType.DIVORCE, this, husband, wife, divorceAgeInDays);
                        endDay = divorceAgeInDays;
                        divorceReason = temporalDivorceReasonMaleDistribution.getSample(OrganicPopulation.getCurrentDay());
                        if (divorceReason == DivorceReason.ADULTERY) {
                            setupAffair(wife, husband);
                        }
                        if (OrganicPopulation.logging) {
                            LoggingControl.divorceReasonMaleDistributionLogger.log(OrganicPopulation.getCurrentDay(), divorceReason);
                        }
                        break;
                    case FEMALE:
                        divorceAgeInDays = wife.getBirthDay() + temporalDivorceAgeForFemaleDistribution.getSample(OrganicPopulation.getCurrentDay(), actualMarriageDay - wife.getBirthDay(), dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay()) - wife.getBirthDay());
                        new OrganicEvent(EventType.DIVORCE, this, husband, wife, divorceAgeInDays);
                        endDay = divorceAgeInDays;
                        divorceReason = temporalDivorceReasonFemaleDistribution.getSample(OrganicPopulation.getCurrentDay());
                        if (divorceReason == DivorceReason.ADULTERY) {
                            setupAffair(husband, wife);
                        }
                        if (OrganicPopulation.logging) {
                            LoggingControl.divorceReasonFemaleDistributionLogger.log(OrganicPopulation.getCurrentDay(), divorceReason);
                        }
                        break;
                    case NO_DIVORCE:
                        // If not then added earliest death date
                        int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
                        endDay = firstPartnersDeathDate;
                        new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, husband, wife, firstPartnersDeathDate);
                        break;
                    default:
                        break;
                }
                if (OrganicPopulation.logging) {
                    LoggingControl.divorceInstiagetionByGenderDistributionLogger.log(OrganicPopulation.getCurrentDay(), instigatedBy);
                }

            } catch (NoPermissableValueException e) {
                int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
                new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, husband, wife, firstPartnersDeathDate);
                endDay = firstPartnersDeathDate;
            } catch (NotSetUpAtClassInitilisationException e) {
                System.err.println("Non restrited distribution called with restricted values");
            }
        }
    }

    private void setupAffair(final OrganicPerson marriedPerson, final OrganicPerson cheatedPerson) {
        int numberOfAffairs = temporalAffairNumberOfDistribution.getSample(OrganicPopulation.getCurrentDay());
        AffairSpacingDistribution affairDistribution = AffairSpacingDistribution.affairDistributionFactory(this, random);
        for (int i = 0; i < numberOfAffairs; i++) {
            int day = affairDistribution.getIntSample();
            OrganicEvent.addPersonToAffairsWaitingQueue(marriedPerson, day);
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
        int numberOfChildrenInPregnacy = temporalChildrenNumberOfInMaternityDistribution.getSample(OrganicPopulation.getCurrentDay());
        if (numberOfChildrenInPregnacy > numberOfChildrenToBeHadByCouple - childrenIds.size()) {
            numberOfChildrenInPregnacy = numberOfChildrenToBeHadByCouple - childrenIds.size();
        }
        if (numberOfChildrenInPregnacy == 0) {
            return new OrganicPerson[0];
        } else {
            OrganicPerson[] children = new OrganicPerson[numberOfChildrenInPregnacy];
            int dayOfBirth;
            do {
                dayOfBirth = timeBetweenMaternitiesDistrobution.getSample().intValue();
            } while (dayOfBirth <= 0 && wife.permissibleBirthDay(dayOfBirth));
            if (husband != null && wife != null && PopulationLogic.parentsHaveSensibleAgesAtChildBirth(husband.getBirthDay(), husband.getDeathDay(), wife.getBirthDay(), wife.getDeathDay(), dayOfBirth + currentDay)) {
                new OrganicEvent(EventType.BIRTH, this, husband, wife, currentDay + dayOfBirth);
                wife.addDayToRecordOfBirths(currentDay + dayOfBirth);
                for (int i = 0; i < numberOfChildrenInPregnacy; i++) {
                    children[i] = new OrganicPerson(IDFactory.getNextID(), currentDay + dayOfBirth, id, wife.getPopulation(), false, this);
                    childrenIds.add(children[i].getId());
                }
                if (OrganicPopulation.logging) {
                    LoggingControl.numberOfChildrenInMaterityDistributionLogger.log(currentDay, numberOfChildrenInPregnacy);
                }
                return children;
            } else {
                return new OrganicPerson[0];
            }
        }
    }
    
    /*
     * -------------------------------- Break up methods --------------------------------
     */

    /**
     * Instigates a divorce in the partnership.
     *
     * @param husband An OrganicPartnership object representing the male.
     * @param wife    An OrganicPartnership object representing the female.
     */
    public void divorce(final OrganicPerson husband, final OrganicPerson wife) {
        husband.populateTimeline(true);
        wife.populateTimeline(true);
    }

    /**
     * Method called to end the cohabitation and to populate the partners onwards timelines with new partnership eligibility events.
     * 
     * @param male The male of the cohabitation partnership.
     * @param female The female of the cohabitation partnership.
     */
    public void endCohabitation(final OrganicPerson male, final OrganicPerson female) {
        male.populateTimeline(false);
        female.populateTimeline(false);
    }

    /*
     * Statistical birth helper methods
     */

    private int getMeanForChildSpacingDistribution(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
        int mean = getLastPossibleBirthDate(husband, wife) - currentDay;
        if (numberOfChildrenToBeHadByCouple == 0) {
            return -1;
        }
        return (int) (mean / numberOfChildrenToBeHadByCouple);
    }

    /*
     * -------------------------------- Getters and Setters --------------------------------
     */
    
    /**
     * Family Name getter.
     *
     * @return familyName
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Returns the end date of the relationship in days since 1/1/1600.
     *
     * @return The end date of the relationship in days since 1/1/1600.
     */
    public int getEndDate() {
        return endDay;
    }

    /**
     * Returns the family type as interpreted from the marriage and cohabitation boolean values.
     * 
     * @return The family type.
     */
    public FamilyType getFamilyType() {
        if (!cohabiting && !married) {
            return FamilyType.AFFAIR;
        } else if (cohabiting && !married) {
            return FamilyType.COHABITATION;
        } else if (cohabiting && married) {
            return FamilyType.COHABITATION_THEN_MARRIAGE;
        } else if (!cohabiting && married) {
            return FamilyType.MARRIAGE;
        }
        return null;
    }
    
    /**
     * Returns the start date of the partnership in days since 1/1/1600.
     * 
     * @return The start date of the partnership in days since 1/1/1600.
     */
    public int getParntershipDay() {
        return partnershipDay;
    }

    /*
     * -------------------------------- Private method --------------------------------
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
    
    private int dateOfFirstPartnersDeath(final int husbandDeath, final int wifeDeath) {
        if (husbandDeath < wifeDeath) {
            return husbandDeath;
        } else {
            return wifeDeath;
        }
    }
    
    private void setCohabMarriageFlags(final FamilyType familyType) {
        switch (familyType) {
            case SINGLE:
                break;
            case COHABITATION:
                cohabiting = true;
                married = false;
                break;
            case COHABITATION_THEN_MARRIAGE:
                cohabiting = true;
                married = true;
                break;
            case MARRIAGE:
                cohabiting = false;
                married = true;
                break;
            default:
                break;
        }
    }
    
    /*
     * -------------------------------- Interface method --------------------------------
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
        if (getFamilyType() == FamilyType.AFFAIR || getFamilyType() == FamilyType.COHABITATION) {
            return null;
        }
        return DateManipulation.daysToDate(partnershipDay);

        // This code can be used to make cohab then marriage partnerships return the marriage date rather than the initial cohab partnership day.
        //        if (cohabThenMarriageMarriageDay == -1) {
        //            return DateManipulation.daysToDate(partnershipDay);
        //        } else {
        //            return DateManipulation.daysToDate(cohabThenMarriageMarriageDay);
        //        }
    }

    @Override
    public String getMarriagePlace() {
        return location;
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }
    
    @Override
    public int compareTo(final IPartnership o) {
        if (this.equals(o)) {
            return 0;
        } else {
            return 1;
        }
    }
    
    /*
     * -------------------------------- Distribution Getters --------------------------------
     */

    /**
     * Returns the Divorce Instigated by Gender Distribution.
     * @return The Divorce Instigated by Gender Distribution.
     */
    public static TemporalEnumDistribution<DivorceInstigation> getTemporalDivorceInstigatedByGenderDistribution() {
        return temporalDivorceInstigatedByGenderDistribution;
    }

    /**
     * Returns the Divorce Age for Males Distribution.
     * @return The Divorce Age for Males Distribution.
     */
    public static TemporalIntegerDistribution getTemporalDivorceAgeForMaleDistribution() {
        return temporalDivorceAgeForMaleDistribution;
    }

    /**
     * Returns the Divorce Age for Females Distribution.
     * @return The Divorce Age for Females Distribution.
     */
    public static TemporalIntegerDistribution getTemporalDivorceAgeForFemaleDistribution() {
        return temporalDivorceAgeForFemaleDistribution;
    }

    /**
     * Returns the Number of Children in Cohabitation Distribution.
     * @return The Number of Children in Cohabitation Distribution.
     */
    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInCohabDistribution() {
        return temporalChildrenNumberOfInCohabDistribution;
    }

    /**
     * Returns the Number of Children in Cohabitation then Marriage Distribution.
     * @return The Number of Children in Cohabitation then Marriage Distribution.
     */
    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInCohabThenMarriageDistribution() {
        return temporalChildrenNumberOfInCohabThenMarriageDistribution;
    }

    /**
     * Returns the Number of Children in Marriage Distribution.
     * @return The Number of Children in Marriage Distribution.
     */
    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInMarriageDistribution() {
        return temporalChildrenNumberOfInMarriageDistribution;
    }

    /**
     * Returns the Number of Children in Maternity Distribution.
     * @return The Number of Children in Maternity Distribution.
     */
    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInMaternityDistribution() {
        return temporalChildrenNumberOfInMaternityDistribution;
    }

    /**
     * Returns the Length of Cohabitation Distribution.
     * @return The Length of Cohabitation Distribution.
     */
    public static TemporalIntegerDistribution getTemporalCohabitationLengthDistribution() {
        return temporalCohabitationLengthDistribution;
    }

    /**
     * Returns the Number of Affairs Distribution.
     * @return The Number of Affairs Distribution.
     */
    public static TemporalIntegerDistribution getTemporalAffairNumberOfDistribution() {
        return temporalAffairNumberOfDistribution;
    }

    /**
     * Returns the Number of Children in Affairs Distribution.
     * @return The Number of Children in Affairs Distribution.
     */
    public static TemporalIntegerDistribution getTemporalAffairNumberOfChildrenDistribution() {
        return temporalAffairNumberOfChildrenDistribution;
    }

    /**
     * Returns the Divorce Reason for Males Distribution.
     * @return The Divorce Reason for Males Distribution.
     */
    public static TemporalEnumDistribution<DivorceReason> getTemporalDivorceReasonMaleDistribution() {
        return temporalDivorceReasonMaleDistribution;
    }

    /**
     * Returns the Divorce Reason for Females Distribution.
     * @return The Divorce Reason for Females Distribution.
     */
    public static TemporalEnumDistribution<DivorceReason> getTemporalDivorceReasonFemaleDistribution() {
        return temporalDivorceReasonFemaleDistribution;
    }

    /**
     * Returns the Time from Cohabitation to Marriage Distribution.
     * @return The Time from Cohabitation to Marriage Distribution.
     */
    public static TemporalIntegerDistribution getTemporalCohabitaitonToMarriageTimeDistribution() {
        return temporalCohabitaitonToMarriageTimeDistribution;
    }
}
