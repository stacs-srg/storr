package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.util.ErrorHandling;
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

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private final Path repository_path;
    private final File repository_directory;
    private final String name;
    private final IStore store;

    private final Map<String, IBucket> bucket_cache;

    Repository(final Path base_path, String name, IStore store) throws RepositoryException {

        if (!legalName(name)) {
            throw new RepositoryException("Illegal repository name <" + name + ">");
        }

        this.name = name;
        this.store = store;

        repository_path = base_path.resolve(name);
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

    public IBucket makeBucket(final String bucket_name, BucketKind kind) throws RepositoryException {

        IBucket bucket;
        switch (kind) {
            case DIRECTORYBACKED: {
                DirectoryBackedBucket.createBucket(bucket_name, this, kind);
                bucket = new DirectoryBackedBucket(this, bucket_name, kind);
                break;
            }
            case INDIRECT: {
                DirectoryBackedIndirectBucket.createBucket(bucket_name, this, kind);
                try {
                    bucket = new DirectoryBackedIndirectBucket(this, bucket_name);
                    break;
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
            }
            case INDEXED: {
                DirectoryBackedIndexedBucket.createBucket(bucket_name, this, kind);
                bucket = new DirectoryBackedIndexedBucket(this, bucket_name);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    public <T extends ILXP> IBucket<T> makeBucket(final String bucket_name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {

        IBucket bucket;
        switch (kind) {
            case DIRECTORYBACKED: {
                DirectoryBackedBucket.createBucket(bucket_name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedBucket(this, bucket_name, kind, tFactory);
                break;
            }
            case INDIRECT: {
                DirectoryBackedIndirectBucket.createBucket(bucket_name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedIndirectBucket(this, bucket_name, tFactory);
                break;
            }
            case INDEXED: {
                DirectoryBackedIndexedBucket.createBucket(bucket_name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedIndexedBucket(this, bucket_name, tFactory);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    @Override
    public boolean bucketExists(final String name) {

        return legalName(name) && Files.exists(getBucketPath(name));
    }

    private Path getBucketPath(final String name) {

        return repository_path.resolve(name);
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
    public <T extends ILXP> IBucket<T> getBucket(String bucket_name, ILXPFactory<T> tFactory) throws RepositoryException {

        if (bucketExists(bucket_name)) {
            if (bucket_cache.containsKey(bucket_name)) {
                return bucket_cache.get(bucket_name);
            }

            BucketKind kind = DirectoryBackedBucket.getKind(bucket_name, this);
            switch (kind) {
                case DIRECTORYBACKED: {
                    return new DirectoryBackedBucket(this, bucket_name, kind, tFactory);
                }
                case INDIRECT: {
                    return new DirectoryBackedIndirectBucket(this, bucket_name, tFactory);
                }
                case INDEXED: {
                    return new DirectoryBackedIndexedBucket(this, bucket_name, tFactory);
                }
                default: {
                    throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                }
            }
        }
        return null;
    }

    @Override
    public IBucket getBucket(String bucket_name) throws RepositoryException {

        if (bucketExists(bucket_name)) {

            if (bucket_cache.containsKey(bucket_name)) {
                return bucket_cache.get(bucket_name);

            } else {
                BucketKind kind = DirectoryBackedBucket.getKind(bucket_name, this);
                switch (kind) {
                    case DIRECTORYBACKED: {
                        return new DirectoryBackedBucket(this, bucket_name, kind);
                    }
                    case INDIRECT: {
                        try {
                            return new DirectoryBackedIndirectBucket(this, bucket_name);
                        } catch (IOException e) {
                            throw new RepositoryException(e);
                        }
                    }
                    case INDEXED: {
                        return new DirectoryBackedIndexedBucket(this, bucket_name);
                    }
                    default: {
                        throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public BucketNamesIterator getBucketNameIterator() {

        return new BucketNamesIterator(this, repository_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory) {
        return new BucketIterator(this, repository_directory, tFactory);
    }

    @Override
    public Path getRepoPath() {
        return repository_path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public IStore getStore() {

        return store;
    }

    private static class BucketNamesIterator implements Iterator<String> {

        private final Iterator<File> file_iterator;
        private final Repository repository;

        BucketNamesIterator(final Repository repository, final File repo_directory) {

            this.repository = repository;
            file_iterator = new FileIterator(repo_directory, false, true);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public String next() {
            return file_iterator.next().getName();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove called on stream - unsupported");
        }

    }

    static boolean legalName(String name) { // TODO May want to strengthen these conditions
        return name != null && !name.equals("");
    }

    private static class BucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Iterator<File> file_iterator;
        private final Repository repository;
        private ILXPFactory<T> tFactory;

        BucketIterator(final Repository repository, final File repo_directory, ILXPFactory<T> tFactory) {

            this.repository = repository;
            file_iterator = new FileIterator(repo_directory, false, true);
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
