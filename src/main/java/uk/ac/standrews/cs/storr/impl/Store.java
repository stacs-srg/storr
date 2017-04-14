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
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by al on 06/06/2014.
 */
public class Store implements IStore {

    private final static String REPO_DIR_NAME = "REPOS";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Path repository_path;
    private final Map<String, IRepository> repository_cache;

    private Watcher watcher;
    private ITransactionManager transaction_manager;
    private TypeFactory type_factory;

    public Store(Path store_path) throws StoreException {

        repository_path = store_path.resolve(REPO_DIR_NAME);
        repository_cache = new HashMap<>();

        checkCreate(store_path);
        checkCreate(repository_path);

        try {
            watcher = new Watcher();
            watcher.startService();

            transaction_manager = new TransactionManager(this);
            type_factory = new TypeFactory(this);

        } catch (IOException | RepositoryException e) {
            throw new StoreException(e.getMessage());
        }
    }

    /**
     * @return the next free pid
     */
    static long getNextFreePID() {
        return getPRN();
    }

    /**
     * @return a pseudo random positive long
     */
    private static long getPRN() {

        long next_prn;
        do {
            next_prn = RANDOM.nextLong();
        } while (next_prn <= 0);

        return next_prn;
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return transaction_manager;
    }

    @Override
    public TypeFactory getTypeFactory() {
        return type_factory;
    }

    @Override
    public IRepository makeRepository(final String name) throws RepositoryException {

        if (!Helper.NameIsLegal(name)) {
            throw new RepositoryException("Illegal Repository name <" + name + ">");
        }
        createRepository(name);
        IRepository r = getRepository(name);
        repository_cache.put(name, r);
        return r;
    }

    @Override
    public boolean repositoryExists(String name) {

        return Files.exists(getRepoPath(name));
    }

    @Override
    public void deleteRepository(String name) throws RepositoryException {
        if (!repositoryExists(name)) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }
        try {
            FileManipulation.deleteDirectory(getRepoPath(name));
        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name);
        }
    }

    ////////////////// private and protected methods //////////////////

    @Override
    public IRepository getRepository(String name) throws RepositoryException {

        if (repositoryExists(name)) {
            if (repository_cache.containsKey(name)) {
                return repository_cache.get(name);
            } else {
                IRepository r = new Repository(this, name, repository_path);
                repository_cache.put(name, r);
                return r;
            }
        }
        throw new RepositoryException("repository does not exist: " + name);
    }

    @Override
    public Iterator<IRepository> getIterator() {
        return new RepositoryIterator(this, repository_path);
    }

    @Override
    public Watcher getWatcher() {
        return watcher;
    }

    private void checkCreate(Path dir) throws StoreException {

        if (!Files.exists(dir)) {  // only create if it doesn't exist - try and make the directory

            try {
                FileManipulation.createDirectoryIfDoesNotExist(dir);
            } catch (IOException e) {
                throw new StoreException("Directory " + dir.toString() + " does not exist and cannot be created");
            }

        } else { // it does exist - check that it is a directory

            if (!Files.isDirectory(dir)) {
                throw new StoreException(dir.toString() + " exists but is not a directory");
            }
        }
    }

    private Path getRepoPath(final String name) {

        return repository_path.resolve(name);
    }

    private void createRepository(String name) throws RepositoryException {

        if (repositoryExists(name)) {
            throw new RepositoryException("Repo: " + name + " already exists at: " + getRepoPath(name));
        }

        try {
            FileManipulation.createDirectoryIfDoesNotExist(getRepoPath(name));

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private static class RepositoryIterator implements Iterator<IRepository> {

        private final Iterator<File> file_iterator;
        private final IStore store;

        public RepositoryIterator(final IStore store, final Path repo_directory) {

            this.store = store;
            file_iterator = new FileIterator(repo_directory.toFile(), false, true);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public IRepository next() {

            String name = file_iterator.next().getName();

            try {
                return store.getRepository(name);

            } catch (RepositoryException e) {
                throw new RuntimeException("RepositoryException in iterator", e);
            }
        }

        @Override
        public void remove() {

            throw new UnsupportedOperationException("remove called on stream - unsupported");
        }
    }
}
