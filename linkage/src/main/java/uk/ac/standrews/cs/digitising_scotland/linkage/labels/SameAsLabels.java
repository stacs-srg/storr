package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILabels;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 19/05/2014.
 */
public class SameAsLabels implements ILabels {

    public static final String TYPE = SameAsLabels.class.getName();

    public static final String  first = "first";
    public static final String  second = "second";
    public static final String resolver = "resolver";
    public static final String relationship = "relationship";
    public static final String confidence = "confidence";

    public static final List<String> FIELD_NAMES = Arrays.asList(first,second,resolver,relationship,confidence);

    public List<String> getLabels() {
        return FIELD_NAMES;
    }

    @Override
    public Type getFieldType(String label) {
        if( label.equals(first)) return Type.REFERENCE;
        if( label.equals(second)) return Type.REFERENCE;
        if( label.equals(resolver)) return Type.STRING;
        if( label.equals(confidence)) return Type.FLOAT;
        return Type.UNKNOWN;
    }

}
