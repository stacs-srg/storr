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
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDivorceInstigatedByGenderDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalDivorceReasonDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal.TemporalIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IDFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.IPartnership;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.PopulationLogic;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.LoggingControl;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger.OrganicPopulationLogger;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * The OrganicPartnership class models the different types of possibly partnership in the model.
 * 
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public final class OrganicPartnership implements IPartnership {


    // Universal partnership distributions
    private static Random random = RandomFactory.getRandom();
    private static TemporalDivorceInstigatedByGenderDistribution temporalDivorceInstigatedByGenderDistribution;
    private static TemporalIntegerDistribution temporalDivorceAgeForMaleDistribution;
    private static TemporalIntegerDistribution temporalDivorceAgeForFemaleDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInMarriageOrCohabDistribution;
    private static TemporalIntegerDistribution temporalChildrenNumberOfInMaternityDistribution;
    private static TemporalIntegerDistribution temporalCohabitationLengthDistribution;
    private static TemporalIntegerDistribution temporalAffairNumberOfDistribution;
    private static TemporalIntegerDistribution temporalAffairNumberOfChildrenDistribution;

    private static TemporalDivorceReasonDistribution temporalDivorceReasonMaleDistribution;
    private static TemporalDivorceReasonDistribution temporalDivorceReasonFemaleDistribution;

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

    private OrganicPopulation population;

    private int cohabThenMarriageMarriageDay = -1;

    private boolean on;
    private int numberOfChildrenToBeHadByCouple;
    private NormalDistribution timeBetweenMaternitiesDistrobution;
    private DivorceReason divorceReason;

    /*
     * Factory and constructor
     */

    /**
     * Setups the classes temporal distributions as runtime.
     * 
     * @param population The instance of the population to which the distributions are to pertain.
     */
    public static void setupTemporalDistributionsInOrganicPartnershipClass(final OrganicPopulation population) {
        temporalChildrenNumberOfInMarriageOrCohabDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_marriage_or_cohab_distributions_filename", random, true);
        temporalDivorceInstigatedByGenderDistribution = new TemporalDivorceInstigatedByGenderDistribution(population, "divorce_instigated_by_gender_distributions_filename", random);
        temporalDivorceAgeForMaleDistribution = new TemporalIntegerDistribution(population, "divorce_age_for_male_distributions_data_filename", random, false);
        temporalDivorceAgeForFemaleDistribution = new TemporalIntegerDistribution(population, "divorce_age_for_female_distributions_data_filename", random, false);
        temporalChildrenNumberOfInMaternityDistribution = new TemporalIntegerDistribution(population, "children_number_of_in_maternity_distributions_data_filename", random, false);
        temporalCohabitationLengthDistribution = new TemporalIntegerDistribution(population, "cohabitation_length_distributions_data_filename", random, false);
        temporalDivorceReasonMaleDistribution = new TemporalDivorceReasonDistribution(population, "divorce_reason_male_distributions_data_filename", random);
        temporalDivorceReasonFemaleDistribution = new TemporalDivorceReasonDistribution(population, "divorce_reason_female_distributions_data_filename", random);
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
    public static Object[] createOrganicPartnership(final int id, final OrganicPerson husband, final OrganicPerson wife, final int partnershipDay, final int currentDay, final FamilyType familyType, final OrganicPopulation population) {
        // Handle children appropriately
        OrganicPartnership partnership = new OrganicPartnership(id, husband, wife, partnershipDay, familyType, population);
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
        // TODO inheritance of male name - need consideration of naming if not coming from a marriage
        // if (familyType == FamilyType.MARRIAGE) {
        familyName = husband.getSurname();
        //		}
        if (familyType == FamilyType.COHABITATION_THEN_MARRIAGE) {
            int latestDay = husband.getDeathDay();
            if (wife.getDeathDay() < latestDay) {
                latestDay = wife.getDeathDay();
            }
            try {
                cohabThenMarriageMarriageDay = partnershipDay + temporalCohabitaitonToMarriageTimeDistribution.getSample(population.getCurrentDay(), 0, latestDay);
            } catch (NoPermissableValueException e) {
                cohabThenMarriageMarriageDay = partnershipDay;
            } catch (NotSetUpAtClassInitilisationException e) {
                System.err.println("Non restrited distribution called with restricted values");
            }
        }
        this.turnOn();
        setCohabMarriageFlags(familyType);
    }

    /*
     * High level methods
     */

    private OrganicPerson[] createPartnershipTimeline(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
        // Decide if/when partnership terminates
        if (!cohabiting && !married) {
            // Single / lone parent family
            // model affairs in here
            // Nothing to do here I think, end needs to be set once child births have been decided.
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
     * SetUp methods
     */

    private OrganicPerson[] setUpBirthPlan(final OrganicPerson husband, final OrganicPerson wife, final int currentDay) {
        int maxPossibleChildren = (int) ((getLastPossibleBirthDate(husband, wife) - population.getCurrentDay()) / (PopulationLogic.getInterChildInterval() * OrganicPopulation.getDaysPerYear()));
        try {
            if (!cohabiting && !married) {
                // Single / lone parent family
                numberOfChildrenToBeHadByCouple = temporalAffairNumberOfChildrenDistribution.getSample(population.getCurrentDay(), 0, maxPossibleChildren);
            } else if (cohabiting && !married) {
                // cohabiting
                numberOfChildrenToBeHadByCouple = temporalChildrenNumberOfInMarriageOrCohabDistribution.getSample(population.getCurrentDay(), 0, maxPossibleChildren);
            } else if (cohabiting && married) {
                // cohab then marriage / marriage
                numberOfChildrenToBeHadByCouple = temporalChildrenNumberOfInMarriageOrCohabDistribution.getSample(population.getCurrentDay(), 0, maxPossibleChildren);
                LoggingControl.numberOfChildrenFromMarriagesDistributionLogger.log(population.getCurrentDay(), numberOfChildrenToBeHadByCouple);
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
                if (!cohabiting && !married) {
                    setUpAffairEndEvent(currentDay, husband, wife);
                }
                return new OrganicPerson[0];
            }
            return setUpBirthEvent(husband, wife, currentDay);
        } else {
            if (!cohabiting && !married) {
                setUpAffairEndEvent(currentDay, husband, wife);
            }
            return new OrganicPerson[0];
        }

    }

    private void setUpAffairEndEvent(final int currentDay, final OrganicPerson male, final OrganicPerson female) {
        new OrganicEvent(EventType.END_OF_AFFAIR, this, male, female, currentDay + 1);
    }

    private void setUpCohabitationEndEvent(final OrganicPerson male, final OrganicPerson female, final int currentDay) {
        int lengthOfCohab = temporalCohabitationLengthDistribution.getSample(population.getCurrentDay());
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
            try {
                switch (temporalDivorceInstigatedByGenderDistribution.getSample(population.getCurrentDay())) {
                    case MALE:
                        divorceAgeInDays = husband.getBirthDay() + temporalDivorceAgeForMaleDistribution.getSample(population.getCurrentDay(), actualMarriageDay - husband.getBirthDay(), dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay()) - husband.getBirthDay());
                        new OrganicEvent(EventType.DIVORCE, this, husband, wife, divorceAgeInDays);
                        endDay = divorceAgeInDays;
                        divorceReason = temporalDivorceReasonMaleDistribution.getSample(population.getCurrentDay());
                        if (divorceReason == DivorceReason.ADULTERY) {
                            setupAffair(wife, husband);
                        }
                        break;
                    case FEMALE:
                        divorceAgeInDays = wife.getBirthDay() + temporalDivorceAgeForFemaleDistribution.getSample(population.getCurrentDay(), actualMarriageDay - wife.getBirthDay(), dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay()) - wife.getBirthDay());
                        new OrganicEvent(EventType.DIVORCE, this, husband, wife, divorceAgeInDays);
                        endDay = divorceAgeInDays;
                        divorceReason = temporalDivorceReasonFemaleDistribution.getSample(population.getCurrentDay());
                        if (divorceReason == DivorceReason.ADULTERY) {
                            setupAffair(husband, wife);
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
            } catch (NoPermissableValueException e) {
                int firstPartnersDeathDate = dateOfFirstPartnersDeath(husband.getDeathDay(), wife.getDeathDay());
                new OrganicEvent(EventType.PARTNERSHIP_ENDED_BY_DEATH, this, husband, wife, firstPartnersDeathDate);
                endDay = firstPartnersDeathDate;
            } catch (NotSetUpAtClassInitilisationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void setupAffair(final OrganicPerson marriedPerson, final OrganicPerson cheatedPerson) {
        int numberOfAffairs = temporalAffairNumberOfDistribution.getSample(population.getCurrentDay());
        AffairSpacingDistribution affairDistribution = AffairSpacingDistribution.affairDistributionFactory(this, random);
        for (int i = 0; i < numberOfAffairs; i++) {
            int day = affairDistribution.getIntSample();
            marriedPerson.getPopulation().addPersonToAffairsWaitingQueue(marriedPerson, day);
            new OrganicEvent(EventType.AFFAIR, marriedPerson, day);
            if (marriedPerson.getSex() == 'M') {
                new OrganicEvent(EventType.MALE_BEGINS_AFFAIR, this, marriedPerson, cheatedPerson, day);
            } else {
                new OrganicEvent(EventType.FEMALE_BEGINS_AFFAIR, this, marriedPerson, cheatedPerson, day);
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
        int numberOfChildrenInPregnacy = temporalChildrenNumberOfInMaternityDistribution.getSample(population.getCurrentDay());
        if (numberOfChildrenInPregnacy > numberOfChildrenToBeHadByCouple - childrenIds.size()) {
            numberOfChildrenInPregnacy = numberOfChildrenToBeHadByCouple - childrenIds.size();
            // FIXME Loosing children here
        }
        if (numberOfChildrenInPregnacy == 0) {
            if (!cohabiting && !married && lastChildBorn()) {
                setUpAffairEndEvent(currentDay, husband, wife);
            }
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
                if (!cohabiting && !married && lastChildBorn()) {
                    setUpAffairEndEvent(currentDay + dayOfBirth + 1, husband, wife);
                }
                return children;
            } else {
                OrganicPopulationLogger.incStopedHavingEarlyDeaths(numberOfChildrenToBeHadByCouple - children.length);
                if (!cohabiting && !married && lastChildBorn()) {
                    setUpAffairEndEvent(currentDay, husband, wife);
                }
                return new OrganicPerson[0];
            }
        }
    }

    /**
     * Checks if the last child to be born in the partnership has been.
     * 
     * @return Boolean value of true if the last child has been born, else false.
     */
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
        turnOff();
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
     * 
     * @param familyName The family name.
     */
    public void setFamilyName(final String familyName) {
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
     * Returns the population of which the partnership is a member of.
     * 
     * @return The population of which the partnership is a member of.
     */
    public OrganicPopulation getPopulation() {
        return population;
    }

    /**
     * Sets the timeline of the partnership.
     *
     * @param timeline The timeline to be set to.
     */

    /**
     * Returns the end date of the relationship in days since 1/1/1600.
     *
     * @return The end date of the relationship in days since 1/1/1600.
     */
    public int getEndDate() {
        return endDay;
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
        
//        if (cohabThenMarriageMarriageDay == -1) {
//            return DateManipulation.daysToDate(partnershipDay);
//        } else {
//            return DateManipulation.daysToDate(cohabThenMarriageMarriageDay);
//        }
    }
    
    public int getParntershipDay() {
        return partnershipDay;
    }

    @Override
    public String getMarriagePlace() {
        return location;
    }

    @Override
    public List<Integer> getChildIds() {
        return childrenIds;
    }

    private void setCohabMarriageFlags(final FamilyType familyType) {
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
    
    /**
     * RETURNS MARRIAGE FOR BOTH MARRIAGE AND MARRIAGE THEN COHABITATATION.
     * 
     * @return The family type.
     */
    public FamilyType getFamilyType() {
        if (!cohabiting && !married) {
            return FamilyType.AFFAIR;
        } else if (cohabiting && !married) {
            return FamilyType.COHABITATION;
        } else {
            return FamilyType.MARRIAGE;
        }
        
    }

    public static TemporalDivorceInstigatedByGenderDistribution getTemporalDivorceInstigatedByGenderDistribution() {
        return temporalDivorceInstigatedByGenderDistribution;
    }

    public static TemporalIntegerDistribution getTemporalDivorceAgeForMaleDistribution() {
        return temporalDivorceAgeForMaleDistribution;
    }

    public static TemporalIntegerDistribution getTemporalDivorceAgeForFemaleDistribution() {
        return temporalDivorceAgeForFemaleDistribution;
    }

    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInMarriageOrCohabDistribution() {
        return temporalChildrenNumberOfInMarriageOrCohabDistribution;
    }

    public static TemporalIntegerDistribution getTemporalChildrenNumberOfInMaternityDistribution() {
        return temporalChildrenNumberOfInMaternityDistribution;
    }

    public static TemporalIntegerDistribution getTemporalCohabitationLengthDistribution() {
        return temporalCohabitationLengthDistribution;
    }

    public static TemporalIntegerDistribution getTemporalAffairNumberOfDistribution() {
        return temporalAffairNumberOfDistribution;
    }

    public static TemporalIntegerDistribution getTemporalAffairNumberOfChildrenDistribution() {
        return temporalAffairNumberOfChildrenDistribution;
    }

    public static TemporalDivorceReasonDistribution getTemporalDivorceReasonMaleDistribution() {
        return temporalDivorceReasonMaleDistribution;
    }

    public static TemporalDivorceReasonDistribution getTemporalDivorceReasonFemaleDistribution() {
        return temporalDivorceReasonFemaleDistribution;
    }

    public static TemporalIntegerDistribution getTemporalCohabitaitonToMarriageTimeDistribution() {
        return temporalCohabitaitonToMarriageTimeDistribution;
    }
}
