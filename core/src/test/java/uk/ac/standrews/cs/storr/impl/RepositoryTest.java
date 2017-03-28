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
        IBucket bucket = repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repository.bucketExists(generic_bucket_name1));
        assertEquals(bucket.getName(), repository.getBucket(generic_bucket_name1).getName());
    }

    @Test
    public void deleteBucketTest() throws RepositoryException {
        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED);

        assertTrue(repository.bucketExists(generic_bucket_name1));
        repository.deleteBucket(generic_bucket_name1);

        assertFalse(repository.bucketExists(generic_bucket_name1));
    }
}