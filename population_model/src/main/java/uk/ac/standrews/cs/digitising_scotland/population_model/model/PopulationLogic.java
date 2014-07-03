package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;

import java.util.Date;

/**
 * Created by graham on 03/07/2014.
 */
public class PopulationLogic {

    private static final int MINIMUM_MOTHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_MOTHER_AGE_AT_CHILDBIRTH = 50;
    private static final int MAX_GESTATION_IN_DAYS = 300;
    private static final int MINIMUM_FATHER_AGE_AT_CHILDBIRTH = 12;
    private static final int MAXIMUM_FATHER_AGE_AT_CHILDBIRTH = 70;

    public static boolean parentsHaveSensibleAgesAtChildBirth(final IPerson father, final IPerson mother, final IPerson child) {

        Date mother_birth_date = mother.getBirthDate();
        Date mother_death_date = mother.getDeathDate();

        Date father_birth_date = father.getBirthDate();
        Date father_death_date = father.getDeathDate();

        Date child_birth_date = child.getBirthDate();


        boolean result = parentsHaveSensibleAgesAtChildBirth(father_birth_date, father_death_date, mother_birth_date, mother_death_date, child_birth_date);

        if (!result) {

            System.out.println("Error case:");

            System.out.println("mother birth: " + mother_birth_date);
            System.out.println("mother death: " + mother_death_date);
            System.out.println("father birth: " + father_birth_date);
            System.out.println("father death: " + father_death_date);
            System.out.println("child birth: " + child_birth_date);
            System.out.println();
        }
        return result;
    }

    public static boolean parentsHaveSensibleAgesAtChildBirth(int father_birth_date, int father_death_date, int mother_birth_date, int mother_death_date, int child_birth_date) {

        return parentsHaveSensibleAgesAtChildBirth(
                DateManipulation.daysToDate(father_birth_date),
                DateManipulation.daysToDate(father_death_date),
                DateManipulation.daysToDate(mother_birth_date),
                DateManipulation.daysToDate(mother_death_date),
                DateManipulation.daysToDate(child_birth_date));
    }

    public static boolean parentsHaveSensibleAgesAtChildBirth(Date father_birth_date, Date father_death_date, Date mother_birth_date, Date mother_death_date, Date child_birth_date) {

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
