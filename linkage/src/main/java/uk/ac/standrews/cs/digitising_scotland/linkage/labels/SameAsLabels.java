package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by al on 19/05/2014.
 */
public class SameAsLabels implements Iterable<String> {

    public static final String TYPE = "Same_as";

    public static final String  first = "first";
    public static final String  second = "second";
    public static final String relationship = "relationship";
    public static final String resolver = "resolver";

    public static final Iterable<String> FIELD_NAMES = Arrays.asList(first,second);


    public Iterator<String> iterator() {
        return FIELD_NAMES.iterator();
    }

}
