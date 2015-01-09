package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;

/**
 * Created by al on 30/10/14.
 */
public class Types {

    public static final String LABEL = "$LABEL$";

    /**
     * Checks the TYPE LABEL on a record (if there is one) is consistent with a supplied label (generally from a bucket)
     *
     * @param record        whose label is to be checked
     * @param type_label_id the label against which the checking is to be performed
     * @param <T>           the type of the record being checked
     * @return true if the labels are consistent
     */
    public static <T extends ILXP> boolean check_label_consistency(final T record, long type_label_id) {

        if (record.containsKey(Types.LABEL)) { // if there is a label it must be correct
            try {
                return Types.checkLabelsConsistentWith(record.getLong(Types.LABEL), type_label_id);
            } catch (KeyNotFoundException | TypeMismatchFoundException e) {
                return false; // label not there or inappropriate type
            }
        } else {
            return true; // if there is no label that is OK
        }
    }

    /**
     * Checks that the content of a record is consistent with a supplied label (generally from a bucket)
     *
     * @param record        whose _structure is to be checked
     * @param type_label_id the label against which the checking is to be performed
     * @param <T>           the type of the record being checked
     * @return true if the structure is consistent
     * @throws IOException
     */
    public static <T extends ILXP> boolean check_structural_consistency(final T record, long type_label_id) throws IOException {


        IReferenceType bucket_type = TypeFactory.getInstance().typeWithId(type_label_id);

        return check_structural_consistency(record, bucket_type);
    }

    /**
     * Checks that the content of a record is consistent with a supplied label (generally from a bucket)
     *
     * @param record   whose _structure is to be checked
     * @param ref_type the type being checked against
     * @param <T>      the type of the record being checked
     * @return true if the structure is consistent
     * @throws IOException
     */
    public static <T extends ILXP> boolean check_structural_consistency(final T record, IReferenceType ref_type) {

        Set<String> record_keys = record.getLabels();

        Collection<String> required_labels = ref_type.getLabels();

        for (String label : required_labels) {
            if (!record_keys.contains(label)) {
                // required label not present
                return false;
            }
            // required label is present now check the types of the keys in the record
            try {
                Object value = record.get(label);
                if (!ref_type.getFieldType(label).valueConsistentWithType(value)) {
                    return false;
                }
            } catch (KeyNotFoundException e) {
                ErrorHandling.exceptionError(e, "Label missing (illegal code path): " + label);// this cannot happpen - already tested
                return false; // for safety
            } catch (TypeMismatchFoundException e) {
                //TODO look at me in a little while - 10/12/14!!!!!!
                ErrorHandling.exceptionError(e, "Type mistmatch for label: " + label + "error: " + e.getMessage());
                return false; // for safety
            }
        }
        return true; // all matched to here we are finished!

    }


    /**
     * Checks the TYPE LABEL is consistent with a supplied label (generally from a bucket)
     *
     * @param supplied_label_id to be checked
     * @param type_label_id     the label against which the checking is to be performed
     * @return true if the labels are consistent
     */
    public static boolean checkLabelsConsistentWith(long supplied_label_id, long type_label_id) {
        // do id check first
        if (type_label_id == supplied_label_id) {
            return true;
        }
        // if that doesn't work do structural check
        try {
            IReferenceType stored_label = TypeFactory.getInstance().typeWithId(supplied_label_id);
            IReferenceType required_label = TypeFactory.getInstance().typeWithId(supplied_label_id);
            Collection<String> required = required_label.getLabels();
            for (String label : required) {
                if (required_label.getFieldType(label) != stored_label.getFieldType(label)) {
                    return false;
                }
            }
            return true;
        } catch (KeyNotFoundException e) {
            ErrorHandling.error("KeyNotFoundException - returning false");
            return false;
        } catch (TypeMismatchFoundException e) {
            ErrorHandling.error("Type mismatch - returning false");
            return false;
        }
    }

    public static IType stringToType(String value) {

        if (LXPBaseType.STRING.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.STRING;
        }
        if (LXPBaseType.INT.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.INT;
        }
        if (LXPBaseType.DOUBLE.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.DOUBLE;
        }
        if (LXPBaseType.BOOLEAN.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.BOOLEAN;
        }
        if (TypeFactory.getInstance().containsKey(value)) {
            return TypeFactory.getInstance().typeWithname(value);
        }
        ErrorHandling.error("Encountered reference to type not defined: " + value);
        return LXPBaseType.UNKNOWN;
    }

    public static LXP getTypeRep(Class c) {

        LXP type_rep = new LXP();

        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {

            if (Modifier.isStatic(f.getModifiers())) {

                if (f.isAnnotationPresent(LXP_SCALAR.class)) {
                    if (f.isAnnotationPresent(LXP_REF.class)) {
                        ErrorHandling.error("Conflicting labels: " + f.getName()); // Graham wrote this :)
                    }
                    LXP_SCALAR scalar_type = f.getAnnotation(LXP_SCALAR.class);
                    try {
                        f.setAccessible(true);
                        String label_name = (String) f.get(null); // label name is the value of the labelled Java field!
                        type_rep.put(label_name, scalar_type.type().name());
                    } catch (IllegalAccessException e) {
                        ErrorHandling.exceptionError(e, "Illegal access for label: " + f.getName());
                    } catch (IllegalKeyException e) {
                        ErrorHandling.exceptionError(e, "Illegal key in label: " + f.getName());
                    }
                } else if (f.isAnnotationPresent(LXP_REF.class)) {
                    LXP_REF ref_type = f.getAnnotation(LXP_REF.class);
                    String ref_type_name = ref_type.type(); // this is the name of the type that the reference refers to
                    try {
                        f.setAccessible(true);
                        String label_name = (String) f.get(null); // label name is the value of the labelled Java field!
                        type_rep.put(label_name, ref_type_name);
                    } catch (IllegalAccessException e) {
                        ErrorHandling.exceptionError(e, "Illegal access for label: " + f.getName());
                    } catch (IllegalKeyException e) {
                        ErrorHandling.exceptionError(e, "Illegal key in label: " + f.getName());
                    }
                }

            }
        }
        return type_rep;
    }
}
