package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl.merger;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Merges a number of possibly heterogeneous streams into a single stream
 * Merging is performed end to end - the first record of the second stream follows the last record of the first and so on.
 * Created by al on 02/05/2014.
 */
public class TailToTailMergedStream implements ILXPInputStream {

    private ILXPInputStream[] streams;

    public TailToTailMergedStream(final ILXPInputStream[] streams) {

        this.streams = Arrays.copyOf(streams, streams.length);
    }

    public Iterator<ILXP> iterator() {
        return new StreamMergeIterator();
    }

    private class StreamMergeIterator implements Iterator<ILXP> {

        private Iterator<ILXP> current_iterator;
        private int index = 0; // index into the streams array

        public StreamMergeIterator() {

            current_iterator = streams[index].iterator();
        }

        @Override
        public boolean hasNext() {

            return current_iterator.hasNext() ? true : nextStream();
        }

        @Override
        public ILXP next() {

            try {
                return current_iterator.next();

            } catch (NoSuchElementException e) { // if there are no more in current stream try and set up the next one.

                if (nextStream()) {
                    return current_iterator.next();
                } else {
                    throw e;
                }
            }
        }

        @Override
        public void remove() {

            throw new UnsupportedOperationException("remove called on stream - unsupported");
        }

        /**
         * Move the streams on.
         * i.e. make the next one in the streams array current and return true
         * increments the stream pointer index.
         * keeps doing this if null stream or empty streams are found
         *
         * @return true if there is another stream and it has records in it.
         */
        private boolean nextStream() {

            do {
                index++;
                if (index < streams.length) {
                    current_iterator = streams[index].iterator();
                } else {
                    return false; // we have run out of streams
                }
            }
            while (!current_iterator.hasNext());

            return true;
        }
    }
}
