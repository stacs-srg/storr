package uk.ac.standrews.cs.digitising_scotland.jstore.types;

import uk.ac.standrews.cs.digitising_scotland.jstore.impl.Store;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.BucketException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.LXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IType;
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
 * Created by al on 2/11/2014.
 * A class representing reference types that may be encoded above LXP storage layer (optional)
 */
public class LXPReferenceType implements IReferenceType {

    private ILXP typerep;

    public LXPReferenceType(String json_encoded_type_descriptor_file_name) {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(json_encoded_type_descriptor_file_name), FileManipulation.FILE_CHARSET)) {

            this.typerep = new LXP(new JSONReader(reader));

        } catch (PersistentObjectException e) {
            ErrorHandling.exceptionError(e, "persistent object exception reading types file: " + json_encoded_type_descriptor_file_name);
        } catch (IOException e) {
            ErrorHandling.exceptionError(e, "IO exception reading types file: " + json_encoded_type_descriptor_file_name);
        } catch (IllegalKeyException e) {
            ErrorHandling.exceptionError(e, "Illegal key encountered reading types file: " + json_encoded_type_descriptor_file_name);
        }
    }

    public LXPReferenceType(LXP typerep) {
        this.typerep = typerep;
    }

    @Override
    public ILXP getRep() {
        return typerep;
    }

    public boolean valueConsistentWithType(Object value) {
        if (!(value instanceof Long)) {
            return false;
        }

        Long id = (Long) value;  // must be a reference to a record of appropriate type
        ILXP record = null;

        try {
            IBucket bucket = Store.getInstance().getObjectCache().getBucket(id);
            if (bucket == null) { // didn't find the bucket
                ErrorHandling.error("Did not find referenced bucket whilst checking type consistency");
                return false;
            }
            record = bucket.get(id);
            if (record == null) { // we haven't found that record in the store
                ErrorHandling.error("Did not find referenced record whilst checking type consistency");
                return false;
            }
        } catch (BucketException e) {
            ErrorHandling.error("Bucket exception whilst checking type consistency");
            return false;
        }
        return Types.check_structural_consistency(record, this);
    }

    @Override
    public Collection<String> getLabels() {
        return typerep.getLabels();
    }

    @Override
    public IType getFieldType(String label) throws KeyNotFoundException, TypeMismatchFoundException {
        if (typerep.containsKey(label)) {
            // TODO This needs repaired - check usages!
            String value = typerep.getString(label);
            return Types.stringToType(value);
        } else return LXPBaseType.UNKNOWN;
    }

    public long getId() {
        return typerep.getId();
    }

}
