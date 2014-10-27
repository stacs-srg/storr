package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Abstract file processor class that should be used when walking a file tree.
 * Extending classes should implement a process method that processes the input file as required.
 * @author jkc25
 *
 */
public abstract class FileProcessor {

    abstract void process(Path file) throws NumberFormatException, IOException;

}
