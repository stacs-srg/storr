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
import uk.ac.standrews.cs.storr.interfaces.*;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private static final String ILLEGAL_CHARS_MAC = ":";
    private static final String ILLEGAL_CHARS_LINUX = "/\0";
    private static final String ILLEGAL_CHARS_WINDOWS = "<>:\"/\\|?\\*";

    private static final String ILLEGAL_CHARS = ILLEGAL_CHARS_MAC + ILLEGAL_CHARS_LINUX + ILLEGAL_CHARS_WINDOWS;
    private static final String LEGAL_CHARS_PATTERN = "[^" + ILLEGAL_CHARS + "]*";

    private final IStore store;
    private final String repository_name;
    private final Path repository_path;
    private final File repository_directory;

    private final Map<String, IBucket> bucket_cache;

    Repository(IStore store, String repository_name, final Path base_path) throws RepositoryException {

        if (!repositoryNameIsLegal(repository_name)) {
            throw new RepositoryException("Illegal repository name <" + repository_name + ">");
        }

        this.store = store;
        this.repository_name = repository_name;
        repository_path = base_path.resolve(repository_name);
        repository_directory = repository_path.toFile();

        bucket_cache = new HashMap<>();

        if (!repository_directory.exists()) {  // only if the repo doesn't exist - try and make the directory

            if (!repository_directory.mkdir()) {
                throw new RepositoryException("Directory " + repository_directory.getAbsolutePath() + " does not exist and cannot be created");
            }

        } else { // it does exist - check that it is a directory
            if (!repository_directory.isDirectory()) {
                throw new RepositoryException(repository_directory.getAbsolutePath() + " exists but is not a directory");
            }
        }
    }

    @Override
    public IBucket makeBucket(final String bucket_name, BucketKind kind) throws RepositoryException {

        IBucket bucket = BucketKind.createBucket(this, bucket_name, kind);
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    @Override
    public <T extends ILXP> IBucket<T> makeBucket(final String bucket_name, BucketKind kind, ILXPFactory<T> tFactory) throws RepositoryException {

        IBucket bucket = BucketKind.createBucket(this, bucket_name, tFactory, kind);
        bucket_cache.put(bucket_name, bucket);
        return bucket;
    }

    @Override
    public boolean bucketExists(final String name) {

        return bucketNameIsLegal(name) && Files.exists(getBucketPath(name));
    }

    @Override
    public void deleteBucket(final String name) throws RepositoryException {

        if (!bucketExists(name)) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }

        try {
            FileManipulation.deleteDirectory(getBucketPath(name));
            bucket_cache.remove(name);

        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name);
        }
    }

    @Override
    public IBucket getBucket(final String bucket_name) throws RepositoryException {

        if (bucketExists(bucket_name)) {

            final IBucket bucket = bucket_cache.get(bucket_name);
            return bucket != null ? bucket : BucketKind.getBucket(this, bucket_name);
        }
        throw new RepositoryException("bucket does not exist: " + bucket_name);
    }

    @Override
    public <T extends ILXP> IBucket<T> getBucket(final String bucket_name, final ILXPFactory<T> tFactory) throws RepositoryException {

        if (bucketExists(bucket_name)) {

            final IBucket bucket = bucket_cache.get(bucket_name);
            return bucket != null ? bucket : BucketKind.getBucket(this, bucket_name, tFactory);
        }
        throw new RepositoryException("bucket does not exist: " + bucket_name);
    }

    @Override
    public Iterator<String> getBucketNameIterator() {

        return new BucketNamesIterator(repository_directory);
    }

    @Override
    public <T extends ILXP> Iterator<IBucket<T>> getIterator(ILXPFactory<T> tFactory) {
        return new BucketIterator(this, repository_directory, tFactory);
    }

    @Override
    public Path getRepositoryPath() {
        return repository_path;
    }

    @Override
    public String getName() {
        return repository_name;
    }

    @Override
    public IStore getStore() {
        return store;
    }

    /**
     * Check that the repository name is legal.
     * A name is legal if:
     * - it exists and it has at least one character
     * - it is a valid file name for the file system
     *
     * TODO - consider limiting the size of the name to 31 characters for better compatability with old file systems?
     * @param name to be checked
     * @return true if the name is legal
     */
    public static boolean bucketNameIsLegal(String name) {

        return name.matches(LEGAL_CHARS_PATTERN);
    }

    public static boolean repositoryNameIsLegal(String name) {

        return name.matches(LEGAL_CHARS_PATTERN);
    }

    private Path getBucketPath(final String name) {

        return repository_path.resolve(name);
    }

    private static class BucketNamesIterator implements Iterator<String> {

        private final Iterator<File> file_iterator;

        BucketNamesIterator(final File repo_directory) {
            file_iterator = new FileIterator(repo_directory, false, true);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public String next() {
            return file_iterator.next().getName();
        }
    }

    private static class BucketIterator<T extends ILXP> implements Iterator<IBucket<T>> {

        private final Repository repository;
        private final Iterator<File> file_iterator;
        private final ILXPFactory<T> tFactory;

        BucketIterator(final Repository repository, final File repository_directory, ILXPFactory<T> tFactory) {

            this.repository = repository;
            file_iterator = new FileIterator(repository_directory, false, true);
            this.tFactory = tFactory;
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public IBucket<T> next() {

            try {
                return repository.getBucket(file_iterator.next().getName(), tFactory);

            } catch (RepositoryException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }
    }
}
