package uk.ac.standrews.cs.jstore.impl;

import org.junit.Test;
import uk.ac.standrews.cs.jstore.interfaces.IStore;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class StoreFactoryTest {

    @Test
    public void testGetStore() throws Exception {
        Path tempStore = Files.createTempDirectory(null);
        StoreFactory.setStorePath(tempStore);

        IStore store = StoreFactory.getStore();
        assertNotNull(store);
    }

    @Test
    public void testMakeStore() throws Exception {
        Path tempStore = Files.createTempDirectory(null);
        StoreFactory.setStorePath(tempStore);

        IStore store = StoreFactory.makeStore();
        assertNotNull(store);
    }

    @Test
    public void testGetSameStore() throws Exception {
        Path tempStore = Files.createTempDirectory(null);
        StoreFactory.setStorePath(tempStore);

        IStore store = StoreFactory.getStore();
        assertNotNull(store);

        IStore otherStore = StoreFactory.getStore();

        assertEquals(store, otherStore);
    }

    @Test
    public void testSetPathDoesNotAffectGetStore() throws Exception {
        Path tempStore = Files.createTempDirectory(null);
        StoreFactory.setStorePath(tempStore);

        IStore store = StoreFactory.getStore();
        assertNotNull(store);

        Path otherTempStore = Files.createTempDirectory("other-path");
        StoreFactory.setStorePath(otherTempStore);

        IStore otherStore = StoreFactory.getStore();

        assertEquals(store, otherStore);
    }

}