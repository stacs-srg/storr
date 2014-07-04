package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketIndex;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IIndexedBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by al on 23/05/2014.
 * <p/>
 * Provides an index over the Records stored in the Bucket
 */
public class IndexedBucket extends Bucket implements IIndexedBucket {

    private Map<String, IBucketIndex> indexes = new HashMap<>();

    private static final String INDEX = "INDEX";
    private static final String INDEX_DIR_NAME = "INDICES";

    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name      the name of the bucket (also used as directory name).
     * @param base_path the repository path in which the bucket is created.
     */
    public IndexedBucket(final String name, final String base_path) throws IOException {

        super(name, base_path);
        initIndexes();
    }

    private void initIndexes() throws IOException {

        String dirname = dirPath();
        // Ensure that the index directory exists
        File index = new File(dirname + File.separator + INDEX_DIR_NAME);
        if (!index.isDirectory() && !index.mkdir()) {
            throw new IOException("Index Directory: " + dirname + " does not exist and cannot create");
        }

        Iterator<File> iterator = FileIteratorFactory.createFileIterator(index, true, false);
        while (iterator.hasNext()) {
            File next = iterator.next();
            indexes.put(next.getName(), new BucketIndex(next.getName(), next.toPath(), this));
        }
    }

    @Override
    public void addIndex(final String label) throws IOException {

        Path path = Paths.get(this.filePath(INDEX_DIR_NAME + "/" + INDEX + label));

        if (Files.exists(path)) {
            throw new IOException("index exists");
        } else {
            Files.createDirectory(path); // create a directory to store the index
            indexes.put(label, new BucketIndex(label, path, this)); // keep the in memory index list up to date
        }
    }

    @Override
    public IBucketIndex getIndex(final String label) {
        return indexes.get(label);
    }

    @Override
    public void put(final ILXP record) throws IOException, JSONException {

        Set<String> keys = indexes.keySet(); // all the keys currently being indexed
        for (String key : keys) {
            if (record.containsKey(key)) { // we are indexing this key
                IBucketIndex index = indexes.get(key); // so get the index
                index.add(record); // and add this record to the index for that key
            }
        }
        super.put(record);
    }

    /**
     * @return a file iterator which filters out the index files
     */
    private Iterator<File> createFileIterator() {

        return FileIteratorFactory.createFileIterator(directory, true, false);
    }
}
