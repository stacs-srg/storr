package uk.ac.standrews.cs.digitising_scotland.jstore.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.exceptions.StoreException;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.impl.TransactionManager;
import uk.ac.standrews.cs.digitising_scotland.jstore.impl.transaction.interfaces.ITransactionManager;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IObjectCache;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IStore;
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

    private final String store_path;
    private final String repo_path;
    private final File store_root_directory;
    private final File repo_directory;
//    private final File id_file; // no longer needed for pseudo random ids

    private static IStore instance = null;
    private final IObjectCache object_cache;
    private static SecureRandom sr = new SecureRandom();
    // private int id = 1;

    private static ITransactionManager tm = null;

    @SuppressFBWarnings(value = "ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD", justification = "intended behaviour")
    public Store(String store_path) throws StoreException, IOException {

        this.store_path = store_path;
        this.repo_path = store_path + File.separator + REPO_DIR_NAME;

        store_root_directory = new File(store_path);
        repo_directory = new File(repo_path);
        // no longer needed for pseudo random ids:
        // id_file = new File(store_path + File.separator + ID_FILE_NAME);

        checkCreate(store_root_directory);
        checkCreate(repo_directory);

        object_cache = new ObjectCache();
        instance = this;

        try {
            this.tm = new TransactionManager();  // This references the instance object that this constructor creates - is this safe?
        } catch (RepositoryException e) {
            throw new StoreException( e );
        }
    }

    public synchronized static IStore getInstance() {

        if (instance == null) {
            ErrorHandling.hardError("No Store specified");
            return null;
        }
        return instance;
    }


    @Override
    public ITransactionManager getTransactionManager() {
        return tm;
    }

    @Override
    public IRepository makeRepository(final String name) throws RepositoryException {

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
    public long getNextFreePID() {
        return getPRN();
    }


    @Override
    public IObjectCache getObjectCache() {
        return object_cache;
    }

    /******************** private methods ********************/

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

// These method were used for saving ids before pseudo random longs were used.
//
//    private void initId() throws IOException {
//
//        try (BufferedReader reader = Files.newBufferedReader(Paths.getString(id_file.getAbsolutePath()), FileManipulation.FILE_CHARSET)) {
//
//            String line = reader.readLine();
//            if (line == null) throw new IOException("Couldn't read ID from file");
//            id = Integer.parseInt(line);
//        }
//    }
//
//    private void saveId() throws IOException {
//
//        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.getString(id_file.getAbsolutePath()), FileManipulation.FILE_CHARSET))) {
//            writer.println(id);
//            writer.println();
//        }
//    }
//
//    private void checkCreateId() throws IOException, StoreException {
//        if (!id_file.exists()) { // only create this file if it doesn't exist
//
//            if (!id_file.createNewFile()) {
//                throw new StoreException("ID file " + id_file.getAbsolutePath() + " does not exist and cannot be created");
//            }
//            saveId(); // initialise the persistent counter
//        }
//    }

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
