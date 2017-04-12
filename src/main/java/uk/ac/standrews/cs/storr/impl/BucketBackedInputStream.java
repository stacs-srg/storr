package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.ILXP;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BucketBackedInputStream<T extends ILXP> implements IInputStream<T> {

    private final IBucket<T> bucket;

    BucketBackedInputStream(final IBucket<T> bucket) throws IOException {

        this.bucket = bucket;
    }

    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private Iterator<Long> oid_iterator = bucket.getOids().iterator();

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
