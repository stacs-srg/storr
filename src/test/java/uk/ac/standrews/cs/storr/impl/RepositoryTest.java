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

import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static uk.ac.standrews.cs.storr.impl.Repository.bucketNameIsLegal;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RepositoryTest extends CommonTest {

    private static final String GENERIC_BUCKET_NAME = "BUCKET1";
    private static final List<String> LEGAL_NAMES = Arrays.asList("$$$bucket$$$bucket$$$", "a $$$bucket$$$bucket$$$");
    private static final List<String> ILLEGAL_NAMES = Arrays.asList("a: $$$bucket$$$bucket$$$","a/$$$bucket$$$bucket$$$","a<$$$bucket$$$bucket$$$","a\\$$$bucket$$$bucket$$$?");

    @Test
    public void createBucketTest() throws RepositoryException {

        repository.makeBucket(GENERIC_BUCKET_NAME, BucketKind.DIRECTORYBACKED);

        assertTrue(repository.bucketExists(GENERIC_BUCKET_NAME));
        assertEquals(GENERIC_BUCKET_NAME, repository.getBucket(GENERIC_BUCKET_NAME).getName());
    }

    @Test
    public void deleteBucketTest() throws RepositoryException {

        repository.makeBucket(GENERIC_BUCKET_NAME, BucketKind.DIRECTORYBACKED);
        repository.deleteBucket(GENERIC_BUCKET_NAME);

        assertFalse(repository.bucketExists(GENERIC_BUCKET_NAME));
    }

    @Test
    public void nameLegalityTest() {

        for (String name : LEGAL_NAMES) {
            assertTrue(bucketNameIsLegal(name));
        }

        for (String name : ILLEGAL_NAMES) {
            assertFalse(bucketNameIsLegal(name));
        }
    }
}