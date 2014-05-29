package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import org.json.JSONException;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IIndex;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IIndexedBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by al on 23/05/2014.
 * <p/>
 * Provides an index over the Records stored in the Bucket
 */
public class IndexedBucket extends Bucket implements IIndexedBucket {

    public HashMap<String, IIndex> indexes = new HashMap<String, IIndex>();
    private static String INDEX = "INDEX";
    private static String index_dir_name = "INDICES";

    /**
     * Creates a handle on a bucket.
     * Assumes that bucket has been created already using a factory - i.e. the directory already exists.
     *
     * @param name      - the name of the bucket (also used as directory name).
     * @param base_path - the repository path in which the bucket is created.
     */
    public IndexedBucket(String name, String base_path) throws Exception {
        super(name, base_path);
        init_indexes();
    }

    private void init_indexes() throws Exception {
        String dirname = dirPath();
        // Ensure that the index directory exists
        File index = new File(dirname + File.separator + index_dir_name);
        if (!index.isDirectory() && !index.mkdir()) {
            throw new Exception("Index Directory: " + dirname + " does not exist and cannot create");
        }
        Iterator<File> fi = FileIteratorFactory.createFileIterator(index, true, false);
        while (fi.hasNext()) {
            File next = fi.next();
            indexes.put(next.getName(), new Index(next.getName(), next.toPath(), this));
        }

    }


    @Override
    public void add_index(String label) throws IOException {
        Path path = Paths.get(this.filePath(index_dir_name + "/" + INDEX + label));

        if (Files.exists(path)) {
            throw new IOException("index exists");
        } else {
            Files.createDirectory(path); // create a directory to store the index
            indexes.put(label, new Index(label, path, this)); // keep the in memory index list up to date
        }
    }

    @Override
    public IIndex get_index(String label) {
        return indexes.get(label);

    }

    @Override
    public void save(ILXP record) throws IOException, JSONException {
        Set<String> keys = indexes.keySet(); // all the keys currently being indexed
        for (String key : keys) {
            if (record.containsKey(key)) { // we are indexing this key
                IIndex index = indexes.get(key); // so get the index
                index.add(record); // and add this record to the index for that key

            }
        }


    }
}
