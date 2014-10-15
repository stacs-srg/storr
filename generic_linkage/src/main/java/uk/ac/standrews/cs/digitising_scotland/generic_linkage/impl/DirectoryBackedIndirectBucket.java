package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.util.ErrorHandling;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndirectBucket<T extends ILXP> extends DirectoryBackedBucket<T> {

    public DirectoryBackedIndirectBucket(String name, String base_path) throws IOException, RepositoryException {
        super(name, base_path);
    }

    public DirectoryBackedIndirectBucket(String name, String base_path, ILXPFactory<T> tFactory) throws IOException, RepositoryException {
        super(name, base_path, tFactory);
    }

    public static <T extends ILXP> IBucket<T> createBucket(String name, Repository repository, ILXPFactory<T> tFactory) throws RepositoryException {

        try {
            DirectoryBackedBucket.createBucket(name, repository, tFactory);
            return new DirectoryBackedIndirectBucket(name, repository.getRepo_path(), tFactory);
        } catch (IOException e) {

            ErrorHandling.error("I/O Exception creating bucket");
            return null;
        }
    }

    @Override
    public void put(final T record) throws IOException, JSONException {

        Path path = Paths.get(filePath(record.getId()));

        if (Files.exists(path)) {
            throw new IOException("File already exists - LXP records in buckets may not be overwritten");
        }

        // create a file containing nothing whose name is the id of the record

        Path p = Files.createFile(Paths.get(filePath(record.getId())), null);

        // Sym link could be created by finding the original at this point and putting it in - but may not work on all platforms?

    }

    @Override
    public T get(final int id) throws PersistentObjectException, IOException {

        if (Files.exists(Paths.get(filePath(id)), NOFOLLOW_LINKS)) {

            return Store.getInstance().get(id); // go find the record where ever it is ( TODO optimise later - could build a real index to buckets each time a record is added to bucket)

        } else {
            throw new PersistentObjectException("Record does not exist in indexed bucket");
        }
    }


    @Override
    public IInputStream<T> getInputStream() throws IOException {
        return new BucketBackedInputStream(this,directory);
    }


    @Override
    public IOutputStream<T> getOutputStream() {
        return new BucketBackedOutputStream(this);
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDIRECT;
    }


}
