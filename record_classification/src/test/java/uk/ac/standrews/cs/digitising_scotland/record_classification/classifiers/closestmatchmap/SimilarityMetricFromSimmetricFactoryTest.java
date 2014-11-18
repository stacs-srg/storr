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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;

public class SimilarityMetricFromSimmetricFactoryTest {

    SimilarityMetricFromSimmetricFactory factory;

    @Before
    public void setUp() throws Exception {

        factory = new SimilarityMetricFromSimmetricFactory();
    }

    @Test
    public void testTheSameString() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(1.0, metric.getSimilarity("Foo", "Foo"), 0.0001);

    }

    @Test
    public void testOverLap() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(0.5, metric.getSimilarity("Foo Bar", "Foo"), 0.0001);

    }

    @Test
    public void testDifferntString() {

        SimilarityMetric<String> metric = factory.create(new JaccardSimilarity());
        Assert.assertEquals(0.0, metric.getSimilarity("Foo", "Bar"), 0.0001);

    }

}
