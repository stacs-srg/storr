package uk.ac.standrews.cs.storr.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileIterator implements Iterator<File> {

    private final Iterator<File> file_iterator;

    /**
     * @param directory          the directory to iterate over
     * @param files_wanted       true if the files in the directory should be returned
     * @param directories_wanted true if the directories in the directory should be returned
     */
    public FileIterator(final File directory, final boolean files_wanted, final boolean directories_wanted) {

        File[] files = directory.listFiles();
        if (files == null) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is not a directory.");
        }

        List<File> results = new ArrayList<>();

        for (File f : files) {
            if (f.isDirectory() && directories_wanted || f.isFile() && files_wanted) {
                results.add(f);
            }
        }

        file_iterator = results.iterator();
    }

    @Override
    public boolean hasNext() {
        return file_iterator.hasNext();
    }

    @Override
    public File next() {
        return file_iterator.next();
    }
}
