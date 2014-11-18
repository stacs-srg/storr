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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.loss_functions;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.AverageLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LogLengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.SumLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 * Tests that the calculate methods for each class that extends AbstractLossFunction works as expected.
 * @author jkc25
 *
 */
public class LossFunctionTest {

    private LossFunction<Multiset<Classification>, Double> sumLoss;
    private LossFunction<Multiset<Classification>, Double> averageLoss;
    private LossFunction<Multiset<Classification>, Double> lengthWeighted;
    private LossFunction<Multiset<Classification>, Double> logLengthWeighted;
    private Multiset<Classification> classifications;

    @Before
    public void setUp() throws Exception {

        sumLoss = new SumLossFunction();
        averageLoss = new AverageLossFunction();
        lengthWeighted = new LengthWeightedLossFunction();
        logLengthWeighted = new LogLengthWeightedLossFunction();
        classifications = createClassificationSet();

    }

    private Multiset<Classification> createClassificationSet() {

        Multiset<Classification> set = HashMultiset.create();

        Classification c0 = new Classification(null, new TokenSet("Foo"), 1.0);
        Classification c1 = new Classification(null, new TokenSet("Foo"), 0.5);
        Classification c2 = new Classification(null, new TokenSet("Foo Bar"), 0.4);
        Classification c3 = new Classification(null, new TokenSet("Foo Bar Foo"), 0.4);
        Classification c4 = new Classification(null, new TokenSet("Foo Foo Bar Bar"), 0.1);
        set.add(c0);
        set.add(c1);
        set.add(c2);
        set.add(c3);
        set.add(c4);

        return set;
    }

    @Test
    public void testSumLoss() {

        Assert.assertEquals(2.4, sumLoss.calculate(classifications), 0.001);
    }

    @Test
    public void testAverageLoss() {

        Assert.assertEquals(0.48, averageLoss.calculate(classifications), 0.001);
    }

    @Test
    public void testLengthLoss() {

        Assert.assertEquals(3.9, lengthWeighted.calculate(classifications), 0.001);
    }

    @Test
    public void testLogLengthLoss(){
        Assert.assertEquals(2.195,logLengthWeighted.calculate(classifications),0.001);
    }
}
