/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.types;

import uk.ac.standrews.cs.storr.impl.DynamicLXP;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.LXPReference;
import uk.ac.standrews.cs.storr.impl.exceptions.*;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.utilities.FileManipulation;
import uk.ac.standrews.cs.utilities.JSONReader;

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

    private LXP typerep;

    public LXPReferenceType(String json_encoded_type_descriptor_file_name, IRepository repo, IBucket bucket) {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(json_encoded_type_descriptor_file_name), FileManipulation.FILE_CHARSET)) {

            typerep = new DynamicLXP(new JSONReader(reader), bucket);

        } catch (PersistentObjectException | IOException | IllegalKeyException e) {
            throw new ReferenceException("Error creating LXPReference", e);
        }
    }

    public LXPReferenceType(DynamicLXP typerep) {
        this.typerep = typerep;
    }

    @Override
    public LXP getRep() {
        return typerep;
    }

    public boolean valueConsistentWithType(Object value) {

        if( value == null ) {
            return true; // permit all null values
        }
        if (!(value instanceof IStoreReference)) {
            return false;
        }

        try {
            IStore store = typerep.getRepository().getStore();
            LXPReference reference = (LXPReference) value;   // This line was changed too!

            // if we just require an lxp don't do more structural checking.

            return equals(store.getTypeFactory().getTypeWithName("lxp")) || Types.checkStructuralConsistency(reference.getReferend(), this);

        } catch (ReferenceException | BucketException e) {
            return false;
        }
    }

    @Override
    public Collection<String> getLabels() {
        return typerep.getMetaData().getFieldNamesToSlotNumbers().keySet();
    }

    @Override
    public IType getFieldType(String label) throws KeyNotFoundException, TypeMismatchFoundException {

        if (typerep.getMetaData().containsLabel(label)) {
            String value = (String) typerep.get(label);
            return Types.stringToType(value, typerep.getRepository().getStore());

        } else return LXPBaseType.UNKNOWN;
    }

    public long getId() {
        return typerep.getId();
    }
}
