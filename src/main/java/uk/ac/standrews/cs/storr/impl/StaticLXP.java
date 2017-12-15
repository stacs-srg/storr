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
import java.util.HashMap;



/**
 * This is a Labelled Cross Product (a tuple).
 * This is the basic unit that is stored in Buckets.
 * Higher order language level types may be constructed above this basic building block.
 * LXP provides a thin wrapper over a Map (providing name value lookup) along with identity and the ability to save and recover persistent versions (encoded in JSON).
 */
public abstract class StaticLXP extends LXP implements Comparable<StaticLXP> {

    public StaticLXP() {
        super();
    }

    public StaticLXP(long object_id, IBucket bucket ) {
        super( object_id, bucket);
    }

    public StaticLXP(long persistent_object_id, JSONReader reader, IBucket bucket) throws PersistentObjectException {
        super( persistent_object_id, reader, bucket );
    }

    public StaticLXP(JSONReader reader, IBucket bucket ) throws PersistentObjectException {
        super( reader, bucket );
    }

    @Override
    public void check(String key) throws IllegalKeyException {
        if (key == null || key.equals("")) {
            throw new IllegalKeyException("null key");
        }
        HashMap<String, Integer> field_name_to_slot = getMetaData().getFieldNamesToSlotNumbers();

        if( ! field_name_to_slot.containsKey(key) ) {
            throw new IllegalKeyException( key );
        }
    }

    // Java housekeeping

    @Override
    public int compareTo(StaticLXP o) {
        return Long.compare(this.getId(), o.getId());
    }

    @Override
    public boolean equals(Object o) {

        return (o instanceof StaticLXP) && (compareTo((StaticLXP) o)) == 0;
    }

    public String toString() {

        StringWriter writer = new StringWriter();
        try {
            serializeToJSON(new JSONWriter(writer));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    public int hashCode() {
        return (int) getId();
    }

}
