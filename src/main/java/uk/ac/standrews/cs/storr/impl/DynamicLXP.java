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
package uk.ac.standrews.cs.storr.impl;

import org.json.JSONWriter;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.PersistentObjectException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.utilities.JSONReader;

import java.io.StringWriter;
import java.util.Map;

/**
 * This is a Labelled Cross Product (a tuple).
 * This is the basic unit that is stored in Buckets.
 * Higher order language level types may be constructed above this basic building block.
 * LXP provides a thin wrapper over a Map (providing name value lookup) along with identity and the ability to save and recover persistent versions (encoded in JSON).
 */
public class DynamicLXP extends LXP implements Comparable<DynamicLXP> {

    public DynamicLXP() {
        super();
    }

    public DynamicLXP(final long object_id, final IBucket bucket) {
        super(object_id, bucket);
    }

    public DynamicLXP(final long persistent_object_id, final JSONReader reader, final IBucket bucket) throws PersistentObjectException {
        super(persistent_object_id, reader, bucket);
    }

    public DynamicLXP(final JSONReader reader, final IBucket bucket) throws PersistentObjectException {
        super(reader, bucket);
    }

    @Override
    public Metadata getMetaData() {
        return metadata;
    }

    //****** end of put methods ******//

    @Override
    public void check(final String key) throws IllegalKeyException {

        if (key == null || key.equals("")) {
            throw new IllegalKeyException("null key");
        }

        final Map<String, Integer> field_name_to_slot = metadata.getFieldNamesToSlotNumbers();
        final Map<Integer, String> slot_to_fieldname = metadata.getSlotNumbersToFieldNames();

        if (!field_name_to_slot.containsKey(key)) {
            final int next_slot = findFirstFree();
            field_name_to_slot.put(key, next_slot);
            slot_to_fieldname.put(next_slot, key);
        }
    }

    // Java housekeeping

    @Override
    public int compareTo(final DynamicLXP o) {
        return Long.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(final Object o) {

        return (o instanceof DynamicLXP) && (compareTo((DynamicLXP) o)) == 0;
    }

    public String toString() {

        final StringWriter writer = new StringWriter();
        try {
            serializeToJSON(new JSONWriter(writer));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    public int hashCode() {
        return (int) getId();
    }


}
