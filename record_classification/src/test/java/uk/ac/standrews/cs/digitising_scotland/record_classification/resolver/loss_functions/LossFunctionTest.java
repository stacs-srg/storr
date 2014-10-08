package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.loss_functions;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.AverageLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.SumLossFunction;

/**
 * Tests that the calculate methods for each class that extends AbstractLossFunction works as expected.
 * @author jkc25
 *
 */
public class LossFunctionTest {

    private LossFunction<Set<Classification>,Double> sumLoss;
    private LossFunction<Set<Classification>,Double> averageLoss;
    private LossFunction<Set<Classification>,Double> lengthWeighted;
    private Set<Classification> classifications;

    @Before
    public void setUp() throws Exception {

        sumLoss = new SumLossFunction();
        averageLoss = new AverageLossFunction();
        lengthWeighted = new LengthWeightedLossFunction();
        classifications = createClassificationSet();

    }

    private Set<Classification> createClassificationSet() {

        Set<Classification> set = new HashSet<Classification>();

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
}
