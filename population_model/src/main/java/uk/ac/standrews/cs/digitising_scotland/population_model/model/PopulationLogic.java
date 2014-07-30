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
package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;

/**
 * Created by graham on 03/07/2014.
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public class PopulationLogic {

    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 50;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_FATHER_AGE_AT_CHILDBIRTH = 70;

    private static final int INTER_CHILD_INTERVAL = 3;
    private static final int TIME_BEFORE_FIRST_CHILD = 1;

    private static final int MAXIMUM_AGE_DIFFERENCE_IN_PARTNERSHIP = 15;
    private static final int MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS = 7;

    /**
     * Checks whether the ages of the given parents are sensible for the given child.
     *
     * @param father the father
     * @param mother the mother
     * @param child  the child
     * @return true if the parents' ages are sensible
     */
    @SuppressWarnings("FeatureEnvy")
    public static boolean parentsHaveSensibleAgesAtChildBirth(final IPerson father, final IPerson mother, final IPerson child) {

        Date mother_birth_date = mother.getBirthDate();
        Date mother_death_date = mother.getDeathDate();

        Date father_birth_date = father.getBirthDate();
        Date father_death_date = father.getDeathDate();

        Date child_birth_date = child.getBirthDate();

        return parentsHaveSensibleAgesAtChildBirth(father_birth_date, father_death_date, mother_birth_date, mother_death_date, child_birth_date);
    }

    /**
     * Checks whether the ages of the given parents are sensible for the given child.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param father_birth_date the birth date of the father
     * @param father_death_date the death date of the father
     * @param mother_birth_date the birth date of the mother
     * @param mother_death_date the death date of the mother
     * @param child_birth_date  the birth date of the child
     * @return true if the parents' ages are sensible
     */
    @SuppressWarnings("FeatureEnvy")
    public static boolean parentsHaveSensibleAgesAtChildBirth(final int father_birth_date, final int father_death_date, final int mother_birth_date, final int mother_death_date, final int child_birth_date) {

        return parentsHaveSensibleAgesAtChildBirth(
                DateManipulation.daysToDate(father_birth_date),
                DateManipulation.daysToDate(father_death_date),
                DateManipulation.daysToDate(mother_birth_date),
                DateManipulation.daysToDate(mother_death_date),
                DateManipulation.daysToDate(child_birth_date));
    }

    /**
     * Returns the earliest possible child birth date, for a given marriage date and optional previous child birth date.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param marriage_date             the marriage date
     * @param previous_child_birth_date the previous child birth date, or -1 if there is no previous birth
     * @return the earliest possible birth date
     */
    public static int earliestAcceptableBirthDate(final int marriage_date, final int previous_child_birth_date) {

        return previous_child_birth_date == -1 ? DateManipulation.addYears(marriage_date, TIME_BEFORE_FIRST_CHILD) : DateManipulation.addYears(previous_child_birth_date, INTER_CHILD_INTERVAL);
    }

    /**
     * Checks whether the age difference between the two prospective partners is reasonable.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param birth_date1 the first birth date
     * @param birth_date2 the second birth date
     * @return true if the age difference is reasonable
     */
    public static boolean partnerAgeDifferenceIsReasonable(final int birth_date1, final int birth_date2) {

        return Math.abs(DateManipulation.differenceInYears(birth_date1, birth_date2)) <= MAXIMUM_AGE_DIFFERENCE_IN_PARTNERSHIP;
    }

    /**
     * Checks whether there is a long enough period between the two given marriage dates.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param marriage_date          the prospective marriage date
     * @param previous_marriage_date the previous marriage date
     * @return true if the period is long enough
     */
    public static boolean longEnoughBetweenMarriages(final int marriage_date, final int previous_marriage_date) {

        return DateManipulation.differenceInYears(previous_marriage_date, marriage_date) > MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS;
    }

    /**
     * Checks whether the given divorce date is after the given marriage date.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param divorce_date  the divorce date
     * @param marriage_date the marriage date
     * @return true if the divorce date is after the marriage date
     */
    public static boolean divorceAfterMarriage(final int divorce_date, final int marriage_date) {

        return DateManipulation.differenceInDays(marriage_date, divorce_date) > 0;
    }

    /**
     * Checks whether the given divorce date is before the given death date.
     * Dates are expressed as defined in {@link uk.ac.standrews.cs.digitising_scotland.util.DateManipulation#dateToDays(java.util.Date)}.
     *
     * @param divorce_date the divorce date
     * @param death_date   the death date
     * @return true if the divorce date is after the marriage date
     */
    public static boolean divorceBeforeDeath(final int divorce_date, final int death_date) {

        return DateManipulation.differenceInDays(divorce_date, death_date) > 0;
    }

    /**
     * Returns the maximum mother's age at child birth.
     *
     * @return the maximum mother's age at child birth
     */
    public static int getMaximumMotherAgeAtChildBirth() {
        return MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH;
    }

    /**
     * Returns the minimum mother's age at child birth.
     *
     * @return the minimum mother's age at child birth
     */
    public static int getMinimumMotherAgeAtChildBirth() {
        return MINIMUM_MOTHER_AGE_AT_CHILDBIRTH;
    }

    /**
     * Returns the maximum father's age at child birth.
     *
     * @return the maximum father's age at child birth
     */
    public static int getMaximumFathersAgeAtChildBirth() {
        return MAXIMUM_FATHER_AGE_AT_CHILDBIRTH;
    }

    /**
     * Returns the minimum father's age at child birth.
     *
     * @return the minimum father's age at child birth
     */
    public static int getMinimumFathersAgeAtChildBirth() {
        return MINIMUM_FATHER_AGE_AT_CHILDBIRTH;
    }

    /**
     * Returns the mean inter-child interval in years.
     *
     * @return the mean inter-child interval in years
     */
    public static int getInterChildInterval() {
        // TODO is the Javadoc right?
        return INTER_CHILD_INTERVAL;
    }

    private static boolean parentsHaveSensibleAgesAtChildBirth(final Date father_birth_date, final Date father_death_date, final Date mother_birth_date, final Date mother_death_date, final Date child_birth_date) {

        return motherAliveAtBirth(mother_death_date, child_birth_date) &&
                motherNotTooYoungAtBirth(mother_birth_date, child_birth_date) &&
                motherNotTooOldAtBirth(mother_birth_date, child_birth_date) &&
                fatherAliveAtConception(father_death_date, child_birth_date) &&
                fatherNotTooYoungAtBirth(father_birth_date, child_birth_date) &&
                fatherNotTooOldAtBirth(father_birth_date, child_birth_date);
    }

    private static boolean motherAliveAtBirth(final Date mother_death_date, final Date child_birth_date) {

        return mother_death_date == null || dateNotAfter(child_birth_date, mother_death_date);
    }

    private static boolean motherNotTooYoungAtBirth(final Date mother_birth_date, final Date child_birth_date) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother_birth_date, child_birth_date);

        return notLessThan(mothers_age_at_birth, MINIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean motherNotTooOldAtBirth(final Date mother_birth_date, final Date child_birth_date) {

        final int mothers_age_at_birth = parentsAgeAtChildBirth(mother_birth_date, child_birth_date);

        return notGreaterThan(mothers_age_at_birth, MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherAliveAtConception(final Date father_death_date, final Date child_birth_date) {

        return father_death_date == null || dateNotAfter(child_birth_date, DateManipulation.addDays(father_death_date, MAX_GESTATION_IN_DAYS));
    }

    private static boolean fatherNotTooYoungAtBirth(final Date father_birth_date, final Date child_birth_date) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father_birth_date, child_birth_date);

        return notLessThan(fathers_age_at_birth, MINIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static boolean fatherNotTooOldAtBirth(final Date father_birth_date, final Date child_birth_date) {

        final int fathers_age_at_birth = parentsAgeAtChildBirth(father_birth_date, child_birth_date);

        return notGreaterThan(fathers_age_at_birth, MAXIMUM_FATHER_AGE_AT_CHILDBIRTH);
    }

    private static int parentsAgeAtChildBirth(final Date parent_birth_date, final Date child_birth_date) {

        return DateManipulation.differenceInYears(parent_birth_date, child_birth_date);
    }

    private static boolean notLessThan(final int i1, final int i2) {

        return i1 >= i2;
    }

    private static boolean notGreaterThan(final int i1, final int i2) {

        return i1 <= i2;
    }

    private static boolean dateNotAfter(final Date date1, final Date date2) {

        return DateManipulation.differenceInDays(date1, date2) >= 0;
    }
}
