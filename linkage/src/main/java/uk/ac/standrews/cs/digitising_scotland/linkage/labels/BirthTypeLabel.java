package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 19/05/2014.
 */
public class BirthTypeLabel extends CommonTypeLabel {

    public static final String TYPE = BirthTypeLabel.class.getName();

    public static final String BIRTH_YEAR = "birth_year";
    public static final String BIRTH_DAY = "birth_day";
    public static final String BIRTH_MONTH = "birth_month";
    public static final String BIRTH_ADDRESS = "birth_address";

    public static final String FATHERS_FORENAME = "fathers_forename";   // TODO check these - there are too many in here - these are not all common!
    public static final String FATHERS_SURNAME = "fathers_surname";
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    public static final String MOTHERS_FORENAME = "mothers_forename";
    public static final String MOTHERS_SURNAME = "mothers_surname";

    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    public static final String CHANGED_SURNAME = "changed_surname";
    public static final String CHANGED_FORENAME = "changed_forename";
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    public static final String PARENTS_DAY_OF_MARRIAGE = "parents_day_of_marriage";
    public static final String PARENTS_MONTH_OF_MARRIAGE = "parents_month_of_marriage";
    public static final String PARENTS_YEAR_OF_MARRIAGE = "parents_year_of_marriage";
    public static final String PARENTS_PLACE_OF_MARRIAGE = "parents_place_of_marriage";
    public static final String ILLEGITIMATE_INDICATOR = "illegitimate_indicator";
    public static final String INFORMANT = "informant";
    public static final String INFORMANT_DID_NOT_SIGN =  "informant_did_not_sign";

    public static final String ADOPTION = "adoption";

    public static final List<String> BIRTH_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
    REGISTRATION_DISTRICT_SUFFIX, ENTRY, BIRTH_YEAR, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, BIRTH_DAY, BIRTH_MONTH,
    BIRTH_ADDRESS, FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
    PARENTS_DAY_OF_MARRIAGE, PARENTS_MONTH_OF_MARRIAGE, PARENTS_YEAR_OF_MARRIAGE, PARENTS_PLACE_OF_MARRIAGE, ILLEGITIMATE_INDICATOR, INFORMANT,
    INFORMANT_DID_NOT_SIGN, CORRECTED_ENTRY, ADOPTION, IMAGE_QUALITY);

    public java.util.Collection<String> getLabels() { return BIRTH_FIELD_NAMES; }

    @Override
    public int getId() { // TODO delete this class!
        return -1;
    }
}
