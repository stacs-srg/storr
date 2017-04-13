/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.KeyNotFoundException;
import uk.ac.standrews.cs.storr.impl.exceptions.TypeMismatchFoundException;
import uk.ac.standrews.cs.storr.interfaces.IBucketIndex;
import uk.ac.standrews.cs.storr.interfaces.IInputStream;
import uk.ac.standrews.cs.storr.interfaces.ILXP;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static uk.ac.standrews.cs.utilities.FileManipulation.FILE_CHARSET;

/**
 * Created by al on 23/05/2014.
 */
public class BucketIndex implements IBucketIndex {

    private final Path dir;
    private final DirectoryBackedIndexedBucket indexed_bucket;
    private final String label;
    private Map<String, List<Long>> map = null; // a map of values to the record with fields with those values.

    /**
     * Create an index of records in a IndexedFileBasedBucketImpl.
     * pre requisite: directory indicated by @param path has already been created
     * Implementation of backing file
     *
     * @param label          the label in the records being indexed
     * @param dir            the dir holding the index
     * @param indexed_bucket the bucket being indexed
     */
    BucketIndex(final String label, final Path dir, final DirectoryBackedIndexedBucket indexed_bucket) {

        this.label = label; // the label being indexed
        this.dir = dir; // the path to the dir being used to hold the index
        this.indexed_bucket = indexed_bucket;
    }

    private synchronized void onDemandLoadContents() throws BucketException {

        if (map == null) { // not loaded yet
            // read in the indexed ids from the index file

            map = new HashMap<>();

            // load in the persistent Hash Map

            // Need to open all the files in the directory - all named with the value being indexed (NOT KEY)
            // Each contain a list of ids.

            Iterator<File> files = new FileIterator(dir.toFile(), true, false);

            while (files.hasNext()) {

                File f = files.next();
                String key = f.getName(); // the name of the file is also the key
                List<String> values = null; // read in all the lines (ids as Strings)
                try {
                    values = Files.readAllLines(f.toPath(), FILE_CHARSET);
                } catch (IOException e) {
                    throw new BucketException(e.getMessage());
                }
                ArrayList<Long> ids = new ArrayList<>();
                for (String s : values) {
                    ids.add(Long.parseLong(s)); // add in the keys from the file
                }
                map.put(key, ids); // add to HashMap
            }
        }
    }

    @Override
    public Set<String> keySet() throws BucketException {

        onDemandLoadContents();

        return map.keySet();
    }

    @Override
    public List<Long> values(final String value) throws BucketException {

        onDemandLoadContents();

        return map.get(value); // list of integers which are indices into the bucket;
    }

    @Override
    public IInputStream records(final String value) throws BucketException {

        onDemandLoadContents();

        List<Long> entries = map.get(value); // list of integers which are indices into the bucket;
        ArrayList<File> files = new ArrayList<>();

        for (Long i : entries) {
            files.add(indexed_bucket.filePath(i).toFile());
        }

        try {
            return new IndexedBucketInputStream(indexed_bucket, files.iterator());
        } catch (IOException e) {
            throw new BucketException(e.getMessage());
        }
    }

    @Override
    public void add(final ILXP record) throws BucketException {

        onDemandLoadContents();

        String value; // getString the value associated with the label being indexed in this index.
        try {
            value = record.getString(label);
        } catch (KeyNotFoundException e) {
            throw new BucketException("type label not found");
        } catch (TypeMismatchFoundException e) {
            throw new BucketException("type mismatch");
        }

        List<Long> entry = map.get(value); // look up index for the value associated with this do we have any of these values in the index already

        if (entry != null) {
            entry.add(record.getId()); // add it in.
        } else {
            ArrayList<Long> list = new ArrayList<>();    // create a new list
            list.add(record.getId());                     // add it in
            map.put(value, list);                           // add new list to the map.
        }

        // Now add the new value to the list.

        Path path = dir.resolve(value);  // Paths.getString( dir.toFile().getAbsolutePath() + File.separator + value );

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            try (Writer writer = Files.newBufferedWriter(path, FILE_CHARSET)) {

                writer.append(record.getId() + "\n"); // add the new item to the list.
                writer.flush();
            }
        } catch (IOException e) {
            throw new BucketException(e.getMessage());
        }
    }
}
