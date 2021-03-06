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

import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IIndexedBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by al on 12/7/2017
 */
public class DirectoryBackedMapBucket<T extends LXP> extends DirectoryBackedIndexedBucket<T> implements IIndexedBucket<T> {

    private static final String MAP_TYPE_LABEL_FILE_NAME = "MAPTYPELABEL";
    private long map_type_label_id = -1;          // -1 == not set


    /**
     * Creates a handle on a $$$bucket$$$bucket$$$.
     * Assumes that $$$bucket$$$bucket$$$ has been created already using a factory - i.e. the directory already exists.
     *
     * @param repository the repository in which the $$$bucket$$$bucket$$$ is created.
     * @param map_name   the name of the $$$bucket$$$bucket$$$ (also used as directory name).
     * @throws RepositoryException if a RepositoryException is thrown in implementation
     */
    DirectoryBackedMapBucket(final IRepository repository, final String map_name, BucketKind kind, Class<T> bucketType, boolean create_map) throws RepositoryException {

        super(repository, map_name, kind, create_map);
        long type_label = -1;
        if (create_map) {
            try {
                addIndex(Tuple.KEY);
                LXPMetadata metadata = bucketType.newInstance().getMetaData();
                type_label = metadata.getType().getId();
                setMapType(type_label);
            } catch (IOException e) {
                throw new RepositoryException(e.getCause());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RepositoryException("Cannot invoke reflective method: getStaticMetaData 1");
            }
        } else {

            try {
                LXPMetadata LXPMetadata = bucketType.newInstance().getMetaData(); // (metadata) bucketType.getDeclaredMethod("getStaticMetaData").invoke(null);
                type_label = LXPMetadata.getType().getId();

                if (getMapType() != type_label) {
                    throw new RepositoryException("Bucket label incompatible with supplied class: " + bucketType.getName() + " doesn't match field_storage type label:" + map_type_label_id);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                throw new RepositoryException("Cannot invoke reflective method: getStaticMetaData 2");
            }
        }
    }

    private long getMapType() {

        if (map_type_label_id != -1) {
            return map_type_label_id;
        } // only look it up if not cached.

        Path path = directory.toPath();
        Path typepath = path.resolve(META_BUCKET_NAME).resolve(MAP_TYPE_LABEL_FILE_NAME);

        try (BufferedReader reader = Files.newBufferedReader(typepath, FileManipulation.FILE_CHARSET)) {

            String id_as_string = reader.readLine();
            map_type_label_id = Long.parseLong(id_as_string);
            return map_type_label_id;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMapType(long type_label_id) throws IOException {

        if (this.map_type_label_id != -1) {
            throw new IOException("Type label already set");
        }
        this.map_type_label_id = type_label_id; // cache it and keep a persistent copy of the label.

        Path path = directory.toPath();
        Path meta_path = path.resolve(META_BUCKET_NAME);
        FileManipulation.createDirectoryIfDoesNotExist(meta_path);

        Path typepath = meta_path.resolve(MAP_TYPE_LABEL_FILE_NAME);
        if (Files.exists(typepath)) {
            throw new IOException("Map Type label already set");
        }
        FileManipulation.createFileIfDoesNotExist((typepath));

        try (BufferedWriter writer = Files.newBufferedWriter(typepath, FileManipulation.FILE_CHARSET)) {

            writer.write(Long.toString(type_label_id)); // Write the $$$$id$$$$id$$$$ of the typelabel OID into this field.
            writer.newLine();
        }
    }
}
