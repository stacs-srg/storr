package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.types.Type;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketLXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ITypeLabel;
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

    private ILXP labels;

    public TypeLabel( ILXP labels ) {
        this.labels = labels;
    }

    public static TypeLabel createNewTypeLabel(String json_encoded_type_descriptor_file_name, IBucketLXP types_bucket) {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(json_encoded_type_descriptor_file_name), FileManipulation.FILE_CHARSET)) {

           ILXP labels = new LXP( new JSONReader(reader) );
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
        return labels.getKeys();
    }

    @Override
    public Type getFieldType(String label) {
        return null;
    }

    public int getId() {
        return labels.getId();
    }
}
