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

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BucketBackedInputStream<T extends PersistentObject> implements IInputStream<T> {

    private final IBucket<T> bucket;

    BucketBackedInputStream(final IBucket<T> bucket) throws IOException {

        this.bucket = bucket;
    }

    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private Iterator<Long> oid_iterator = bucket.getOids().iterator();  //TODO Maybe slow????

            @Override
            public boolean hasNext() {
                return oid_iterator.hasNext();
            }

            @Override
            public T next() {

                try {
                    return bucket.getObjectById(oid_iterator.next());

                } catch (BucketException e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }
        };
    }
}
