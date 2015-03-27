package uk.ac.standrews.cs.jstore.impl;

import uk.ac.standrews.cs.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.jstore.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.jstore.interfaces.IObjectCache;
import uk.ac.standrews.cs.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.jstore.interfaces.IStore;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Iterator;

/**
 * Created by al on 06/06/2014.
 */
public class Store implements IStore {

    private final static String REPO_DIR_NAME = "REPOS";
//    private final static String ID_FILE_NAME = "id_file"; // // no longer needed for pseudo random ids

    private static String store_path = null;
    private final String repo_path;
    private final File store_root_directory;
    private final File repo_directory;
//    private final File id_file; // no longer needed for pseudo random ids

    private final IObjectCache object_cache;
    private static SecureRandom sr = new SecureRandom();
    // private int id = 1;

    private ITransactionManager tm = null;

    protected Store() throws StoreException {

        if( store_path == null ) {
            throw new StoreException( "Null store path specified" );
        }
        this.repo_path = store_path + File.separator + REPO_DIR_NAME;

        store_root_directory = new File(store_path);
        repo_directory = new File(repo_path);
        // no longer needed for pseudo random ids:
        // id_file = new File(store_path + File.separator + ID_FILE_NAME);

        checkCreate(store_root_directory);
        checkCreate(repo_directory);

        object_cache = new ObjectCache();
    }

    public void setTransactionManager( ITransactionManager trans_manager ) throws StoreException {
        if( tm != null ) {
            throw new StoreException( "Transaction Manager already set" );
        }
        this.tm = trans_manager;
    }

    protected static void set_store_path( String path ) {
        store_path = path;
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return tm;
    }

    @Override
    public IRepository makeRepository(final String name) throws RepositoryException {

        if( ! legal_name( name ) ) {
            throw new RepositoryException( "Illegal Repository name <" + name + ">" );
        }
        createRepository(name);
        return getRepo(name);
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
        return new Repository(repo_path, name);
    }

    @Override
    public Iterator<IRepository> getIterator() {
        return new RepoIterator(this, repo_directory);
    }


    @Override
    public IObjectCache getObjectCache() {
        return object_cache;
    }

    /******************** private methods ********************/

    public static long getNextFreePID() {
        return getPRN();
    }

    /**
     * @return a pseudo random positive long
     */
    private static long getPRN() {

        long next_prn;
        do {
            next_prn = sr.nextLong();
        } while (next_prn <= 0);
        return next_prn;
    }

    private void checkCreate(File root_dir) throws StoreException {
        if (!root_dir.exists()) {  // only create if it doesn't exist - try and make the directory

            if (!root_dir.mkdir()) {
                throw new StoreException("Directory " + root_dir.getAbsolutePath() + " does not exist and cannot be created");
            }

        } else { // it does exist - check that it is a directory
            if (!root_dir.isDirectory()) {
                throw new StoreException(root_dir.getAbsolutePath() + " exists but is not a directory");
            }
        }
    }

    private Path getRepoPath(final String name) {

        return Paths.get(repo_path).resolve(name);
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

        public RepoIterator(final IStore store, final File repo_directory) {

            this.store = store;
            file_iterator = FileIteratorFactory.createFileIterator(repo_directory, false, true);
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
