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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

public class TrainAndMultiplyClassifyTest {

    @Test
    public void test() {

        File experiment0 = new File("target/Experiment0");
        File experiment1 = new File("target/Experiment1");
        System.out.println(experiment0.getAbsolutePath());
        if (!experiment0.mkdirs()) {
            System.err.println("Could not create experiment0 Folder");
        }
        if (!experiment1.mkdirs()) {
            System.err.println("Could not create experiment1 Folder");
        }
        final String experimentalFolderName = Utils.getExperimentalFolderName("target", "Experiment");
        Assert.assertEquals("target/Experiment2", experimentalFolderName);
        experiment0.delete();
        experiment1.delete();
    }

}
