package uk.ac.standrews.cs.storr.examples;

import uk.ac.standrews.cs.storr.impl.BucketKind;
import uk.ac.standrews.cs.storr.impl.LXP;
import uk.ac.standrews.cs.storr.impl.Store;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by al and simone on 26/8/2016
 */
public class SimplestExample {

    public static void main(String[] args) throws IOException, StoreException, RepositoryException, BucketException {

        Path tempStorePath = Files.createTempDirectory("/tmp/xyz");
        IStore store = new Store(tempStorePath);
        IRepository repo = store.makeRepository("repo");
        IBucket b = repo.makeBucket("bucket", BucketKind.DIRECTORYBACKED);

        LXP lxp = new LXP();
        lxp.put("age", 42);
        lxp.put("address", "home");

        b.makePersistent(lxp);
    }
}
