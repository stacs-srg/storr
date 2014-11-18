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
package uk.ac.standrews.cs.digitising_scotland.record_classification.legacy.lda;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TopicModelTest {

    @After
    public void tearDown() {

        FileUtils.deleteQuietly(new File("target/LDAExecutionTimes.txt"));
        FileUtils.deleteQuietly(new File("target/ldaTagged.txt"));
        FileUtils.deleteQuietly(new File("target/ldaModel.model"));

    }

    @Test
    public void test() {

        TopicModel tm = new TopicModel();
        File input = new File(getClass().getResource("/ldaTest.txt").getFile());
        tm.process(input);
        Assert.assertTrue(new File("target/ldaTagged.txt").exists());
    }
}
