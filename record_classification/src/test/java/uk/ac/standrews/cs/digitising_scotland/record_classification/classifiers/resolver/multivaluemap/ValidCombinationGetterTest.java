package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.multivaluemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.LossFunctionApplier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic.ValidCombinationGetter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.ClassificationSetValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.LengthWeightedLossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 15:34.
 */
public class ValidCombinationGetterTest {

    private ValidCombinationGetter<Code, Classification, TokenSet, ClassificationSetValidityAssessor> vCG = new ValidCombinationGetter<>(new ClassificationSetValidityAssessor());
    private MultiValueMapTestHelper mvmHelper;
    private LossFunction<Multiset<Classification>, Double> lengthWeighted = new LengthWeightedLossFunction();
    private LossFunctionApplier<Classification, Double, LengthWeightedLossFunction> lossFunctionApplier = new LossFunctionApplier<>(new LengthWeightedLossFunction());

    @Before
    public void setup() throws IOException, CodeNotValidException {

        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white", "2100", 0.85);
        mvmHelper.addMockEntryToMatrix("brown", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white", "2200", 0.87);
        mvmHelper.addMockEntryToMatrix("brown", "4215", 0.87);
        mvmHelper.addMockEntryToMatrix("white", "4215", 0.8);
        mvmHelper.addMockEntryToMatrix("brown", "6700", 0.85);
        mvmHelper.addMockEntryToMatrix("white", "6700", 0.83);
    }

    @Test
    public void getValidCodeTriplesTest() throws Exception {

        TokenSet originalSet = new TokenSet("brown white");
        List<Multiset<Classification>> validTriples = vCG.getValidSets(mvmHelper.getMap(), originalSet);
        Assert.assertEquals(20, validTriples.size());
        mvmHelper.addMockEntryToMatrix("blue", "3000", 0.83);
        TokenSet originalSet1 = new TokenSet("brown white blue");
        validTriples = vCG.getValidSets(mvmHelper.getMap(), originalSet1);
        Assert.assertEquals(41, validTriples.size());
        for (Multiset<Classification> set : validTriples) {
            Assert.assertEquals(1.5, lengthWeighted.calculate(set), 1.5);
        }
        Set<Classification> best = lossFunctionApplier.getBest(validTriples);
        Double averageConfidence = 0.;
        for (Classification triple : best) {
            averageConfidence += triple.getConfidence();
        }
        Assert.assertEquals((2 * 0.87 + 0.83), averageConfidence, 0.001);
    }

    @Test
    public void testLossFunctionApplierReturnsEmptySetWithEmptyGetBestArg() {

        List<Multiset<Classification>> classifications = new ArrayList<>();
        Assert.assertTrue(lossFunctionApplier.getBest(classifications).isEmpty());
    }
}
