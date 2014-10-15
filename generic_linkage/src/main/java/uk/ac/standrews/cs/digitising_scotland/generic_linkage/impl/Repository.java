package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
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


    public IBucket makeBucket(final String name, BucketKind kind) throws RepositoryException {
        switch( kind ) {
            case DIRECTORYBACKED: {
                IBucket bucket = DirectoryBackedBucket.createBucket(name, this);
                bucket.setKind( kind );
                return bucket;
            }
            case INDIRECT: {
                IBucket bucket = DirectoryBackedIndirectBucket.createBucket(name, this);
                bucket.setKind( kind );
                return bucket;
            }
            case INDEXED: {
                IBucket bucket = DirectoryBackedIndexedBucket.createBucket(name, this);
                bucket.setKind( kind );
                return bucket;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
    }

    public <T extends ILXP> IBucket<T> makeBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {
        switch( kind ) {
            case DIRECTORYBACKED: {
                    IBucket<T> bucket = DirectoryBackedBucket.createBucket(name, this, tFactory);
                    bucket.setKind( kind );
                    return bucket;
            }
            case INDIRECT: {
                IBucket<T> bucket = DirectoryBackedIndirectBucket.createBucket(name, this, tFactory);
                bucket.setKind( kind );
                return bucket;
            }
            case INDEXED: {
                IBucket<T> bucket = DirectoryBackedIndexedBucket.createBucket(name, this, tFactory);
                bucket.setKind( kind );
                return bucket;
            }
            default: {
                throw new RepositoryException("Bucketkind: " + kind + " not recognized");
            }
        }
    }


    private void addBucketType(Path path, ILXPFactory<?> tFactory ) throws IOException {
        // Extract content type of the bucket.

        Class<? extends ILXPFactory> c = tFactory.getClass();

        Method[] methods = c.getMethods();
        for (Method m : methods) {
            if( m.getName().equals("create") ) {   // we are interested in the create method - don't know how to extract it easier due to parameterisation
                Class type = m.getReturnType();
                String typeString = type.toString();

                Path metapath = path.resolve("META");
                FileManipulation.createDirectoryIfDoesNotExist(metapath);   // TODO move the labels in here too.

                Path typepath = metapath.resolve("TYPE");
                FileManipulation.createFileIfDoesNotExist((typepath));

                // Now write the type into this file

                try (Writer writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

                    writer.write(typeString);
                }
            }
        }
    }

    @Override
    public boolean bucketExists(final String name) {

        return Files.exists(getBucketPath(name));
    }

    private Path getBucketPath(final String name) { // TODO this is duplicate code.

        return Paths.get(repo_path).resolve(name);
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
    public <T extends ILXP> IBucket<T> getBucket(String name, ILXPFactory<T> tFactory) throws RepositoryException {
        if (bucketExists(name)) {
            BucketKind kind = DirectoryBackedBucket.getKind(name, repo_path);
            try {
                switch( kind ) {
                    case DIRECTORYBACKED: {
                        IBucket bucket = new DirectoryBackedBucket(name, repo_path,tFactory);
                        return bucket;
                    }
                    case INDIRECT: {
                        return new DirectoryBackedIndirectBucket(name,repo_path,tFactory);
                    }
                    case INDEXED: {
                        return new DirectoryBackedIndexedBucket(name, repo_path,tFactory);
                    }
                    default: {
                        throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                    }
                }
            } catch (IOException e) {
                throw new RepositoryException("IO Exception accessing bucket " + name);
            }
        }
        return null;
    }

    @Override
    public IBucket getBucket(String name) throws RepositoryException {
        if (bucketExists(name)) {
            BucketKind kind = DirectoryBackedBucket.getKind(name, repo_path);
            try {
                switch( kind ) {
                    case DIRECTORYBACKED: {
                        IBucket bucket = new DirectoryBackedBucket(name, repo_path);
                        return bucket;
                    }
                    case INDIRECT: {
                        return new DirectoryBackedIndirectBucket(name,repo_path);
                    }
                    case INDEXED: {
                        return new DirectoryBackedIndexedBucket(name, repo_path);
                    }
                    default: {
                        throw new RepositoryException("Bucketkind: " + kind + " not recognized");
                    }
                }
            } catch (IOException e) {
                throw new RepositoryException("IO Exception accessing bucket " + name);
            }
        }
        return null;
    }

    @Override
    public BucketNamesIterator getBucketNameIterator() {

        return new BucketNamesIterator(this, repo_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator( ILXPFactory<T> tFactory ) {
        return new BucketIterator(this, repo_directory,tFactory);
    }

    @Override
    public String getRepo_path() {
        return repo_path;
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
                return repository.getBucket(name,tFactory);

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
