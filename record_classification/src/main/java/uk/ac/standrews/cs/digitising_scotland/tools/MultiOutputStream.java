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

    private OutputStream[] outputStreams;

    public OutputStream[] getOutputStreams() {

        return outputStreams.clone();
    }

    public void setOutputStreams(final OutputStream[] outputStreams) {

        this.outputStreams = outputStreams.clone();
    }

    public MultiOutputStream(final OutputStream... outputStreams) {

        this.outputStreams = outputStreams;
    }

    @Override
    public void write(final int b) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    @Override
    public void write(final byte[] b) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {

        for (OutputStream out : outputStreams) {
            out.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {

        for (OutputStream out : outputStreams) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {

        for (OutputStream out : outputStreams) {
            out.close();
        }
    }
}
