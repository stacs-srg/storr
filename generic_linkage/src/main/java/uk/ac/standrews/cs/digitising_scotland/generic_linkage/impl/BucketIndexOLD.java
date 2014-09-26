package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucketIndexOLD;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXP;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.ILXPInputStreamTypedOld;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static uk.ac.standrews.cs.digitising_scotland.util.FileManipulation.FILE_CHARSET;

/**
 * Created by al on 23/05/2014.
 */
public class BucketIndexOLD implements IBucketIndexOLD {

    private final Path dir;
    private final DirectoryBackedIndexedBucketTypedOLD indexed_bucket;
    private final String label;
    private Map<String, List<Integer>> map = null; // a map of values to the record with fields with those values.

    /**
     * Create an index of records in a IndexedFileBasedBucketImpl.
     * pre requisite: directory indicated by @param path has already been created
     * Implementation of backing file
     *
     * @param label          - the label in the records being indexed
     * @param dir            - the dir holding the index
     * @param indexed_bucket - the bucket being indexed
     */
    public BucketIndexOLD(final String label, final Path dir, final DirectoryBackedIndexedBucketTypedOLD indexed_bucket) {

        this.label = label; // the label being indexed
        this.dir = dir; // the path to the dir being used to hold the index
        this.indexed_bucket = indexed_bucket;
    }

    private void onDemandLoadContents() throws IOException {

        if (map == null) { // not loaded yet
            // read in the indexed ids from the index file

            map = new HashMap<>();

            // load in the persistent Hash Map

            // Need to open all the files in the directory - all named with the value being indexed (NOT KEY)
            // Each contain a list of ids.

            Iterator<File> files = FileIteratorFactory.createFileIterator(dir.toFile(), true, false);

            while (files.hasNext()) {

                File f = files.next();
                String key = f.getName(); // the name of the file is also the key
                List<String> values = Files.readAllLines(f.toPath(), FILE_CHARSET); // read in all the lines (ids as Strings)
                ArrayList<Integer> ids = new ArrayList<>();
                for (String s : values) {
                    ids.add(Integer.parseInt(s)); // add in the keys from the file
                }
                map.put(key, ids); // add to HashMap
            }
        }
    }

    @Override
    public Set<String> keySet() throws IOException {

        onDemandLoadContents();

        return map.keySet();
    }


    @Override
    public List<Integer> values(final String value) throws IOException {

        onDemandLoadContents();

        return map.get(value); // list of integers which are indices into the bucket;
    }

    @Override
    public ILXPInputStreamTypedOld records(final String value) throws IOException {

        onDemandLoadContents();

        List<Integer> entries = map.get(value); // list of integers which are indices into the bucket;
        ArrayList<File> files = new ArrayList<>();

        for (Integer i : entries) {
            files.add(new File(indexed_bucket.filePath(i)));
        }

        return new IndexedBucketInputStreamTypedOld(indexed_bucket, files.iterator());
    }

    @Override
    public void add(final ILXP record) throws IOException {

        onDemandLoadContents();

        String value = record.get(label); // get the value associated with the label being indexed in this index.

        List<Integer> entry = map.get(value); // look up index for the value associated with this do we have any of these values in the index already

        if (entry != null) {
            entry.add(record.getId()); // add it in.
        } else {
            ArrayList<Integer> list = new ArrayList<>();    // create a new list
            list.add(record.getId());                     // add it in
            map.put(value, list);                           // add new list to the map.
        }

        // Now add the new value to the list.

        Path path = dir.resolve(value);  // Paths.get( dir.toFile().getAbsolutePath() + File.separator + value );

        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        try (Writer writer = Files.newBufferedWriter(path, FILE_CHARSET)) {

            writer.append(record.getId() + "\n"); // add the new item to the list.
            writer.flush();
        }
    }
}
