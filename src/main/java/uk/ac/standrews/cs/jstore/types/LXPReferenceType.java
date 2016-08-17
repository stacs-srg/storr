package uk.ac.standrews.cs.jstore.types;

import uk.ac.standrews.cs.jstore.impl.LXP;
import uk.ac.standrews.cs.jstore.impl.StoreReference;
import uk.ac.standrews.cs.jstore.impl.TypeFactory;
import uk.ac.standrews.cs.jstore.impl.exceptions.*;
import uk.ac.standrews.cs.jstore.interfaces.ILXP;
import uk.ac.standrews.cs.jstore.interfaces.IReferenceType;
import uk.ac.standrews.cs.jstore.interfaces.IStoreReference;
import uk.ac.standrews.cs.jstore.interfaces.IType;
import uk.ac.standrews.cs.jstore.util.ErrorHandling;
import uk.ac.standrews.cs.jstore.util.FileManipulation;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.rpc.stream.JSONReader;

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
        if (!(value instanceof String)) {
            return false;
        }

        try {
            IStoreReference reference = createStoreReference(value);
            ILXP record = getRecord(reference);

            if( this.equals( TypeFactory.getInstance().typeWithname("lxp") ) ) { // if we just require an lxp don't do more structural checking.
                return true;
            } else {
                return Types.check_structural_consistency(record, this);
            }
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
            return Types.stringToType(value);
        } else return LXPBaseType.UNKNOWN;
    }

    public long getId() {
        return typerep.getId();
    }

    private IStoreReference createStoreReference(Object value) throws ReferenceException {
        IStoreReference reference;

        try {
            reference = new StoreReference((String) value);
        } catch (ReferenceException e) {
            // We could not determine that this is a store reference
            ErrorHandling.exceptionError(e, "Could not create store reference - not necessarily a system error" );
            throw new ReferenceException(e.getMessage());
        }

        return reference;
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
