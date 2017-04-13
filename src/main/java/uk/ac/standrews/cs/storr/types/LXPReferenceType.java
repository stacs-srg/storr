package uk.ac.standrews.cs.storr.types;

import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.StoreReference;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.impl.temp.JSONReader;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.utilities.FileManipulation;
import uk.ac.standrews.cs.utilities.archive.ErrorHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * Created by al on 2/11/2014.
 * A class representing reference types that may be encoded above OID storage layer (optional)
 */
public class LXPReferenceType implements IReferenceType {

    private ILXP typerep;

    public LXPReferenceType(String json_encoded_type_descriptor_file_name, IRepository repo, IBucket bucket) {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(json_encoded_type_descriptor_file_name), FileManipulation.FILE_CHARSET)) {

            typerep = new LXP(new JSONReader(reader), repo, bucket);

        } catch (PersistentObjectException | IOException | IllegalKeyException e) {
            throw new ReferenceException("Error creating LXPReference: " + e.getMessage());
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

        if (!(value instanceof String)) {
            return false;
        }

        try {
            IStore store = typerep.getRepository().getStore();
            IStoreReference reference = new StoreReference(store, (String) value);

            // if we just require an lxp don't do more structural checking.
            return equals(store.getTypeFactory().getTypeWithName("lxp")) || Types.checkStructuralConsistency(getRecord(reference), this);

        } catch (ReferenceException | BucketException e) {
            return false;
        }
    }

    @Override
    public Collection<String> getLabels() {
        return typerep.getLabels();
    }

    @Override
    public IType getFieldType(String label) throws KeyNotFoundException, TypeMismatchFoundException {

        if (typerep.containsKey(label)) {
            String value = typerep.getString(label);
            return Types.stringToType(value, typerep.getRepository().getStore());

        } else return LXPBaseType.UNKNOWN;
    }

    public long getId() {
        return typerep.getId();
    }

    private ILXP getRecord(IStoreReference reference) throws BucketException {
        ILXP record;

        try {
            record = reference.getReferend();
        } catch (BucketException e) {
            ErrorHandling.error("Did not find referenced record whilst checking type consistency");
            throw new BucketException(e);
        }

        return record;
    }
}
