package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepositoryIterator;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.util.Iterator;

/**
 * Iterates over a repository (a collection of buckets to give access to each of the buckets
 * Created by al on 11/05/2014.
 */
public class RepositoryIterator implements IRepositoryIterator {

    private final Iterator<File> file_iterator;
    private final Repository repository;

    public RepositoryIterator(final Repository repository, final File repo_directory) {

        this.repository = repository;
        file_iterator = FileIteratorFactory.createFileIterator(repo_directory, false, true);
    }

    public boolean hasNext() {
        return file_iterator.hasNext();
    }

    @Override
    public IBucket next() {

        String name = file_iterator.next().getName();

        try {
            return repository.getBucket(name);

        } catch (RepositoryException e) {
            ErrorHandling.exceptionError(e, "RepositoryException in iterator");
            return null;
        }
    }

    @Override
    public void remove() {

        throw new UnsupportedOperationException("remove called on stream - unsupported");
    }
}
