package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 19/05/2014.
 */
public class Marriage extends CommonLabels {

    public final String TYPE = "Marriage";

    public static final String GROOM_SURNAME = "groom_surname";
    public static final String GROOM_FORENAME = "groom_forename";
    public static final String BRIDE_SURNAME = "bride_surname";
    public static final String BRIDE_FORENAME = "bride_forename";
    public static final String MARRIAGE_YEAR = "marriage_year";
    public static final String CHANGED_GROOM_SURNAME = "changed_groom_surname";
    public static final String CHANGED_GROOM_FORENAME = "changed_groom_forename";
    public static final String CHANGED_BRIDE_SURNAME = "changed_bride_surname";
    public static final String CHANGED_BRIDE_FORENAME = "changed_bride_forename";
    public static final String GROOM_DID_NOT_SIGN = "groom_did_not_sign";
    public static final String BRIDE_DID_NOT_SIGN = "bride_did_not_sign";
    public static final String MARRIAGE_DAY = "marriage_day";
    public static final String MARRIAGE_MONTH = "marriage_month";
    public static final String DENOMINATION = "denomination";
    public static final String GROOM_ADDRESS = "groom_address";
    public static final String GROOM_AGE_OR_DATE_OF_BIRTH = "groom_age_or_date_of_birth";
    public static final String GROOM_OCCUPATION = "groom_occupation";
    public static final String GROOM_MARITAL_STATUS = "groom_marital_status";
    public static final String BRIDE_ADDRESS = "bride_address";
    public static final String BRIDE_AGE_OR_DATE_OF_BIRTH = "bride_age_or_date_of_birth";
    public static final String BRIDE_OCCUPATION = "bride_occupation";
    public static final String BRIDE_MARITAL_STATUS = "bride_marital_status";
    public static final String GROOM_FATHERS_FORENAME = "groom_fathers_forename";
    public static final String GROOM_FATHERS_SURNAME = "groom_fathers_surname";
    public static final String GROOM_FATHER_DECEASED = "groom_father_deceased";
    public static final String GROOM_MOTHERS_FORENAME = "groom_mothers_forename";
    public static final String GROOM_MOTHERS_MAIDEN_SURNAME = "groom_mothers_maiden_surname";
    public static final String GROOM_MOTHER_DECEASED = "groom_mother_deceased";
    public static final String GROOM_FATHERS_OCCUPATION = "groom_fathers_occupation";
    public static final String BRIDE_FATHERS_FORENAME = "bride_fathers_forename";
    public static final String BRIDE_FATHERS_SURNAME = "bride_fathers_surname";
    public static final String BRIDE_FATHER_DECEASED = "bride_father_deceased";
    public static final String BRIDE_MOTHERS_FORENAME = "bride_mothers_forename";
    public static final String BRIDE_MOTHERS_MAIDEN_SURNAME = "bride_mothers_maiden_surname";
    public static final String BRIDE_MOTHER_DECEASED = "bride_mother_deceased";
    public static final String BRIDE_FATHER_OCCUPATION = "bride_father_occupation";

    public static final Iterable<String> MARRIAGE_FIELD_NAMES = Arrays.asList(ID, GROOM_SURNAME, GROOM_FORENAME, BRIDE_SURNAME, BRIDE_FORENAME, YEAR_OF_REGISTRATION,
            REGISTRATION_DISTRICT_NUMBER, ENTRY, MARRIAGE_YEAR, CHANGED_GROOM_SURNAME, CHANGED_GROOM_FORENAME,
            CHANGED_BRIDE_SURNAME, CHANGED_BRIDE_FORENAME, GROOM_DID_NOT_SIGN,
            BRIDE_DID_NOT_SIGN, MARRIAGE_DAY, MARRIAGE_MONTH, DENOMINATION, GROOM_ADDRESS,
            GROOM_AGE_OR_DATE_OF_BIRTH, GROOM_OCCUPATION,
            GROOM_MARITAL_STATUS, BRIDE_ADDRESS, BRIDE_AGE_OR_DATE_OF_BIRTH,
            BRIDE_OCCUPATION, BRIDE_MARITAL_STATUS, GROOM_FATHERS_FORENAME,
            GROOM_FATHERS_SURNAME, GROOM_FATHER_DECEASED, GROOM_MOTHERS_FORENAME, GROOM_MOTHERS_MAIDEN_SURNAME,
            GROOM_MOTHER_DECEASED, GROOM_FATHERS_OCCUPATION, BRIDE_FATHERS_FORENAME, BRIDE_FATHERS_SURNAME,
            BRIDE_FATHER_DECEASED, BRIDE_MOTHERS_FORENAME, BRIDE_MOTHERS_MAIDEN_SURNAME, BRIDE_MOTHER_DECEASED,
            BRIDE_FATHER_OCCUPATION, CORRECTED_ENTRY, IMAGE_QUALITY);

    public Iterable<String> get_field_names() {
        return MARRIAGE_FIELD_NAMES;
    }

    @Override
    public String get_type() {
        return TYPE;
    }

}
