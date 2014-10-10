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

    @Override
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

    @Override
    public IBucketLXP makeLXPBucket(final String name, BucketKind kind  ) throws RepositoryException {
       switch( kind ) {
           case DIRECTORYBACKED: {
               IBucketLXP bucket = DirectoryBackedBucketLXP.createBucket(name, this);
               bucket.setKind( kind  );
               return bucket;
           }
           case INDIRECT: {
               IBucketLXP bucket = DirectoryBackedIndirectBucketLXP.createBucket(name, this);
               bucket.setKind( kind  );
               return bucket;
           }
           case INDEXED: {
               IBucketLXP bucket = DirectoryBackedIndexedBucketLXP.createBucket(name, this);
               bucket.setKind( kind  );
               return bucket;
           }
           default: {
               throw new RepositoryException("Bucketkind: " + kind + " not recognized");
           }
       }
    }

    private <T extends ILXP> void createBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory ) throws RepositoryException {

        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            switch( kind ) {
                case DIRECTORYBACKED: {
                    DirectoryBackedBucket.createBucket(name, this, tFactory);
                    return;
                }
                case INDIRECT: {
                    DirectoryBackedIndirectBucket.createBucket(name, this, tFactory);
                }
                case INDEXED: {
                    DirectoryBackedIndexedBucket.createBucket(name, this, tFactory);
                }
                default:
            }
        } catch (IOException e) {
            throw new RepositoryException( e.getMessage() );
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
    public IBucketLXP getLXPBucket(String name) throws RepositoryException {
        try {
            return new DirectoryBackedBucketLXP(name, repo_path);
        } catch (IOException e) {
            throw new RepositoryException("IO Exception accessing bucket " + name);
        }
    }

    @Override
    public <T extends ILXP> IBucket<T> getBucket(String name, ILXPFactory<T> tFactory) throws RepositoryException {
        try {
            return new DirectoryBackedBucket(name, repo_path, tFactory);
        } catch (IOException e) {
            throw new RepositoryException("IO Exception accessing bucket " + name);
        }
    }

    @Override
    public BucketIterator getLXPIterator() {

        return new BucketIterator(this, repo_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator( ILXPFactory<T> tFactory ) {
        return new TypedBucketIterator(this, repo_directory,tFactory);
    }

    @Override
    public String getRepo_path() {
        return repo_path;
    }

    private static class TypedBucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Iterator<File> file_iterator;
        private final Repository repository;
        private ILXPFactory<T> tFactory;

        public TypedBucketIterator(final Repository repository, final File repo_directory,ILXPFactory<T> tFactory) {

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

    private static class BucketIterator implements Iterator<IBucketLXP> {

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
        public IBucketLXP next() {

            String name = file_iterator.next().getName();

            try {
                return repository.getLXPBucket(name);

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
