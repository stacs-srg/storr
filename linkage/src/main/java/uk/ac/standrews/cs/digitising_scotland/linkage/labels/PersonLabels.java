package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILabels;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 30/05/2014.
 */
public class PersonLabels implements ILabels {

    public static final String TYPE = PersonLabels.class.getName();

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
    public final static String ROLE = "role";
    public final static String OCCUPATION = "occupation";

    public static final List<String> FIELD_NAMES = Arrays.asList(SURNAME,FORENAME,SEX,FATHERS_FORENAME,FATHERS_SURNAME, FATHERS_OCCUPATION, MOTHERS_FORENAME, MOTHERS_SURNAME,
            MOTHERS_MAIDEN_SURNAME, CHANGED_SURNAME, CHANGED_FORENAME, CHANGED_MOTHERS_MAIDEN_SURNAME, ORIGINAL_RECORD_ID,ORIGINAL_RECORD_TYPE, ROLE,OCCUPATION);

    @Override
    public List<String> getLabels() {
        return FIELD_NAMES;
    }

    @Override
    public Type getType(String label) {
        if(getLabels().contains(label)) {
            return Type.STRING;
        } else {
            return Type.UNKNOWN;
        }

    }

}
