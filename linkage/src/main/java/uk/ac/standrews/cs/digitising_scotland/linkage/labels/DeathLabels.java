package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by al on 19/05/2014.
 */
public class DeathLabels extends CommonLabels {

    public final String TYPE = "Marriage";

    public static final String DEATH_YEAR = "death_year";
    public static final String AGE_AT_DEATH = "age_at_death";
    public static final String CHANGED_DEATH_AGE = "changed_death_age";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String OCCUPATION = "occupation";
    public static final String MARITAL_STATUS = "marital_status";
    public static final String SPOUSES_NAMES = "spouses_names";
    public static final String SPOUSES_OCCUPATIONS = "spouses_occupations";
    public static final String DEATH_MONTH = "death_month";
    public static final String DEATH_DAY = "death_day";
    public static final String PLACE_OF_DEATH = "place_of_death";
    public static final String FATHER_DECEASED = "father_deceased";
    public static final String MOTHER_DECEASED = "mother_deceased";
    public static final String COD_A = "cod_a";
    public static final String COD_B = "cod_b";
    public static final String COD_C = "cod_c";
    public static final String CERTIFYING_DOCTOR = "certifying_doctor";

    public static final Iterable<String> DEATH_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, DEATH_YEAR, AGE_AT_DEATH, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, CHANGED_DEATH_AGE,
            DATE_OF_BIRTH, OCCUPATION, MARITAL_STATUS, SPOUSES_NAMES, SPOUSES_OCCUPATIONS,   // TODO check fieldname spouses_occupations - wrong in exporter - population project
            DEATH_MONTH, DEATH_DAY,
            PLACE_OF_DEATH, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, FATHER_DECEASED, MOTHERS_FORENAME,
            MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
            MOTHER_DECEASED, COD_A, COD_B, COD_C, CERTIFYING_DOCTOR, CORRECTED_ENTRY, IMAGE_QUALITY);

    public Iterator<String> iterator() {
        return DEATH_FIELD_NAMES.iterator();
    }

}
