package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class BucketBackedInputStream implements ILXPInputStream {

    private final IBucket bucket;
    private File directory;

    public BucketBackedInputStream(final IBucket bucket, final File directory) throws IOException {

        this.bucket = bucket;
        this.directory = directory;

    }

    public Iterator<ILXP> iterator() {
        return new ILXPIterator(directory);
    }

    private class ILXPIterator implements Iterator<ILXP> {

        private Iterator<File> file_iterator;

        public ILXPIterator(File directory) {
            file_iterator = FileIteratorFactory.createFileIterator(directory, true, false);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public ILXP next() {

            try {
                return bucket.get(Integer.parseInt(file_iterator.next().getName()));

            } catch (PersistentObjectException | IOException e) {
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
