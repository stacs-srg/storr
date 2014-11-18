/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
