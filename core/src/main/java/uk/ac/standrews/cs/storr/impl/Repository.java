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

    private final Path repo_path;
    private final File repo_directory;
    private final String name;
    private final IStore store;

    private final Map<String, IBucket> bucket_cache;

    Repository(final Path base_path, String name, IStore store) throws RepositoryException {

        if (!legal_name(name)) {
            throw new RepositoryException("Illegal repository name <" + name + ">");
        }
        this.name = name;
        this.store = store;
        repo_path = base_path.resolve(name);
        repo_directory = repo_path.toFile();
        bucket_cache = new HashMap<>();

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

    public IBucket makeBucket(final String name, BucketKind kind) throws RepositoryException {

        IBucket bucket;
        switch (kind) {
            case DIRECTORYBACKED: {
                DirectoryBackedBucket.createBucket(name, this, kind);
                bucket = new DirectoryBackedBucket(name, this, kind);
                break;
            }
            case INDIRECT: {
                DirectoryBackedIndirectBucket.createBucket(name, this, kind);
                try {
                    bucket = new DirectoryBackedIndirectBucket(name, this);
                    break;
                } catch (IOException e) {
                    throw new RepositoryException(e);
                }
            }
            case INDEXED: {
                DirectoryBackedIndexedBucket.createBucket(name, this, kind);
                bucket = new DirectoryBackedIndexedBucket(name, this);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put(name, bucket);
        return bucket;
    }

    public <T extends ILXP> IBucket<T> makeBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {

        IBucket bucket = null;
        switch (kind) {
            case DIRECTORYBACKED: {
                DirectoryBackedBucket.createBucket(name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedBucket(name, this, tFactory, kind);
                break;
            }
            case INDIRECT: {
                DirectoryBackedIndirectBucket.createBucket(name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedIndirectBucket(name, this, tFactory);
                break;
            }
            case INDEXED: {
                DirectoryBackedIndexedBucket.createBucket(name, this, kind, tFactory.getTypeLabel());
                bucket = new DirectoryBackedIndexedBucket(name, this, tFactory);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put(name, bucket);
        return bucket;
    }

    @Override
    public boolean bucketExists(final String name) {

        return legal_name(name) && Files.exists(getBucketPath(name));
    }

    private Path getBucketPath(final String name) {

        return repo_path.resolve(name);
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
    public <T extends ILXP> IBucket<T> getBucket(String name, ILXPFactory<T> tFactory) throws RepositoryException {

        if (bucketExists(name)) {
            if (bucket_cache.containsKey(name)) {
                return bucket_cache.get(name);
            }

            BucketKind kind = DirectoryBackedBucket.getKind(name, this);
            switch (kind) {
                case DIRECTORYBACKED: {
                    return new DirectoryBackedBucket(name, this, tFactory, kind);
                }
                case INDIRECT: {
                    return new DirectoryBackedIndirectBucket(name, this, tFactory);
                }
                case INDEXED: {
                    return new DirectoryBackedIndexedBucket(name, this, tFactory);
                }
                default: {
                    throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                }
            }

        }
        return null;
    }

    @Override
    public IBucket getBucket(String name) throws RepositoryException {
        if (bucketExists(name)) {

            if (bucket_cache.containsKey(name)) {
                return bucket_cache.get(name);
            } else {
                BucketKind kind = DirectoryBackedBucket.getKind(name, this);
                switch (kind) {
                    case DIRECTORYBACKED: {
                        return new DirectoryBackedBucket(name, this, kind);
                    }
                    case INDIRECT: {
                        try {
                            return new DirectoryBackedIndirectBucket(name, this);
                        } catch (IOException e) {
                            throw new RepositoryException(e);
                        }
                    }
                    case INDEXED: {
                        return new DirectoryBackedIndexedBucket(name, this);
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

        return new BucketNamesIterator(this, repo_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory) {
        return new BucketIterator(this, repo_directory, tFactory);
    }

    @Override
    public Path getRepoPath() {
        return repo_path;
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
            file_iterator = FileIteratorFactory.createFileIterator(repo_directory, false, true);
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

    private static boolean legal_name(String name) { // TODO May want to strengthen these conditions
        return name != null && !name.equals("");
    }

    private static class BucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Iterator<File> file_iterator;
        private final Repository repository;
        private ILXPFactory<T> tFactory;

        BucketIterator(final Repository repository, final File repo_directory, ILXPFactory<T> tFactory) {

            this.repository = repository;
            file_iterator = FileIteratorFactory.createFileIterator(repo_directory, false, true);
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
