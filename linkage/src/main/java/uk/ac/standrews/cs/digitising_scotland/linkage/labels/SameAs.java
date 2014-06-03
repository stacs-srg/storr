package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;

/**
 * Created by al on 19/05/2014.
 */
public class SameAs extends Labels {

    public static final String TYPE = "SAME_AS";

    public static final String  record_id1 = "record_id1";
    public static final String  record_id2 = "record_id2";

    public static final Iterable<String> FIELD_NAMES = Arrays.asList(record_id1,record_id2);

    @Override
    public Iterable<String> get_field_names() {
        return FIELD_NAMES;
    }

    @Override
    public String get_type() {
        return TYPE;
    }
}
