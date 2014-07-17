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

    public static boolean parentsHaveSensibleAgesAtChildBirth(final IPerson father, final IPerson mother, final IPerson child) {

        Date mother_birth_date = mother.getBirthDate();
        Date mother_death_date = mother.getDeathDate();

        Date father_birth_date = father.getBirthDate();
        Date father_death_date = father.getDeathDate();

        Date child_birth_date = child.getBirthDate();

        return parentsHaveSensibleAgesAtChildBirth(father_birth_date, father_death_date, mother_birth_date, mother_death_date, child_birth_date);
    }

    public static boolean parentsHaveSensibleAgesAtChildBirth(final int father_birth_date, final int father_death_date, final int mother_birth_date, final int mother_death_date, final int child_birth_date) {

        return parentsHaveSensibleAgesAtChildBirth(
                DateManipulation.daysToDate(father_birth_date),
                DateManipulation.daysToDate(father_death_date),
                DateManipulation.daysToDate(mother_birth_date),
                DateManipulation.daysToDate(mother_death_date),
                DateManipulation.daysToDate(child_birth_date));
    }

    public static boolean parentsHaveSensibleAgesAtChildBirth(final Date father_birth_date, final Date father_death_date, final Date mother_birth_date, final Date mother_death_date, final Date child_birth_date) {

        return motherAliveAtBirth(mother_death_date, child_birth_date) &&
                motherNotTooYoungAtBirth(mother_birth_date, child_birth_date) &&
                motherNotTooOldAtBirth(mother_birth_date, child_birth_date) &&
                fatherAliveAtConception(father_death_date, child_birth_date) &&
                fatherNotTooYoungAtBirth(father_birth_date, child_birth_date) &&
                fatherNotTooOldAtBirth(father_birth_date, child_birth_date);
    }

    public static int earliestAcceptableBirthDate(final int marriage_date, final int previous_child_birth_date) {

        return previous_child_birth_date == 0 ? DateManipulation.addYears(marriage_date, TIME_BEFORE_FIRST_CHILD) : DateManipulation.addYears(previous_child_birth_date, INTER_CHILD_INTERVAL);
    }

    public static boolean partnerAgeDifferenceIsReasonable(final int person_birth_date, final int candidate_birth_date) {

        return Math.abs(DateManipulation.differenceInYears(person_birth_date, candidate_birth_date)) <= MAXIMUM_AGE_DIFFERENCE_IN_PARTNERSHIP;
    }

    public static boolean longEnoughBetweenMarriages(final int candidate_marriage_date, final int previous_marriage_date) {

        return DateManipulation.differenceInYears(previous_marriage_date, candidate_marriage_date) > MINIMUM_PERIOD_BETWEEN_PARTNERSHIPS;
    }
    
    public static boolean divorceNotBeforeMarriage(final int marriage_date, final int divorce_date) {
    	
    	return DateManipulation.differenceInDays(marriage_date, divorce_date) > 0;
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
