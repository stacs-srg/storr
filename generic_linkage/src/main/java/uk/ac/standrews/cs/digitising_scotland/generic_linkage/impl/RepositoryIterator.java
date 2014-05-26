package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.util.Iterator;

/**
 *
 * Iterates over a repository (a collection of buckets to give access to each of the buckets
 * Created by al on 11/05/2014.
 */
public class RepositoryIterator implements uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepositoryIterator {

    private final Iterator<File> fileIterator;
    private Repository repository;

    public RepositoryIterator(Repository repository, File repo_directory) {

        this.repository = repository;
        fileIterator = FileIteratorFactory.createFileIterator(repo_directory,false,true);
    }

    public boolean hasNext() {
        return fileIterator.hasNext();
    }

    @Override
    public IBucket next() {
        File f = fileIterator.next();
        String name = f.getName();

        try {
            return repository.getBucket(name);
        } catch (RepositoryException e) {
            ErrorHandling.exceptionError(e, "RepositoryException in iterator");
            return null;
        }


    }

    @Override
    public void remove() {
        ErrorHandling.error( "remove called on stream - unsupported" );
        throw new UnsupportedOperationException("remove called on stream - unsupported" );
    }
}
