package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.storr.util.FileManipulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private final IStore store;
    private final String repository_name;
    private final Path repository_path;
    private final File repository_directory;

    private final Map<String, IBucket> bucket_cache;

    Repository(IStore store, String repository_name, final Path base_path) throws RepositoryException {

        if (!legalName(repository_name)) {
            throw new RepositoryException("Illegal repository name <" + repository_name + ">");
        }

        this.store = store;
        this.repository_name = repository_name;
        repository_path = base_path.resolve(repository_name);
        repository_directory = repository_path.toFile();

        bucket_cache = new HashMap<>();

        if (!repository_directory.exists()) {  // only if the repo doesn't exist - try and make the directory

            if (!repository_directory.mkdir()) {
                throw new RepositoryException("Directory " + repository_directory.getAbsolutePath() + " does not exist and cannot be created");
            }

        } else { // it does exist - check that it is a directory
            if (!repository_directory.isDirectory()) {
                throw new RepositoryException(repository_directory.getAbsolutePath() + " exists but is not a directory");
            }
        }
    }

    @Override
    public IBucket makeBucket(final String bucket_name, BucketKind kind) throws RepositoryException {

        IBucket bucket = BucketKind.createBucket(this, bucket_name, kind);
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    @Override
    public <T extends ILXP> IBucket<T> makeBucket(final String bucket_name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {

        IBucket bucket = BucketKind.createBucket(this, bucket_name, tFactory, kind);
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    @Override
    public boolean bucketExists(final String name) {

        return legalName(name) && Files.exists(getBucketPath(name));
    }

    @Override
    public void deleteBucket(final String name) throws RepositoryException {

        if (!bucketExists(name)) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }

        try {
            FileManipulation.deleteDirectory(getBucketPath(name));
            bucket_cache.remove(name);

        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name);
        }
    }

    @Override
    public IBucket getBucket(final String bucket_name) throws RepositoryException {

        if (bucketExists(bucket_name)) {

            final IBucket bucket = bucket_cache.get(bucket_name);
            return bucket != null ? bucket : BucketKind.getBucket(this, bucket_name);
        }
        throw new RepositoryException("bucket does not exist: " + bucket_name);
    }

    @Override
    public <T extends ILXP> IBucket<T> getBucket(final String bucket_name, final ILXPFactory<T> tFactory) throws RepositoryException {

        if (bucketExists(bucket_name)) {

            final IBucket bucket = bucket_cache.get(bucket_name);
            return bucket != null ? bucket : BucketKind.getBucket(this, bucket_name, tFactory);
        }
        throw new RepositoryException("bucket does not exist: " + bucket_name);
    }

    @Override
    public Iterator<String> getBucketNameIterator() {

        return new BucketNamesIterator(repository_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory) {
        return new BucketIterator(this, repository_directory, tFactory);
    }

    @Override
    public Path getRepositoryPath() {
        return repository_path;
    }

    @Override
    public String getName() {
        return repository_name;
    }

    @Override
    public IStore getStore() {
        return store;
    }

    private Path getBucketPath(final String name) {

        return repository_path.resolve(name);
    }

    static boolean legalName(String name) { // TODO May want to strengthen these conditions
        return name != null && !name.equals("");
    }

    private static class BucketNamesIterator implements Iterator<String> {

        private final Iterator<File> file_iterator;

        BucketNamesIterator(final File repo_directory) {
            file_iterator = new FileIterator(repo_directory, false, true);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public String next() {
            return file_iterator.next().getName();
        }
    }

    private static class BucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Repository repository;
        private final Iterator<File> file_iterator;
        private final ILXPFactory<T> tFactory;

        BucketIterator(final Repository repository, final File repository_directory, ILXPFactory<T> tFactory) {

            this.repository = repository;
            file_iterator = new FileIterator(repository_directory, false, true);
            this.tFactory = tFactory;
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public IBucket<T> next() {

            try {
                return repository.getBucket(file_iterator.next().getName(), tFactory);

            } catch (RepositoryException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }
    }
}
