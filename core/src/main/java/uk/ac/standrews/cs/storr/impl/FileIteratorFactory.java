package uk.ac.standrews.cs.storr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileIteratorFactory {

    /**
     * @param directory - the directory to iterate over
     * @param files_wanted is true if the files in the directory should be returned
     * @param directories_wanted is true if the directories in the directory should be returned
     * @return an iterator or null if illegal directory is past in.
     */
    public static Iterator<File> createFileIterator(final File directory, final boolean files_wanted, final boolean directories_wanted) {

        List<File> files = new ArrayList<File>();

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory.");
        }
        File[] contents = directory.listFiles();
        if (contents == null) {
            return null;
        } else {
            for (File f : contents) {
                if (f.isDirectory() && directories_wanted || f.isFile() && files_wanted) {
                    files.add(f);
                }
            }
            return files.iterator();
        }
    }
}
