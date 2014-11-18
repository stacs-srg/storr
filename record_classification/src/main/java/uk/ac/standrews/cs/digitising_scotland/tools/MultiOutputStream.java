/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
