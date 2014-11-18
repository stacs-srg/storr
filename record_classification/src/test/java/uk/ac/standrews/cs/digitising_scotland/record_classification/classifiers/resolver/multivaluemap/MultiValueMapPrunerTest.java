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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.multivaluemap;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.MultiValueMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.MultiValueMapPruner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.ClassificationComparator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 12:40.
 */
public class MultiValueMapPrunerTest {

    private MultiValueMapPruner<Code, Classification, ClassificationComparator> pruner = new MultiValueMapPruner<>(new ClassificationComparator());
    private MultiValueMapTestHelper mvmHelper;

    @Before
    public void setup() throws IOException, CodeNotValidException {

        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown dog", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white dog", "2100", 0.75);
        mvmHelper.addMockEntryToMatrix("brown dog", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white dog", "2200", 0.67);
        mvmHelper.addMockEntryToMatrix("brown dog", "952", 0.87);
        mvmHelper.addMockEntryToMatrix("white dog", "952", 0.4);
        mvmHelper.addMockEntryToMatrix("brown dog", "95240", 0.85);
        mvmHelper.addMockEntryToMatrix("white dog", "95240", 0.83);
        mvmHelper.addMockEntryToMatrix("red dog", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("blue dog", "2100", 0.75);
        mvmHelper.addMockEntryToMatrix("red dog", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("blue dog", "2200", 0.67);
        mvmHelper.addMockEntryToMatrix("red dog", "952", 0.87);
        mvmHelper.addMockEntryToMatrix("blue dog", "952", 0.4);
        mvmHelper.addMockEntryToMatrix("red dog", "95240", 0.85);
        mvmHelper.addMockEntryToMatrix("blue dog", "95240", 0.83);
    }

    /**
     * Resolve hierarchies test.
     */
    @Test
    public void prunerTest() throws IOException, ClassNotFoundException {

        MultiValueMap<Code, Classification> map = mvmHelper.getMap();
        //params :-           map, complexityUpperBound,listLengthLowerBound,expectedComplexity,expectedKeySetSize
        assertPrunedCorrectly(map, 1000, 1, 256, 4);
        assertPrunedCorrectly(map, 256, 1, 256, 4);
        assertPrunedCorrectly(map, 255, 1, 192, 4);
        assertPrunedCorrectly(map, 16, 1, 16, 4);
        assertPrunedCorrectly(map, 1, 2, 16, 4);
        assertPrunedCorrectly(map, 1, 4, 256, 4);
        assertPrunedCorrectly(map, 1, 10, 256, 4);
        assertPrunedCorrectly(map, 1, 3, 81, 4);
        assertPrunedCorrectly(map, 81, 1, 81, 4);
        assertPrunedCorrectly(map, 70, 1, 54, 4);
        assertPrunedCorrectly(map, 17, 1, 16, 4);
    }

    private void assertPrunedCorrectly(final MultiValueMap<Code, Classification> map, final int complexityUpperBound, final int listLengthLowerBound, final int expectedComplexity, final int expectedKeySetSize) throws IOException, ClassNotFoundException {

        pruner.setComplexityUpperBound(complexityUpperBound);
        pruner.setListLengthLowerBound(listLengthLowerBound);
        MultiValueMap<Code, Classification> map4 = pruner.pruneUntilComplexityWithinBound(map);
        Assert.assertEquals(expectedComplexity, map4.complexity());
        Assert.assertEquals(expectedKeySetSize, map4.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetListLengthLowerBoundIncorrectly() {

        pruner.setListLengthLowerBound(-1);
    }
}
