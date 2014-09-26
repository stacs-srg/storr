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
    public <T extends ILXP> IBucketTypedOLD makeBucket(final String name, ILXPFactory<T> tFactory ) throws RepositoryException {

        createBucket(name, BucketKind.DIRECTORYBACKED,tFactory);
        return getBucket(name,tFactory);
    }

    @Override
    public <T extends ILXP> IBucketTypedOLD<T> makeIndirectBucket(final String name, ILXPFactory<T> tFactory ) throws RepositoryException {

        createBucket(name, BucketKind.INDIRECT,tFactory);
        return getIndirectBucket(name);
    }

    @Override
    public <T extends ILXP> IIndexedBucketTypedOLD makeIndexedBucket(final String name, ILXPFactory<T> tFactory ) throws RepositoryException {

        createBucket(name, BucketKind.INDIRECT,tFactory);
        return getIndexedBucket(name);
    }

    private void createNewBucket(final String name, BucketKind kind, ITypeLabel type ) throws RepositoryException {  //  TODO AL - NOW NEED TO ADD THE POLY! DELETE THE OLD VERSIONS FROM HERE.
        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            Path impl_kind_path = createBucketImplKind(path, kind);
            addBucketTypeLabel(path, type);

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private void addBucketTypeLabel(Path path, ITypeLabel type) throws IOException {
        Path metapath = path.resolve("META");
        FileManipulation.createDirectoryIfDoesNotExist(metapath);

        Path typepath = metapath.resolve("TYPELABEL");
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (Writer writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(type.getId()); // Write the id of the typelabel LXP into this field.
        }
    }

    private <T extends ILXP>  void createBucket(final String name, BucketKind kind, ILXPFactory<T> tFactory ) throws RepositoryException {

        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        try {
            Path path = getBucketPath(name);
            FileManipulation.createDirectoryIfDoesNotExist(path);
            Path impl_kind_path = createBucketImplKind(path, kind);
            addBucketType( path, tFactory );

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
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

     Path createBucketImplKind(Path path, BucketKind kind) throws IOException {
        Path labelpath = path.resolve(kind.name());
        FileManipulation.createDirectoryIfDoesNotExist(labelpath); // create a directory labelled with the kind in the new bucket dir
        return labelpath;
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
    public <T extends ILXP> IBucketTypedOLD getBucket(final String name, ILXPFactory<T> tFactory) throws RepositoryException {

        try {
            IBucketTypedOLD<? extends ILXP> bucket = new DirectoryBackedBucketTypedOLD(name, repo_path, tFactory);     // TODO TYPES - need to check the type with the expected type!!!
            if( bucket.kind().equals(BucketKind.DIRECTORYBACKED)) {
                return bucket;
            } else {
                throw new RepositoryException("Specified bucket is not directory backed " + name);
            }

        } catch (IOException e) {
            throw new RepositoryException("Cannot get bucket called " + name);
        }
    }

    public IBucketTypedOLD getIndirectBucket(final String name) throws RepositoryException {

        try {
            IBucketTypedOLD bucket = new DirectoryBackedIndirectBucketTypedOLD(name, repo_path);
            if( bucket.kind().equals(BucketKind.INDIRECT)) {
                return bucket;
            } else {
                throw new RepositoryException("Specified bucket is not indirect " + name);
            }

        } catch (IOException e) {
            throw new RepositoryException("Cannot get indexed bucket called " + name);
        }
    }


    public IIndexedBucketTypedOLD getIndexedBucket(final String name) throws RepositoryException {

        try {
            IIndexedBucketTypedOLD bucket = new DirectoryBackedIndexedBucketTypedOLD(name, repo_path);
            if( bucket.kind().equals(BucketKind.INDEXED)) {
                return bucket;
            } else {
                throw new RepositoryException("Specified bucket is not indexed " + name);
            }

        } catch (IOException e) {
            throw new RepositoryException("Cannot get indexed bucket called " + name);
        }
    }

    @Override
    public BucketIterator getIterator() {
        return new BucketIterator(this, repo_directory);
    }

    private static class BucketIterator implements Iterator<IBucketTypedOLD> {

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
        public IBucketTypedOLD next() {

            String name = file_iterator.next().getName();

            try {
                return repository.getBucket(name,LXP.getInstance());

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
