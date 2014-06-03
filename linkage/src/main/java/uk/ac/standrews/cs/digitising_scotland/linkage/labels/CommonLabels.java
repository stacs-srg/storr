package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 19/05/2014.
 */
public abstract class CommonLabels implements Iterable<String> {

    public static final String ID = "id";
    public static final String SURNAME = "surname";
    public static final String FORENAME = "forename";
    public static final String SEX = "sex";
    public static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    public static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    public static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";
    public static final String ENTRY = "ENTRY";

    public static final String FATHERS_FORENAME = "fathers_forename";
    public static final String FATHERS_SURNAME = "fathers_surname";
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    public static final String MOTHERS_FORENAME = "mothers_forename";
    public static final String MOTHERS_SURNAME = "mothers_surname";

    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    public static final String CHANGED_SURNAME = "changed_surname";
    public static final String CHANGED_FORENAME = "changed_forename";
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    public static final String CORRECTED_ENTRY = "corrected_entry";
    public static final String IMAGE_QUALITY = "image_quality";

    public static final Iterable<String> COMMON_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME,
            FATHERS_FORENAME, FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME, CHANGED_MOTHERS_MAIDEN_SURNAME,
            CORRECTED_ENTRY, IMAGE_QUALITY);

}
