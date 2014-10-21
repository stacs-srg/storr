package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import org.json.JSONException;

import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ITypeLabel;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by al on 12/09/2014.
 */
public class TypeLabel implements ITypeLabel {

    public static final String LABEL = "$LABEL$";

    private ILXP lxp;

    public TypeLabel( ILXP labels ) {
        this.lxp = labels;
    }

    public static TypeLabel createNewTypeLabel(String json_encoded_type_descriptor_file_name, IBucket types_bucket) {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(json_encoded_type_descriptor_file_name), FileManipulation.FILE_CHARSET)) {

           LXP labels = new LXP( new JSONReader(reader) );
           TypeLabel tl =  new TypeLabel( labels );
           types_bucket.put( labels );
           return tl;

        } catch (PersistentObjectException e) {
            ErrorHandling.exceptionError( e, "persistent object exception reading types file: " + json_encoded_type_descriptor_file_name);
        } catch (IOException e) {
            ErrorHandling.exceptionError( e, "IO exception reading types file: " + json_encoded_type_descriptor_file_name);
        } catch (JSONException e) {
            ErrorHandling.exceptionError( e, "JSON exception adding types to types bucket");
        }
        return null;
    }


    @Override
    public Collection<String> getLabels() {
        return lxp.getKeys();
    }

    @Override
    public Type getFieldType(String label) throws KeyNotFoundException {
        if( lxp.containsKey(label) ) {
            String value = lxp.get(label);
            return Type.SringToType(value);
        }
        else return Type.UNKNOWN;
    }

    public int getId() {
        return lxp.getId();
    }

    public static boolean checkConsistentWith( int supplied_label_id, int required_type_labelID ) {
        // do id check first
        if( required_type_labelID == supplied_label_id ) {
            return true;
        }
        // if that doesn't work do structural check
        try {
            ILXP record = Store.getInstance().get( supplied_label_id );
            ITypeLabel stored_label = new TypeLabel( record  );
            ILXP required_type_label_lxp = Store.getInstance().get( supplied_label_id );
            ITypeLabel required_label = new TypeLabel( required_type_label_lxp  );
            Collection<String> required = required_label.getLabels();
            for( String label : required ) {
                if( required_label.getFieldType(label) != stored_label.getFieldType(label)) {
                    return false;
                }
            }
            return true;
        } catch (PersistentObjectException e) {
            ErrorHandling.error( "PersistentObjectException - returning false" );
            return false;
        } catch (IOException e) {
            ErrorHandling.error( "PersistentObjectException - returning false" );
            return false;
        } catch (KeyNotFoundException e) {
            ErrorHandling.error( "KeyNotFoundException - returning false" );
            return false;
        }
    }
}
