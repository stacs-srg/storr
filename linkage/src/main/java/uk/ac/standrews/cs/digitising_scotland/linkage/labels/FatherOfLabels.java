package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by al on 19/05/2014.
 */
public class FatherOfLabels implements Iterable<String> {

    public static final String TYPE = "Father_of";

    public static final String  birth_record_id = "birth_record_id";
    public static final String  father_id = "father";
    public static final String  child_id = "child";

    public static final Iterable<String> FIELD_NAMES = Arrays.asList(birth_record_id,father_id,child_id);

    @Override
    public Iterator<String> iterator() {
        return FIELD_NAMES.iterator();
    }


}
