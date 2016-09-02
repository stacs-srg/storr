package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class IndexedBucketInputStream implements IInputStream {

    private final IBucket bucket;
    private Iterator<File> file_iterator;

    public IndexedBucketInputStream(final IBucket bucket, final Iterator<File> file_iterator) throws IOException {

        this.bucket = bucket;
        this.file_iterator = file_iterator;
    }


    public Iterator<ILXP> iterator() {
        return new ILXPIterator();
    }

    private class ILXPIterator implements Iterator<ILXP> {

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public ILXP next() {

            try {
                return bucket.getObjectById(Long.parseLong(file_iterator.next().getName()));

            } catch (BucketException e) {
                ErrorHandling.exceptionError(e, "Exception in iterator");
                return null;
            }
        }

        @Override
        public void remove() {
            ErrorHandling.error("remove called on stream - unsupported");
            throw new UnsupportedOperationException("remove called on stream - unsupported");
        }
    }
}
