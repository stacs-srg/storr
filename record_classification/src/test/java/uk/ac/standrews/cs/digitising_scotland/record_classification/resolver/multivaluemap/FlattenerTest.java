package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IBelowThresholdRemover;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IFlattener;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.TestInfoPrintTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.BelowThresholdRemover;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.Flattener;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.ResolverPipelineTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationComparator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationSetValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.LengthWeightedLossFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 12:22.
 */
@RunWith(Parameterized.class)
public class FlattenerTest {


    IFlattener<Code,Classification> flattener = new Flattener<>();
    private MultiValueMapTestHelper mvmHelper;

    public FlattenerTest(final IFlattener<Code,Classification> flattener){
        this.flattener = flattener;
        TestInfoPrintTools printTools = new TestInfoPrintTools();
        printTools.printTestSubjectInfo(this, flattener);
    }

    @Parameterized.Parameters
    public static List<IFlattener[]> params() {
        List<IFlattener[]> params = new ArrayList<>();
        IFlattener<Code,Classification> flattener1 =
                new Flattener<>();
        IFlattener<Code,Classification> resolverPipelineTools =
                new ResolverPipelineTools<>(new LengthWeightedLossFunction(),
                        new ClassificationComparator(),
                        new ClassificationSetValidityAssessor(),0.7);
        addToParams(params, flattener1);
        addToParams(params, resolverPipelineTools);
        return params;
    }

    private static void addToParams(List<IFlattener[]> params, IFlattener<Code, Classification> remover) {
        IFlattener[] array = new IFlattener[1];
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

    @Test
    public void testFlattener() throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> map = mvmHelper.getMap();
        Assert.assertEquals(4, map.complexity());
        Assert.assertEquals(2, map.size());
        MultiValueMap<Code,Classification> map2 = flattener.moveAllIntoKey(map,map.iterator().next());
        Assert.assertEquals(4, map2.complexity());
        Assert.assertEquals(1, map2.size());
    }
}
