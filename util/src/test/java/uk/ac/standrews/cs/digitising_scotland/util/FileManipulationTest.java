package uk.ac.standrews.cs.digitising_scotland.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Created by graham on 02/05/2014.
 */
public class FileManipulationTest {

    private static final int DIRECTORY_COUNT = 10;
    private static final int FILE_PER_DIRECTORY_COUNT = 50;
    public static final String TEMP_FILE_TREE_ROOT = "src/test/resources/temp_file_tree";
    private static final Random RANDOM = new Random(42);

    private String temp_file_tree_root;

    @Before
    public void setUp() throws IOException {

        temp_file_tree_root = TEMP_FILE_TREE_ROOT + RANDOM.nextInt();
        createFileTree();
        assertTreeContainsExpectedNumberOfDirectories();
    }

    @After
    public void tearDown() throws IOException {

        assertNotExists(temp_file_tree_root);
    }

    @Test
    public void createAndDeleteFileTree1() throws IOException {

        FileManipulation.deleteDirectory1(temp_file_tree_root);
    }

    @Test
    public void createAndDeleteFileTree2() throws IOException {

        FileManipulation.deleteDirectory2(temp_file_tree_root);
    }

    @Test
    public void createAndDeleteFileTree3() throws IOException {

        FileManipulation.deleteDirectory3(temp_file_tree_root);
    }

    @Test
//    public void createAndDeleteFileTree4() throws IOException {
//
//        FileManipulation.deleteDirectory4(temp_file_tree_root);
//    }

    private void createFileTree() throws IOException {

        createRoot();

        for (int i = 0; i < DIRECTORY_COUNT; i++) {

            final String random_dir_name = String.valueOf(RANDOM.nextLong());

            final File sub_directory = new File(temp_file_tree_root, random_dir_name);
            createSubDirectory(sub_directory);

            for (int j = 0; j < FILE_PER_DIRECTORY_COUNT; j++) {

                final File file = new File(sub_directory, String.valueOf(RANDOM.nextLong()));
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {

                    final byte[] random_content = new byte[0xFFF];
                    RANDOM.nextBytes(random_content);
                    out.write(random_content);
                }
            }

            assertNumberOfEntries(sub_directory, FILE_PER_DIRECTORY_COUNT);
        }
    }

    private void assertNumberOfEntries(File directory, int count) {

        File[] entries = directory.listFiles();
        assertNotNull(entries);
        assertEquals(count, entries.length);
    }

    private void createSubDirectory(File sub_directory) {

        sub_directory.mkdirs();
        assertExists(sub_directory);
    }

    private void assertTreeContainsExpectedNumberOfDirectories() {

        assertNumberOfEntries(new File(temp_file_tree_root), DIRECTORY_COUNT);
    }

    private void assertExists(String directory_path) {

        assertExists(new File(directory_path));
    }

    private void assertNotExists(String directory_path) {

        if (new File(directory_path).exists()) fail();
    }

    private void assertExists(File sub_directory) {

        if (!sub_directory.exists()) fail();
    }

    private void createRoot() throws IOException {

        FileManipulation.createDirectoryIfDoesNotExist(temp_file_tree_root);
        assertExists(temp_file_tree_root);
    }
}
