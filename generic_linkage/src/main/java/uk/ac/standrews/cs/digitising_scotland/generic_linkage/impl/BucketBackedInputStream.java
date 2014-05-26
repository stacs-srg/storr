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
    Iterator<File> file_iterator;

    public BucketBackedInputStream(IBucket bucket, Iterator<File> file_iterator) throws IOException {

        this.bucket = bucket;
        this.file_iterator = file_iterator;

    }


    public BucketBackedInputStream(IBucket bucket, File directory) throws IOException {

        this( bucket, FileIteratorFactory.createFileIterator(directory, true, false) );
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
                int id = Integer.valueOf(file_iterator.next().getName());

                LXP lxp = new LXP(id);
                lxp.initialiseFromBucket(bucket, id);

                return lxp;

            } catch (PersistentObjectException | IOException e) {
                System.out.println("exception in iterator: " + e.getMessage());
//                ErrorHandling.exceptionError(e, "Exception in iterator");
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
