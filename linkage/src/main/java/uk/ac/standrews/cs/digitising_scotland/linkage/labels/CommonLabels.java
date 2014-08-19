package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILabels;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 19/05/2014.
 */
public abstract class CommonLabels implements ILabels {

    public static final String TYPE_LABEL = "TYPE"; // note that this does not encode type - it is the label for types.

    public static final String ID = "id";
    public static final String SURNAME = "surname";
    public static final String FORENAME = "forename";
    public static final String SEX = "sex";
    public static final String YEAR_OF_REGISTRATION = "YEAR_OF_REGISTRATION";
    public static final String REGISTRATION_DISTRICT_NUMBER = "REGISTRATION_DISTRICT_NUMBER";
    public static final String REGISTRATION_DISTRICT_SUFFIX = "REGISTRATION_DISTRICT_SUFFIX";
    public static final String ENTRY = "ENTRY";


    public static final String CORRECTED_ENTRY = "corrected_entry";
    public static final String IMAGE_QUALITY = "image_quality";

    public static final List<String> COMMON_FIELD_NAMES = Arrays.asList(ID, SURNAME, FORENAME, SEX, YEAR_OF_REGISTRATION, REGISTRATION_DISTRICT_NUMBER,
            REGISTRATION_DISTRICT_SUFFIX, ENTRY, CORRECTED_ENTRY, IMAGE_QUALITY);

    @Override
    /**
     * By default return the type STRING for all known labels and UNKNOWN otherwise
     * May be overridden in sub classes.
     */
    public Type getFieldType(String label) {
        if(getLabels().contains(label)) {
            return Type.STRING;
        } else {
            return Type.UNKNOWN;
        }
    }
}
