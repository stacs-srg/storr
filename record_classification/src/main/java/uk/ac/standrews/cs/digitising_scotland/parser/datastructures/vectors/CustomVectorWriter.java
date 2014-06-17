package uk.ac.standrews.cs.digitising_scotland.parser.datastructures.vectors;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.vectors.io.VectorWriter;

import com.google.common.io.Closeables;

/**
 * Writes out Vectors to a SequenceFile.
 *
 * Closes the writer when done
 */
public class CustomVectorWriter implements VectorWriter {

    /** The writer. */
    private final SequenceFile.Writer writer;

    /** The rec num. */
    private long recNum = 0;

    /**
     * Instantiates a new custom vector writer.
     *
     * @param writer the writer
     */
    public CustomVectorWriter(final SequenceFile.Writer writer) {

        this.writer = writer;
    }

    /* (non-Javadoc)
     * @see org.apache.mahout.utils.vectors.io.VectorWriter#write(java.lang.Iterable, long)
     */
    @Override
    public long write(final Iterable<Vector> iterable, final long maxDocs) throws IOException {

        for (Vector point : iterable) {
            if (recNum >= maxDocs) {
                break;
            }
            if (point != null) {
                writer.append(new LongWritable(recNum++), new VectorWritable(point));
            }

        }
        return recNum;
    }

    /* (non-Javadoc)
     * @see org.apache.mahout.utils.vectors.io.VectorWriter#write(org.apache.mahout.math.Vector)
     */
    @Override
    public void write(final Vector vector) throws IOException {

        NamedVector v = (NamedVector) vector;
        String name = v.getName();
        writer.append(new Text((recNum++) + "/" + name), new VectorWritable(vector));

    }

    /* (non-Javadoc)
     * @see org.apache.mahout.utils.vectors.io.VectorWriter#write(java.lang.Iterable)
     */
    @Override
    public long write(final Iterable<Vector> iterable) throws IOException {

        return write(iterable, Long.MAX_VALUE);
    }

    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {

        Closeables.closeQuietly(writer);
    }

    /**
     * Gets the writer.
     *
     * @return the writer
     */
    public SequenceFile.Writer getWriter() {

        return writer;
    }
}
