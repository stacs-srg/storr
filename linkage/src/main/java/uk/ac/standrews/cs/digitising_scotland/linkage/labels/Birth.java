package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 19/05/2014.
 */
public class Birth extends CommonLabels {

    public static final String TYPE = "birth";

    public static final String BIRTH_YEAR = "birth_year";
    public static final String BIRTH_DAY = "birth_day";
    public static final String BIRTH_MONTH = "birth_month";
    public static final String BIRTH_ADDRESS = "birth_address";

    public static final String PARENTS_DAY_OF_MARRIAGE = "parents_day_of_marriage";
    public static final String PARENTS_MONTH_OF_MARRIAGE = "parents_month_of_marriage";
    public static final String PARENTS_YEAR_OF_MARRIAGE = "parents_year_of_marriage";
    public static final String PARENTS_PLACE_OF_MARRIAGE = "parents_place_of_marriage";
    public static final String ILLEGITIMATE_INDICATOR = "illegitimate_indicator";
    public static final String INFORMANT = "informant";
    public static final String INFORMANT_DID_NOT_SIGN =  "informant_did_not_sign";

    public static final String ADOPTION = "adoption";

    
    public static final Iterable<String> BIRTH_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
    REGISTRATION_DISTRICT_SUFFIX, ENTRY, BIRTH_YEAR, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, BIRTH_DAY, BIRTH_MONTH,
    BIRTH_ADDRESS, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
    PARENTS_DAY_OF_MARRIAGE, PARENTS_MONTH_OF_MARRIAGE, PARENTS_YEAR_OF_MARRIAGE, PARENTS_PLACE_OF_MARRIAGE, ILLEGITIMATE_INDICATOR, INFORMANT,
    INFORMANT_DID_NOT_SIGN, CORRECTED_ENTRY, ADOPTION, IMAGE_QUALITY);

    @Override
    public Iterable<String> get_field_names() {
        return BIRTH_FIELD_NAMES;
    }

    @Override
    public String get_type() {
        return TYPE;
    }
}
