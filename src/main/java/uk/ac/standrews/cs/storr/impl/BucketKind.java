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
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.ILXP;
import uk.ac.standrews.cs.storr.interfaces.ILXPFactory;
import uk.ac.standrews.cs.storr.interfaces.IRepository;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class used to represent a bucket implementation kind.
 * Each kind of bucket has a subtly different semantics/functionality
 * Created by al on 01/08/2014.
 */
public enum BucketKind {

    UNKNOWN, // an error case
    DIRECTORYBACKED,
    INDIRECT,
    IDMAP,
    STRINGMAP,
    INDEXED;

    public static IBucket getBucket(Repository repository, String bucket_name) throws RepositoryException {

        return retrieveBucket(repository, bucket_name, getKind(repository, bucket_name), false);
    }

    public static IBucket createBucket(Repository repository, String bucket_name, BucketKind kind) throws RepositoryException {

        return retrieveBucket(repository, bucket_name, kind, true);
    }

    public static <T extends ILXP> IBucket<T> getBucket(Repository repository, String bucket_name, ILXPFactory<T> tFactory) throws RepositoryException {

        BucketKind kind = getKind(repository, bucket_name);

        return retrieveBucket(repository, bucket_name, tFactory, kind, false);
    }

    public static <T extends ILXP> IBucket createBucket(Repository repository, String bucket_name, ILXPFactory<T> tFactory, BucketKind kind) throws RepositoryException {

        return retrieveBucket(repository, bucket_name, tFactory, kind, true);
    }

    private static IBucket retrieveBucket(Repository repository, String bucket_name, BucketKind kind, boolean create_bucket) throws RepositoryException {

        switch (kind) {

            case DIRECTORYBACKED: {
                return new DirectoryBackedBucket(repository, bucket_name, kind, create_bucket);
            }
            case INDIRECT: {
                return new DirectoryBackedIndirectBucket(repository, bucket_name, create_bucket);
            }
            case INDEXED: {
                return new DirectoryBackedIndexedBucket(repository, bucket_name, create_bucket);
            }
        }

        throw new RepositoryException("invalid bucket kind");
    }

    private static <T extends ILXP> IBucket retrieveBucket(Repository repository, String bucket_name, ILXPFactory<T> tFactory, BucketKind kind, boolean create_bucket) throws RepositoryException {

        switch (kind) {

            case DIRECTORYBACKED: {
                return new DirectoryBackedBucket(repository, bucket_name, kind, tFactory, create_bucket);
            }
            case INDIRECT: {
                return new DirectoryBackedIndirectBucket(repository, bucket_name, tFactory, create_bucket);
            }
            case INDEXED: {
                return new DirectoryBackedIndexedBucket(repository, bucket_name, tFactory, create_bucket);
            }
        }

        throw new RepositoryException("invalid bucket kind");
    }

    private static BucketKind getKind(IRepository repository, String bucket_name) throws RepositoryException {

        Path meta_path = repository.getRepositoryPath().resolve(bucket_name).resolve(DirectoryBackedBucket.META_BUCKET_NAME); // repo/bucketname/meta

        if (Files.exists(meta_path.resolve(DIRECTORYBACKED.name()))) {
            return DIRECTORYBACKED;
        }
        if (Files.exists(meta_path.resolve(INDEXED.name()))) {
            return INDEXED;
        }
        if (Files.exists(meta_path.resolve(INDIRECT.name()))) {
            return INDIRECT;
        }
        if (Files.exists(meta_path.resolve(IDMAP.name()))) {
            return IDMAP;
        }
        if (Files.exists(meta_path.resolve(STRINGMAP.name()))) {
            return STRINGMAP;
        }
        throw new RepositoryException("invalid bucket kind");
    }
}
