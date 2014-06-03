package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 19/05/2014.
 */
public class MotherOf extends Labels {

    public static final String TYPE = "MOTHER_OF";

    public static final String  birth_record_id = "birth_record_id";
    public static final String  mother_id = "mother";
    public static final String  child_id = "child";

    public static final Iterable<String> FIELD_NAMES = Arrays.asList(birth_record_id,mother_id,child_id);

    @Override
    public Iterable<String> get_field_names() {
        return FIELD_NAMES;
    }

    @Override
    public String get_type() {
        return TYPE;
    }
}
