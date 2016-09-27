package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.nds.util.ErrorHandling;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.storr.interfaces.IObjectCache;
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
//    private final static String ID_FILE_NAME = "id_file"; // // no longer needed for pseudo random ids

    private static Path store_path = null;
    private final Path repo_path;

    private final IObjectCache object_cache;
    private static SecureRandom sr = new SecureRandom();
    // private int id = 1;

    private final Map<String,IRepository> repo_cache;

    private ITransactionManager tm = null;

    protected Store() throws StoreException {

        if( store_path == null ) {
            throw new StoreException( "Null store path specified" );
        }
        this.repo_path = store_path.resolve( REPO_DIR_NAME );

        checkCreate(store_path);
        checkCreate(repo_path);

        repo_cache = new HashMap<String,IRepository>();
        object_cache = new ObjectCache();
    }

    public void setTransactionManager( ITransactionManager trans_manager ) throws StoreException {
        if( tm != null ) {
            throw new StoreException( "Transaction Manager already set" );
        }
        this.tm = trans_manager;
    }

    protected static void set_store_path( Path path ) {
        store_path = path;
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return tm;
    }

    @Override
    public IRepository makeRepository(final String name) throws RepositoryException {

        if(!legal_name(name)) {
            throw new RepositoryException( "Illegal Repository name <" + name + ">" );
        }
        createRepository(name);
        IRepository r = getRepo(name);
        repo_cache.put(name,r);
        return r;
    }

    @Override
    public boolean repoExists(String name) {
        return Files.exists(getRepoPath(name));
    }

    @Override
    public void deleteRepo(String name) throws RepositoryException {
        if (!repoExists(name)) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }
        try {
            FileManipulation.deleteDirectory(getRepoPath(name));
        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name);
        }
    }

    @Override
    public IRepository getRepo(String name) throws RepositoryException {
        if( repoExists(name) ) {
            if( repo_cache.containsKey(name)) {
                return repo_cache.get(name);
            } else {
                IRepository r = new Repository(repo_path, name);
                repo_cache.put(name,r);
                return r;
            }
        } else {
            return null;
        }
    }

    @Override
    public Iterator<IRepository> getIterator() {
        return new RepoIterator(this, repo_path);
    }


    @Override
    public IObjectCache getObjectCache() {
        return object_cache;
    }

    /******************** private and protected methods ********************/

    /**
     * @return the next free pid
     */
    protected static long getNextFreePID() {
        return getPRN();
    }

    private static long prn = 1;

    /**
     * @return a pseudo random positive long
     */
    private static long getPRN() {

        return prn++;

        //TODO prn is commented out. **********************

//        long next_prn;
//        do {
//            next_prn = sr.nextLong();
//        } while (next_prn <= 0);
//        return next_prn;
    }

    private void checkCreate(Path dir) throws StoreException {

        if (!Files.exists( dir )) {  // only create if it doesn't exist - try and make the directory

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

        return repo_path.resolve(name);
    }

    private void createRepository(String name) throws RepositoryException {
        if (repoExists(name)) {
            throw new RepositoryException("Repo: " + name + " already exists");
        }

        try {
            FileManipulation.createDirectoryIfDoesNotExist(getRepoPath(name));

        } catch (IOException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private static boolean legal_name(String name) { // TODO May want to strengthen these conditions
        return name != null && ! name.equals( "" );
    }

    private static class RepoIterator implements Iterator<IRepository> {

        private final Iterator<File> file_iterator;
        private final IStore store;

        public RepoIterator(final IStore store, final Path repo_directory) {

            this.store = store;
            file_iterator = FileIteratorFactory.createFileIterator(repo_directory.toFile(), false, true);
        }

        public boolean hasNext() {
            return file_iterator.hasNext();
        }

        @Override
        public IRepository next() {

            String name = file_iterator.next().getName();

            try {
                return store.getRepo(name);

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
