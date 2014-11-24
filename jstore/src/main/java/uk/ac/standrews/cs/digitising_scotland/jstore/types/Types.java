package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory.TypeFactory;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

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
    public static <T extends ILXP> boolean check_label_consistency(final T record, int type_label_id) {

        if (record.containsKey(Types.LABEL)) { // if there is a label it must be correct
            try {
                return Types.checkLabelsConsistentWith(Integer.parseInt(record.get(Types.LABEL)), type_label_id);
            } catch (KeyNotFoundException e) {
                return false; // label not there
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
    public static <T extends ILXP> boolean check_structural_consistency(final T record, int type_label_id) throws IOException {


        IReferenceType bucket_type = TypeFactory.getInstance().typeWithId(type_label_id);

        return check_structural_consistency(record, bucket_type);
    }

    /**
     * Checks that the content of a record is consistent with a supplied label (generally from a bucket)
     *
     * @param record   whose _structure is to be checked
     * @param ref_type teh type being checked against
     * @param <T>      the type of the record being checked
     * @return true if the structure is consistent
     * @throws IOException
     */
    public static <T extends ILXP> boolean check_structural_consistency(final T record, IReferenceType ref_type) {

        Set<String> record_keys = record.getKeys();

        // TODO the keys and types in ref_type must exist in record_keys and the values of appropriate types

        Collection<String> required_labels = ref_type.getLabels();

        for (String label : required_labels) {
            if (!record_keys.contains(label)) {
                // required label not present
                return false;
            }
            // required label is present now check the types of the keys in the record
            String value = null;
            try {
                value = record.get(label);
                if (!Types.checkfieldContent(value, ref_type.getFieldType(label))) {
                    return false;
                }
            } catch (KeyNotFoundException e) {
                ErrorHandling.exceptionError(e, "Label missing (illegal code path): " + label);// this cannot happpen - already tested
                return false; // for safety
            }


        }
        return true; // all matched to here we are finished!

    }

    //******* private methods *******

    /**
     * @param value
     * @param fieldIType
     * @return
     */
    private static boolean checkfieldContent(String value, IType fieldIType) {
        if (fieldIType.isBaseType()) {
            switch (fieldIType.getBaseType()) {
                case STRING: {
                    return true;   // everything is string encoded in JSON.
                }
                case FLOAT: {
                    try {
                        Float f = Float.valueOf(value);
                        // we got have a float - all OK.
                        return true;
                    } catch (NumberFormatException e) {
                        // it wasn't a float value;
                        return false;
                    }
                }
                case INT: {
                    try {
                        Integer i = Integer.valueOf(value);
                        // we got have an int - all OK.
                        return true;
                    } catch (NumberFormatException e) {
                        // it wasn't an int value;
                        return false;
                    }
                }
                case UNKNOWN: {
                    ErrorHandling.error("Encountered UNKNOWN type whilst checking field contents");
                    return false;
                }
                default: {
                    ErrorHandling.error("Unhandled field type whilst checking field contents");
                    return false;
                }
            }
        } else { // it is a reference type
            Integer id = Integer.valueOf(value);  // must be a reference to a record of appropriate type
            ILXP record = null;
            try {
                record = Store.getInstance().get(id);
                if (record == null) { // we haven't found that record in the store
                    return false;
                }
            } catch (IOException e) {
                ErrorHandling.exceptionError(e, "Recovering record type");
                return false;
            } catch (PersistentObjectException e) {
                ErrorHandling.exceptionError(e, "Recovering record type");
                return false;
            }

            return check_structural_consistency(record, fieldIType.getReferenceType());
        }
    }

    /**
     * Checks the TYPE LABEL is consistent with a supplied label (generally from a bucket)
     *
     * @param supplied_label_id to be checked
     * @param type_label_id     the label against which the checking is to be performed
     * @return true if the labels are consistent
     */
    public static boolean checkLabelsConsistentWith(int supplied_label_id, int type_label_id) {
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
        }
    }

    public static IType stringToType(String value) {

        if (LXPBaseType.STRING.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.STRING;
        }
        if (LXPBaseType.INT.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.INT;
        }
        if (LXPBaseType.FLOAT.name().toLowerCase().equals(value.toLowerCase())) {
            return LXPBaseType.FLOAT;
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
                    LXP_SCALAR scalar_type = f.getAnnotation(LXP_SCALAR.class);
                    try {
                        f.setAccessible(true);
                        String label_name = (String) f.get(null); // label name is the value of the labelled Java field!
                        System.out.println("   Adding: " + label_name + "," + scalar_type.type().getBaseType().name());
                        type_rep.put(label_name, scalar_type.type().getBaseType().name());
                    } catch (IllegalAccessException e) {
                        ErrorHandling.exceptionError(e, "Illegal access for label: " + f.getName());
                    }
                }

                if (f.isAnnotationPresent(LXP_REF.class)) {
                    LXP_REF ref_type = f.getAnnotation(LXP_REF.class);
                    String ref_type_name = ref_type.type(); // this is the name of the type that the reference refers to
                    try {
                        f.setAccessible(true);
                        String label_name = (String) f.get(null); // label name is the value of the labelled Java field!
                        System.out.println("   Adding: " + label_name + "," + ref_type_name);
                        type_rep.put(label_name, ref_type_name);
                    } catch (IllegalAccessException e) {
                        ErrorHandling.exceptionError(e, "Illegal access for label: " + f.getName());
                    }
                }

            }
        }
        return type_rep;
    }
}
