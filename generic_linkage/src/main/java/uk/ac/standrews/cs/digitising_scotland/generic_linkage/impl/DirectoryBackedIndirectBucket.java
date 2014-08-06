package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.BucketKind;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

/**
 * Created by al on 01/08/2014.
 *
 * This class maintains a directory of files containing indexes of LXP records held in other buckets
 * Each filename is the id of a record held elsewhere
 * The files are currently empty!
 *
 * Consider making into a sym link in future - but wouldn't work in windows.
 *
 */
public class DirectoryBackedIndirectBucket extends DirectoryBackedBucket implements IBucket {


    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name      the name of the bucket (also used as directory name)
     * @param base_path the path of the parent directory
     */
    public DirectoryBackedIndirectBucket(String name, String base_path) throws IOException {
        super(name, base_path);
    }

    @Override
    public ILXP get(final int id) throws PersistentObjectException, IOException {

        if( Files.exists(Paths.get(filePath(id)), NOFOLLOW_LINKS) ) {

            return Store.getInstance().get(id); // go find the record where ever it is (TODO optimise later - could build a real index to buckets each time a record is added to bucket)

        } else {
            throw new PersistentObjectException( "Record does not exist in indexed bucket");
        }
    }

    @Override
    public void put(final ILXP record) throws IOException, JSONException {

        Path path = Paths.get(filePath(record.getId()));

        if (Files.exists(path)) {
            throw new IOException("File already exists - LXP records in buckets may not be overwritten");
        }

        // create a file containing nothing whose name is the id of the record

        Path p = Files.createFile(Paths.get(filePath(record.getId())),null);

        // Sym link could be created by finding the original at this point and putting it in - but may not work on all platforms?

    }

    @Override
    public BucketKind kind() {
        return BucketKind.INDIRECT;
    }

}
