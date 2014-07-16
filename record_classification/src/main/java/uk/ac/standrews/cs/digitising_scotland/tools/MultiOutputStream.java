package uk.ac.standrews.cs.digitising_scotland.tools;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Supports multiple output streams.
 * Used to redirect stdout and stderr to files.
 * @author jkc25
 *
 */
public class MultiOutputStream extends OutputStream {

    /** The output streams. */
    private OutputStream[] outputStreams;

    /**
     * Gets the output streams.
     *
     * @return the output streams
     */
    public OutputStream[] getOutputStreams() {

        return outputStreams.clone();
    }

    /**
     * Sets the output streams.
     *
     * @param outputStreams the new output streams
     */
    public void setOutputStreams(final OutputStream[] outputStreams) {

        this.outputStreams = outputStreams.clone();
    }

    /**
     * Instantiates a new multi output stream.
     *
     * @param outputStreams the output streams
     */
    public MultiOutputStream(final OutputStream... outputStreams) {

        this.outputStreams = outputStreams;
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b, off, len);
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException {

        for (OutputStream out : outputStreams) {
            out.flush();
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException {

        for (OutputStream out : outputStreams) {
            out.close();
        }
    }
}
