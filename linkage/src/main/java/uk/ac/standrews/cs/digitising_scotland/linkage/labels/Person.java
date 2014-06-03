package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 30/05/2014.
 */
public class Person extends Labels {

    public final String TYPE = "Person";

    public static final String SURNAME = "surname";
    public static final String FORENAME = "forename";
    public static final String SEX = "sex";

    public static final String FATHERS_FORENAME = "fathers_forename";
    public static final String FATHERS_SURNAME = "fathers_surname";
    public static final String FATHERS_OCCUPATION = "fathers_occupation";
    public static final String MOTHERS_FORENAME = "mothers_forename";
    public static final String MOTHERS_SURNAME = "mothers_surname";

    public static final String MOTHERS_MAIDEN_SURNAME = "mothers_maiden_surname";
    public static final String CHANGED_SURNAME = "changed_surname";
    public static final String CHANGED_FORENAME = "changed_forename";
    public static final String CHANGED_MOTHERS_MAIDEN_SURNAME = "changed_mothers_maiden_surname";

    public final static String ORIGINAL_RECORD_ID = "original_record_id";
    public final static String ORIGINAL_RECORD_TYPE = "original_record_type";
    public final static String RELATION_TO_CERT = "relationship_to_cert";
    public final static String OCCUPATION = "occupation";

    public static final Iterable<String> FIELD_NAMES = Arrays.asList(SURNAME,FORENAME,SEX,FATHERS_FORENAME,FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME,
            MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, CHANGED_MOTHERS_MAIDEN_SURNAME, ORIGINAL_RECORD_ID,ORIGINAL_RECORD_TYPE,RELATION_TO_CERT,OCCUPATION);

    @Override
    public Iterable<String> get_field_names() {
        return FIELD_NAMES;
    }

    @Override
    public String get_type() {
        return TYPE;
    }

}
