package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IIndexedBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private final String repo_path;
    private final File repo_directory;

    public Repository(final String repo_path) throws RepositoryException {

        this.repo_path = repo_path;
        repo_directory = new File(repo_path);

        if (!repo_directory.exists()) {  // only if the repo doesn't exist - try and make the directory

            if (!repo_directory.mkdir()) {
                throw new RepositoryException("Directory " + repo_directory.getAbsolutePath() + " does not exist and cannot be created");
            }

        } else { // it does exist - check that it is a directory
            if (!repo_directory.isDirectory()) {
                throw new RepositoryException(repo_directory.getAbsolutePath() + " exists but is not a directory");
            }
        }
    }

    @Override
    public IBucket makeBucket(final String name) throws RepositoryException {

        createBucket(name);
        return getBucket(name);
    }

    @Override
    public IIndexedBucket makeIndexedBucket(final String name) throws RepositoryException {

        createBucket(name);
        return getIndexedBucket(name);
    }

    private void createBucket(final String name) throws RepositoryException {

        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            FileManipulation.createDirectoryIfDoesNotExist(getBucketPath(name));

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private Path getBucketPath(final String name) {

        return Paths.get(repo_path).resolve(name);
    }

    @Override
    public boolean bucketExists(final String name) {

        return Files.exists(getBucketPath(name));
    }

    @Override
    public void deleteBucket(final String name) throws RepositoryException {

        if (!bucketExists(name)) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }

        try {
            FileManipulation.deleteDirectory(getBucketPath(name));

        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name);
        }
    }

    @Override
    public IBucket getBucket(final String name) throws RepositoryException {

        try {
            return new Bucket(name, repo_path);

        } catch (IOException e) {
            throw new RepositoryException("Cannot get bucket called " + name);
        }
    }

    public IIndexedBucket getIndexedBucket(final String name) throws RepositoryException {

        try {
            return new IndexedBucket(name, repo_path);

        } catch (IOException e) {
            throw new RepositoryException("Cannot get indexed bucket called " + name);
        }
    }

    @Override
    public BucketIterator getIterator() {
        return new BucketIterator(this, repo_directory);
    }

    private static class BucketIterator implements Iterator<IBucket> {

        private final Iterator<File> file_iterator;
        private final Repository repository;

        public BucketIterator(final Repository repository, final File repo_directory) {

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

}
