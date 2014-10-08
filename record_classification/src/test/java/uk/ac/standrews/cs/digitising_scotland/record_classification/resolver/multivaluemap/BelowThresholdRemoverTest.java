package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.TestInfoPrintTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.BelowThresholdRemover;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IBelowThresholdRemover;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.ResolverPipelineTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationComparator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationSetValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.LengthWeightedLossFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Testing BelowThresholdRemover with Codes and Classifications.
 * Created by fraserdunlop on 07/10/2014 at 11:53.
 */
@RunWith(Parameterized.class)
public class BelowThresholdRemoverTest {

    private IBelowThresholdRemover<Code, Classification, Double> belowThresholdRemover;
    private MultiValueMapTestHelper mvmHelper;

    public BelowThresholdRemoverTest(final IBelowThresholdRemover<Code, Classification, Double> iBelowThresholdRemover){
        this.belowThresholdRemover = iBelowThresholdRemover;
        TestInfoPrintTools printTools = new TestInfoPrintTools();
        printTools.printTestSubjectInfo(this, belowThresholdRemover);
    }

    @Parameterized.Parameters
    public static List<IBelowThresholdRemover[]> params() {
        List<IBelowThresholdRemover[]> params = new ArrayList<>();
        IBelowThresholdRemover<Code,Classification,Double> belowThresholdRemover =
                new BelowThresholdRemover<>(0.7);
        IBelowThresholdRemover<Code,Classification,Double> resolverPipelineTools =
                new ResolverPipelineTools<>(new LengthWeightedLossFunction(),
                                            new ClassificationComparator(),
                                            new ClassificationSetValidityAssessor(),0.7);
        addToParams(params, belowThresholdRemover);
        addToParams(params, resolverPipelineTools);
        return params;
    }

    private static void addToParams(List<IBelowThresholdRemover[]> params, IBelowThresholdRemover<Code, Classification, Double> remover) {
        IBelowThresholdRemover[] array = new IBelowThresholdRemover[1];
        array[0] = remover;
        params.add(array);
    }

    @Before
    public void setup() throws IOException, CodeNotValidException {
        mvmHelper = new MultiValueMapTestHelper();
        mvmHelper.addMockEntryToMatrix("brown dog", "2100", 0.5);
        mvmHelper.addMockEntryToMatrix("white dog", "2100", 0.85);
        mvmHelper.addMockEntryToMatrix("brown dog", "2200", 0.81);
        mvmHelper.addMockEntryToMatrix("white dog", "2200", 0.87);
    }

    /**
     * Chop below confidence test.
     */
    @Test
    public void removeBelowThresholdTest() throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> map = mvmHelper.getMap();
        Assert.assertEquals(4, map.complexity());
        MultiValueMap<Code, Classification> matrix2 = belowThresholdRemover.removeBelowThreshold(map);
        Assert.assertEquals(2, matrix2.complexity());
    }
}
