package uk.ac.standrews.cs.storr.impl;

import org.junit.Test;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.BucketKind;
import uk.ac.standrews.cs.storr.interfaces.IBucket;

import static org.junit.Assert.*;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class RepositoryTest extends CommonTest {

    private static String generic_bucket_name1 = "BUCKET1";

    @Test
    public void createBucketTest() throws RepositoryException {
        IBucket bucket = repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repo.bucketExists(generic_bucket_name1));
        assertEquals(bucket.getName(), repo.getBucket(generic_bucket_name1).getName());
    }

    @Test
    public void deleteBucketTest() throws RepositoryException {
        repo.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repo.bucketExists(generic_bucket_name1));
        repo.deleteBucket(generic_bucket_name1);

        assertFalse(repo.bucketExists(generic_bucket_name1));
    }
}