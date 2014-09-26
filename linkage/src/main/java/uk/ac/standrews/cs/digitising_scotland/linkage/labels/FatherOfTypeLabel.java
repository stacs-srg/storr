package uk.ac.standrews.cs.digitising_scotland.linkage.labels;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ITypeLabel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by al on 19/05/2014.
 */
public class FatherOfTypeLabel implements ITypeLabel {

    public static final String TYPE = FatherOfTypeLabel.class.getName();

    public static final String  birth_record_id = "birth_record_id";
    public static final String  father_id = "father";
    public static final String  child_id = "child";

    public static final List<String> FIELD_NAMES = Arrays.asList(birth_record_id,father_id,child_id);

    @Override
    public java.util.Collection<String> getLabels() {
        return FIELD_NAMES;
    }

    @Override
    public Type getFieldType(String label) {
        if( label.equals(birth_record_id)) return Type.REFERENCE;
        if( label.equals(father_id)) return Type.REFERENCE;
        if( label.equals(child_id)) return Type.REFERENCE;

        return Type.UNKNOWN;
    }

    @Override
    public int getId() { // TODO delete this class!
        return -1;
    }
}
