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

    private static String generic_bucket_name1 = "BUCKET1";
    private static List<String> LEGAL_NAMES = Arrays.asList("$$$bucket$$$bucket$$$", "a $$$bucket$$$bucket$$$");
    private static List<String> ILLEGAL_NAMES = Arrays.asList("a: $$$bucket$$$bucket$$$","a/$$$bucket$$$bucket$$$","a<$$$bucket$$$bucket$$$","a\\$$$bucket$$$bucket$$$?");

    @Test
    public void createBucketTest() throws RepositoryException {

        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repository.bucketExists(generic_bucket_name1));
        assertEquals(generic_bucket_name1, repository.getBucket(generic_bucket_name1).getName());
    }

    @Test
    public void deleteBucketTest() throws RepositoryException {

        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);
        repository.deleteBucket(generic_bucket_name1);

        assertFalse(repository.bucketExists(generic_bucket_name1));
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