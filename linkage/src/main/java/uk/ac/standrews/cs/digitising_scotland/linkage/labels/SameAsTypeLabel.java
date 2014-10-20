package uk.ac.standrews.cs.digitising_scotland.linkage.labels;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Type;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ITypeLabel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 19/05/2014.
 */
public class SameAsTypeLabel implements ITypeLabel {

    public static final String TYPE = SameAsTypeLabel.class.getName();

    public static final String  first = "first";
    public static final String  second = "second";
    public static final String resolver = "resolver";
    public static final String relationship = "relationship";
    public static final String confidence = "confidence";

    public static final List<String> FIELD_NAMES = Arrays.asList(first,second,resolver,relationship,confidence);

    public java.util.Collection<String> getLabels() {
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

    @Override
    public int getId() { // TODO delete this class!
        return -1;
    }

}
