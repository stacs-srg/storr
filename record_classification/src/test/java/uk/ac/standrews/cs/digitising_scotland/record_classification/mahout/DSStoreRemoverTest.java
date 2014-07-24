package uk.ac.standrews.cs.digitising_scotland.record_classification.mahout;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.fileutils.DSStoreRemover;

/**
 * Tests .DS_Store remover.
 * @author jkc25
 *
 */
public class DSStoreRemoverTest {

    /**
     * Creates a .DS_Store file then removes it. Passes if file is created and removed without exception.
     * @throws IOException is reading error occurs.
     */
    @Test
    public void test() throws IOException {

        File dsStore = new File("target/.DS_Store");
        File test = new File("target");
        if (!test.mkdirs()) {
            System.err.print("Could not create folder " + test.getAbsolutePath());
        }
        if (!dsStore.createNewFile()) {
            System.err.print("Could not create folder " + dsStore.getAbsolutePath());

        }
        dsStore.canRead();
        assertTrue(dsStore.isFile());
        assertTrue(dsStore.exists());
        File here = new File(".");
        DSStoreRemover dsr = new DSStoreRemover();
        dsr.remove(here);
        assertTrue(!dsStore.exists());
    }
}
