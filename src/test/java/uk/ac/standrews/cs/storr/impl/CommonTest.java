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

import org.junit.After;
import org.junit.Before;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;
import uk.ac.standrews.cs.storr.interfaces.IRepository;
import uk.ac.standrews.cs.storr.interfaces.IStore;
import uk.ac.standrews.cs.utilities.FileManipulation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CommonTest {

    static final String REPOSITORY_NAME = "repo";

    private static final boolean DEBUG = false;

    protected IStore store;
    protected IRepository repository;

    Path store_path;

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        store_path = Files.createTempDirectory(null);
        store = new Store(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been created");
        }

        repository = store.makeRepository(REPOSITORY_NAME);
    }

    @After
    public void tearDown() throws IOException {

        FileManipulation.deleteDirectory(store_path);

        if (DEBUG) {
            System.out.println("STORE PATH = " + store_path + " has been deleted");
        }
    }
}
