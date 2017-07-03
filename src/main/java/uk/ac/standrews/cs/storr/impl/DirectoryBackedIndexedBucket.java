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
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by al on 03/10/2014.
 */
public class DirectoryBackedIndexedBucket<T extends ILXP> extends DirectoryBackedBucket<T> implements IIndexedBucket<T> {

    private static final String INDEX = "INDEX";
    private static final String INDEX_DIR_NAME = "INDICES";
    private Map<String, IBucketIndex> indexes = new HashMap<>();

    protected DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name,  BucketKind kind, boolean create_bucket) throws RepositoryException {

        super(repository, bucket_name, kind, create_bucket);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    protected DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name, BucketKind kind, ILXPFactory tFactory, boolean create_bucket) throws RepositoryException {

        super(repository, bucket_name, kind, tFactory, create_bucket);
        try {
            initIndexes();
        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }


    /**
     * Creates a handle on a bucket.
     *
     * @param repository  the repository in which the bucket is created.
     * @param bucket_name the name of the bucket (also used as directory name).
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name, boolean create_bucket) throws RepositoryException {

        this(repository, bucket_name, BucketKind.INDEXED, create_bucket);

    }

    DirectoryBackedIndexedBucket(final IRepository repository, final String bucket_name, ILXPFactory tFactory, boolean create_bucket) throws RepositoryException {

        this(repository, bucket_name, BucketKind.INDEXED, tFactory, create_bucket);
    }

    @Override
    public void addIndex(final String label) throws IOException {

        Path path = dirPath().resolve(INDEX_DIR_NAME).resolve(INDEX + label);

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
    public void makePersistent(final T record) throws BucketException {

        for (Map.Entry<String, IBucketIndex> entry : indexes.entrySet()) {

            String key = entry.getKey();
            IBucketIndex index = entry.getValue();

            if (record.containsKey(key)) { // we are indexing this key

                try {
                    index.add(record); // and add this record to the index for that key
                } catch (IOException e) {
                    throw new BucketException("I/O exception adding index");
                }
            }

        }

        super.makePersistent(record);
    }

    public IInputStream getInputStream() throws BucketException {
        // We already know that the type is compatible - checked in constructor.
        try {
            return new BucketBackedInputStream<>(this);

        } catch (IOException e) {
            throw new BucketException("I/O exception getting stream");
        }
    }

    public IOutputStream getOutputStream() {
        // We already know that the type is compatible - checked in constructor.
        return new BucketBackedOutputStream<>(this);
    }

    @Override
    public BucketKind getKind() {
        return BucketKind.INDEXED;
    }

    private void initIndexes() throws IOException {

        // Ensure that the index directory exists
        File index = dirPath().resolve(INDEX_DIR_NAME).toFile();
        if (!index.isDirectory() && !index.mkdir()) {
            throw new IOException("Index Directory: " + dirPath() + " does not exist and cannot create");
        }

        Iterator<File> iterator = new FileIterator(index, true, true);
        while (iterator.hasNext()) {
            File next = iterator.next();
            String fullname = next.getName();  // THIS IS THE "INDEX" with the actual KEY appended - so strip the INDEX off
            String keyname = fullname.substring( 5 );
            indexes.put(keyname, new BucketIndex(keyname, next.toPath(), this));
        }
    }
}
