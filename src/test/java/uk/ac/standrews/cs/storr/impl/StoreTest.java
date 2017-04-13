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

import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.impl.exceptions.StoreException;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StoreTest extends CommonTest {

    private static final String REPO_NAME = "repo";
    private static String generic_bucket_name1 = "BUCKET1";

    @Before
    public void setUp() throws RepositoryException, IOException, StoreException, URISyntaxException {

        super.setUp();
        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
    }

    @Test
    public void deleteRepositoryTest() throws RepositoryException {

        store.deleteRepository(REPO_NAME);
        assertFalse(store.repositoryExists(REPO_NAME));
    }

    @Test
    public void checkRepositoryHasBeenCreatedTest() throws RepositoryException {

        assertTrue(store.repositoryExists(REPO_NAME));
        assertEquals(repository.getRepositoryPath(), store.getRepository(REPO_NAME).getRepositoryPath());
    }
}
