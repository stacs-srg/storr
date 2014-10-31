package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ITypeLabel;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Created by al on 30/10/14.
 */
public class Types {

    /**
     * Checks the TYPE LABEL on a record are consistent with a supplied label (generally from a bucket)
     *
     * @param record        whose label is to be checked
     * @param type_label_id the label against which the checking is to be performed
     * @param <T>           the type of the record being checked
     * @return true if the labels are consistent
     */
    public static <T extends ILXP> boolean check_label_consistency(final T record, int type_label_id) {

        try {
            return Types.checkLabelsConsistentWith(Integer.parseInt(record.get(TypeLabel.LABEL)), type_label_id);
        } catch (KeyNotFoundException e) {
            return false; // label not there
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

        Set<String> record_keys = record.getKeys();

        try {
            ITypeLabel bucket_label = new TypeLabel(Store.getInstance().get(type_label_id));

            // TODO the keys and types in bucket_label must exist in record_keys and the values of appropriate types

            Collection<String> required_labels = bucket_label.getLabels();

            for (String label : required_labels) {
                if (!record_keys.contains(label)) {
                    // required label not present
                    return false;
                }
                // required label is present now check the types of the keys in the record
                String value = null;
                try {
                    value = record.get(label);
                    if (!Types.checkfieldContent(value, bucket_label.getFieldType(label))) {
                        return false;
                    }
                } catch (KeyNotFoundException e) {
                    ErrorHandling.exceptionError(e, "Label missing (illegal code path): " + label);// this cannot happpen - already tested
                    return false; // for safety
                }


            }
            return true; // all matched to here we are finished!

        } catch (PersistentObjectException e) {
            ErrorHandling.exceptionError(e);
            return false;
        }
    }

    //******* private methods *******

    /**
     * @param value
     * @param fieldType
     * @return
     */
    private static boolean checkfieldContent(String value, Type fieldType) {
        switch (fieldType) {
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
            case REFERENCE: // todo refine this in a bit.
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
            ILXP record = Store.getInstance().get(supplied_label_id);
            ITypeLabel stored_label = new TypeLabel(record);
            ILXP required_type_label_lxp = Store.getInstance().get(supplied_label_id);
            ITypeLabel required_label = new TypeLabel(required_type_label_lxp);
            Collection<String> required = required_label.getLabels();
            for (String label : required) {
                if (required_label.getFieldType(label) != stored_label.getFieldType(label)) {
                    return false;
                }
            }
            return true;
        } catch (PersistentObjectException e) {
            ErrorHandling.error("PersistentObjectException - returning false");
            return false;
        } catch (IOException e) {
            ErrorHandling.error("PersistentObjectException - returning false");
            return false;
        } catch (KeyNotFoundException e) {
            ErrorHandling.error("KeyNotFoundException - returning false");
            return false;
        }
    }
}
