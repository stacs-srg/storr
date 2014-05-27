package uk.ac.standrews.cs.digitising_scotland.generic_linkage.impl;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IIndexedBucket;
import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.IRepository;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.File;
import java.io.IOException;

/**
 * A Collection of buckets identified by a file path representing its root.
 * Created by al on 11/05/2014.
 */
public class Repository implements IRepository {

    private String repo_path;
    private File repo_directory;

    public Repository(String repo_path) throws RepositoryException {
        this.repo_path = repo_path;

        repo_directory = new File(repo_path);

        if (! repo_directory.exists() ) {  // only if the repo doesn't exist - try and make the directory

                if (!repo_directory.mkdir()) {
                    throw new RepositoryException("Directory " + repo_directory.getAbsolutePath() + " does not exist and cannot be created");
                }

        }  else { // it does exist - check that it is a directory
            if( ! repo_directory.isDirectory() ) {
                throw new RepositoryException( repo_directory.getAbsolutePath() + " exists but is not a directory");
            }
        }
    }

    @Override
    public IBucket makeBucket(String name) throws RepositoryException {
        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        String path = repo_path + "/" + name;
        File dir = new File(path);
        if (!dir.mkdir()) {
            ErrorHandling.error("Cannot create bucket " + name);
            return null;
        }
        return getBucket(name);
    }

    @Override
    public IIndexedBucket makeIndexedBucket(String name) throws RepositoryException {
        if (bucketExists(name)) {
            throw new RepositoryException("Bucket: " + name + " already exists");
        }

        String path = repo_path + "/" + name;
        File dir = new File(path);
        if (!dir.mkdir()) {
            ErrorHandling.error("Cannot create bucket " + name);
            return null;
        }
        return getIndexedBucket(name);
    }



    @Override
    public boolean bucketExists(String name) {
        String path = repo_path + "/" + name;
        File dir = new File(path);
        return dir.exists();
    }

    @Override
    public void deleteBucket(String name) throws RepositoryException {
        String path = repo_path + "/" + name;
        File dir = new File(path);
        if( ! dir.exists() ) {
            throw new RepositoryException("Bucket with " + name + "does not exist");
        }

        try {
            FileManipulation.deleteDirectory(path);
        } catch (IOException e) {
            throw new RepositoryException("Cannot delete bucket: " + name );
        }
    }

    @Override
    public IBucket getBucket(String name) throws RepositoryException {
        try {
            return new Bucket(name, repo_path);
        } catch (Exception e) {
            throw new RepositoryException("Cannot get bucket called " + name );
        }
    }

    public IIndexedBucket getIndexedBucket(String name) throws RepositoryException {
        try {
            return new IndexedBucket(name, repo_path);
        } catch (Exception e) {
            throw new RepositoryException("Cannot get indexed bucket called " + name );
        }
    }

    @Override
    public RepositoryIterator getIterator() {
        return new RepositoryIterator(this, repo_directory);
    }
}
