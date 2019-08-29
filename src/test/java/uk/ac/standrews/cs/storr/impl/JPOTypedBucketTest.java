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

import org.junit.jupiter.api.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class JPOTypedBucketTest {

    public String generic_bucket_name1 = "JPOBucket";
    public String REPOSITORY_NAME = "repo";
    public boolean DEBUG = true;

    public IStore store;
    public IRepository repository;

    Path store_path;

    @Test
    public synchronized void testJPOCreation() throws RepositoryException, IllegalKeyException, BucketException, IOException {
        store_path = Files.createTempDirectory(null);
        store = new Store(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been created");
        }

        repository = store.makeRepository(REPOSITORY_NAME);

        try {
            repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED, Person.class);
        } catch( Exception e ) {
            System.out.println( "Exception - $$$bucket$$$bucket$$$ already exists?: " + e );
        }

        IBucket<Person> b = repository.getBucket(generic_bucket_name1, Person.class);

        Person p = new Person(42, "home");

        b.makePersistent(p);

        long id = p.getId();
        Person ppp = (Person) b.getObjectById(id);
        assertEquals(ppp, p);

        FileManipulation.deleteDirectory(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been deleted");
        }
    }
}
