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