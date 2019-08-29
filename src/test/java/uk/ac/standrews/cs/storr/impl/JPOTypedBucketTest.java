package uk.ac.standrews.cs.storr.impl;

import org.junit.Before;
import uk.ac.standrews.cs.storr.impl.exceptions.BucketException;
import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.RepositoryException;
import uk.ac.standrews.cs.storr.interfaces.IBucket;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class JPOTypedBucket extends CommonTest {

    private static String generic_bucket_name1 = "JPOBucket";

    @Before
    public void setUp() throws RepositoryException, IOException, URISyntaxException {

        super.setUp();

        repository.makeBucket(generic_bucket_name1, BucketKind.DIRECTORYBACKED, Person.class);

        TypeFactory type_factory = store.getTypeFactory();
    }


    public synchronized void testJPOCreation() throws RepositoryException, IllegalKeyException, BucketException {
        IBucket<Person> b = repository.getBucket(generic_bucket_name1);
        Person p = new Person(42, "home");

        b.makePersistent(p);

        long id = p.getId();
        Person ppp = (Person) b.getObjectById(id);
        assertEquals(ppp, p);
    }
}
