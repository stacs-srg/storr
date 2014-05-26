package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;


public class FileIteratorFactory {

    /**
     *
     * @param directory - the directory to iterate over
     * @return an iterator or null if illegal directory is past in.
     */
    public static Iterator<File> createFileIterator(File directory, boolean files_wanted, boolean directories_wanted  ) {
        ArrayList<File> files = new ArrayList<File> ();

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory.");
        }
        File[] contents = directory.listFiles();
        if( contents == null ) {
            return null;
        } else {
            for (File f : contents) {
                if ( f.isDirectory() && directories_wanted ) {
                    files.add(f);
                }
                if( f.isFile() && files_wanted ) {
                    files.add(f);
                }
            }
            return files.iterator();
        }

    }

}
