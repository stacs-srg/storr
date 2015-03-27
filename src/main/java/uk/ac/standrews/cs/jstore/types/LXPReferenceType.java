package uk.ac.standrews.cs.jstore.types;

import uk.ac.standrews.cs.jstore.impl.LXP;
import uk.ac.standrews.cs.jstore.impl.StoreFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.jstore.interfaces.IType;
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

        } catch (PersistentObjectException | IOException | IllegalKeyException e) {
            ErrorHandling.exceptionError(e, "Error creating LXPReference" );
            // at this point we are in big trouble!
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
            IBucket bucket = null;
            try {
                bucket = StoreFactory.getStore().getObjectCache().getBucket(id);
            } catch (StoreException e) {
                ErrorHandling.error( "Cannot get store" );
                return false;
            }
            if (bucket == null) { // didn't find the bucket
                ErrorHandling.error("Did not find referenced bucket whilst checking type consistency");
                return false;
            }
            record = bucket.getObjectById(id);
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
