package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.storr.util.FileManipulation;

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

        if (!legal_name(name)) {
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
        } else {
            return null;
        }
    }

    @Override
    public Iterator<IRepository> getIterator() {
        return new RepoIterator(this, repository_path);
    }


    @Override
    public Watcher getWatcher() {
        return watcher;
    }

    ////////////////// private and protected methods //////////////////

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


    private static boolean legal_name(String name) { // TODO May want to strengthen these conditions
        return name != null && !name.equals("");
    }

    private static class RepoIterator implements Iterator<IRepository> {

        private final Iterator<File> file_iterator;
        private final IStore store;

        public RepoIterator(final IStore store, final Path repo_directory) {

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
                ErrorHandling.exceptionError(e, "RepositoryException in iterator");
                return null;
            }
        }

        @Override
        public void remove() {

            throw new UnsupportedOperationException("remove called on stream - unsupported");
        }
    }
}
