package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by al on 19/05/2014.
 */
public class DeathTypeLabel extends CommonTypeLabel {

    public static final String TYPE = DeathTypeLabel.class.getName();

    public static final String DEATH_YEAR = "death_year";
    public static final String AGE_AT_DEATH = "age_at_death";
    public static final String CHANGED_DEATH_AGE = "changed_death_age";
    public static final String DATE_OF_BIRTH = "birth_date";
    public static final String OCCUPATION = "occupation";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String SPOUSES_NAMES = "spouses_names";
    public static final String SPOUSES_OCCUPATIONS = "spouses_occupations";
    public static final String DEATH_MONTH = "death_month";
    public static final String DEATH_DAY = "death_day";
    public static final String PLACE_OF_DEATH = "death_place";
    public static final String FATHER_DECEASED = "father_deceased";
    public static final String MOTHER_DECEASED = "mother_deceased";
    public static final String COD_A = "cod_a";
    public static final String COD_B = "cod_b";
    public static final String COD_C = "cod_c";
    public static final String CERTIFYING_DOCTOR = "certifying_doctor";

    public static final String FATHERS_FORENAME = "fathers_forename";   // TODO check these - there are too many in here - these are not all common!
    public static final String FATHERS_SURNAME = "fathers_surname";
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    public static final String MOTHERS_FORENAME = "mothers_forename";
    public static final String MOTHERS_SURNAME = "mothers_surname";

    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    public static final String CHANGED_SURNAME = "changed_surname";
    public static final String CHANGED_FORENAME = "changed_forename";
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    public static final Collection<String> DEATH_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, DEATH_YEAR, AGE_AT_DEATH, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, CHANGED_DEATH_AGE,
            DATE_OF_BIRTH, OCCUPATION, MARITAL_STATUS, SPOUSES_NAMES, SPOUSES_OCCUPATIONS,
            DEATH_MONTH, DEATH_DAY,
            PLACE_OF_DEATH, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, FATHER_DECEASED, MOTHERS_FORENAME,
            MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
            MOTHER_DECEASED, COD_A, COD_B, COD_C, CERTIFYING_DOCTOR, CORRECTED_ENTRY, IMAGE_QUALITY);

    public Collection<String> getLabels() {
        return DEATH_FIELD_NAMES;
    }

    @Override
    public int getId() { // TODO delete this class!
        return -1;
    }


}
