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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.BlockDistance;
import uk.ac.shef.wit.simmetrics.similaritymetrics.ChapmanLengthDeviation;
import uk.ac.shef.wit.simmetrics.similaritymetrics.DiceSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaccardSimilarity;
import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

/** 
 *  Testing Closest Match Map with the simple String Length Similarity Metric.
 * We make a map of of strings to strings where values= their keys. We randomly
 * generate strings and get the closest match then check that there are no other
 * elements of the key set with greater similarity than the one chosen by ClosestMatchMap.
 * Created by fraserdunlop on 01/10/2014 at 15:31.
 */
@RunWith(Parameterized.class)
public class ParameterisedSimmetricsClosestMatchTest {

    private Map<String, String> map;
    private SimilarityMetric<String> metric;
    private int randomStringLengthUpperBound;
    private int numTests;

    public ParameterisedSimmetricsClosestMatchTest(final AbstractStringMetric metric) {

        SimilarityMetricFromSimmetricFactory factory = new SimilarityMetricFromSimmetricFactory();
        this.metric = factory.create(metric);

    }

    @Parameters
    public static Collection<AbstractStringMetric[]> strings() {

        return Arrays.asList(new AbstractStringMetric[][]{{new DiceSimilarity()}, {new ChapmanLengthDeviation()}, {new BlockDistance()}, {new JaccardSimilarity()}, {new JaroWinkler()}});
    }

    @Before
    public void setUp() {

        SimilarityMetricFromSimmetricFactory factory = new SimilarityMetricFromSimmetricFactory();

        numTests = 50;
        randomStringLengthUpperBound = 50;
        AbstractStringMetric abstractMetric = new JaccardSimilarity();
        metric = factory.create(abstractMetric);

        Map<String, String> map = new HashMap<>();
        String[] strings = new String[]{"apple", "banana", "bonobo", "fricassee", "fermentation vessel", "morose wanderer", "challenged to a fight in the danger zone", "lanaaaaaaaaaaaaa"};
        for (String string : strings) {
            map.put(string, string);
        }
        this.map = map;
    }

    @Test
    public void test() {

        ClosestMatchMap<String, String> closestMatchMap = new ClosestMatchMap<>(metric, map);
        for (int i = 0; i < numTests; i++) {
            assertRandomStringGetsClosestMatch(closestMatchMap);
        }

    }

    private void assertRandomStringGetsClosestMatch(final ClosestMatchMap<String, String> closestMatchMap) {

        String randomString = generateRandomString();
        String match = closestMatchMap.getClosestMatch(randomString);
        Set<String> keySet = closestMatchMap.keySet();
        for (String key : keySet) {

            final boolean condition = metric.getSimilarity(randomString, match) >= metric.getSimilarity(randomString, key);
            if (!condition) {
                System.out.println("failed on random string: " + randomString + " key: " + key);
            }
            Assert.assertTrue(condition);
        }
    }

    private String generateRandomString() {

        return randomString(randomStringLengthUpperBound);
    }

    private String randomString(final int lengthUpperBound) {

        Random rnd = new Random();
        int len = rnd.nextInt(lengthUpperBound);
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(len);
        for (int i = -1; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

}
