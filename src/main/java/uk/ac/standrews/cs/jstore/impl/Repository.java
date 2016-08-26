package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.interfaces.*;
import uk.ac.standrews.cs.jstore.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static uk.ac.standrews.cs.jstore.impl.DirectoryBackedBucket.createBucket;
import static uk.ac.standrews.cs.jstore.interfaces.BucketKind.DIRECTORYBACKED;

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private final String repo_path;
    private final File repo_directory;
    private final String name;

    private final Map<String,IBucket> bucket_cache;

    public Repository(final Path base_path, String name) throws RepositoryException {

        if( ! legal_name( name ) ) {
            throw new RepositoryException( "Illegal repository name <" + name + ">" );
        }
        this.name = name;
        this.repo_path = base_path + File.separator + name;
        repo_directory = new File(repo_path);
        bucket_cache = new HashMap<String,IBucket>();

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
        IBucket bucket = null;
        switch (kind) {
            case DIRECTORYBACKED: {
                bucket = DirectoryBackedBucket.createBucket(name, this, kind);
                break;
            }
            case INDIRECT: {
                 bucket =  createBucket(name, this, kind);
                break;
            }
            case INDEXED: {
                bucket = createBucket(name, this, kind);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put( name, bucket );
        return bucket;
    }

    public <T extends ILXP> IBucket<T> makeBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {
        IBucket bucket = null;
        switch (kind) {
            case DIRECTORYBACKED: {
                createBucket(name, this, kind);
                bucket =  new DirectoryBackedBucket(name, this, tFactory, kind);
                break;
            }
            case INDIRECT: {
                createBucket(name, this, kind);
                bucket =  new DirectoryBackedIndirectBucket(name, this, tFactory);
                break;
            }
            case INDEXED: {
                createBucket(name, this, kind);
                bucket =  new DirectoryBackedIndexedBucket(name, this, tFactory);
                break;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
        bucket_cache.put( name, bucket );
        return bucket;
    }

    @Override
    public boolean bucketExists(final String name) {

        if( legal_name(name)) {
            return Files.exists(getBucketPath(name));
        } else {
            return false;
        }
    }

    private Path getBucketPath(final String name) {

        return Paths.get(repo_path).resolve(name);
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
            if( bucket_cache.containsKey(name)) {
                return bucket_cache.get(name);
            } else {
                BucketKind kind = DirectoryBackedBucket.getKind(name, this);
                switch (kind) {
                    case DIRECTORYBACKED: {
                        IBucket bucket = new DirectoryBackedBucket(name, this, tFactory, kind);
                        return bucket;
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
        }
        return null;
    }

    @Override
    public IBucket getBucket(String name) throws RepositoryException {
        if (bucketExists(name)) {
            if( bucket_cache.containsKey(name)) {
                return bucket_cache.get(name);
            } else {
                IBucket bucket = null;
                BucketKind kind = DirectoryBackedBucket.getKind(name, this);
                try {
                    switch (kind) {
                        case DIRECTORYBACKED: {
                            bucket = new DirectoryBackedBucket(name, this, DIRECTORYBACKED);
                            break;
                        }
                        case INDIRECT: {
                            bucket = new DirectoryBackedIndirectBucket(name, this);
                            break;
                        }
                        case INDEXED: {
                            bucket = new DirectoryBackedIndexedBucket(name, this);
                            break;
                        }
                        default: {
                            throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                        }
                    }
                } catch (IOException e) {
                    throw new RepositoryException("IO Exception accessing bucket " + name);
                }

                bucket_cache.put( name, bucket );
                return bucket;
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
    public String getRepo_path() {
        return repo_path;
    }

    @Override
    public String getName() {
        return name;
    }

    private static class BucketNamesIterator implements Iterator<String> {

        private final Iterator<File> file_iterator;
        private final Repository repository;

        public BucketNamesIterator(final Repository repository, final File repo_directory) {

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
        return name != null && ! name.equals( "" );
    }

    private static class BucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Iterator<File> file_iterator;
        private final Repository repository;
        private ILXPFactory<T> tFactory;

        public BucketIterator(final Repository repository, final File repo_directory, ILXPFactory<T> tFactory) {

            this.repository = repository;
            file_iterator = FileIteratorFactory.createFileIterator(repo_directory, false, true);
            this.tFactory = tFactory;
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public IBucket<T> next() {

            String name = file_iterator.next().getName();

            try {
                return repository.getBucket(name, tFactory);

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
