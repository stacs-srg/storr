package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IFlattener;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IHierarchyResolver;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.TestInfoPrintTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.Flattener;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.HierarchyResolver;
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
 * Created by fraserdunlop on 07/10/2014 at 12:33.
 */
@RunWith(Parameterized.class)
public class HierarchyResolverTest {

    private IHierarchyResolver<Code, Classification> resolver = new HierarchyResolver<>();
    private MultiValueMapTestHelper mvmHelper;

    public HierarchyResolverTest(final IHierarchyResolver<Code,Classification> resolver){
        this.resolver = resolver;
        TestInfoPrintTools printTools = new TestInfoPrintTools();
        printTools.printTestSubjectInfo(this, resolver);
    }

    @Parameterized.Parameters
    public static List<IHierarchyResolver[]> params() {
        List<IHierarchyResolver[]> params = new ArrayList<>();
        IHierarchyResolver<Code,Classification> hierarchyResolver =
                new HierarchyResolver<>();
        IHierarchyResolver<Code,Classification> resolverPipelineTools =
                new ResolverPipelineTools<>(new LengthWeightedLossFunction(),
                        new ClassificationComparator(),
                        new ClassificationSetValidityAssessor(),0.7);
        addToParams(params, hierarchyResolver);
        addToParams(params, resolverPipelineTools);
        return params;
    }

    private static void addToParams(List<IHierarchyResolver[]> params, IHierarchyResolver<Code, Classification> remover) {
        IHierarchyResolver[] array = new IHierarchyResolver[1];
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
        mvmHelper.addMockEntryToMatrix("brown dog", "952", 0.87);
        mvmHelper.addMockEntryToMatrix("white dog", "952", 0.8);
        mvmHelper.addMockEntryToMatrix("brown dog", "95240", 0.85);
        mvmHelper.addMockEntryToMatrix("white dog", "95240", 0.83);
    }

    /**
     * Resolve hierarchies test.
     */
    @Test
    public void resolveHierarchiesTest() throws IOException, ClassNotFoundException {
        MultiValueMap<Code, Classification> map = mvmHelper.getMap();
        Assert.assertEquals(16, map.complexity());
        Assert.assertEquals(4, map.size());
        MultiValueMap<Code, Classification> map2 = resolver.moveAncestorsToDescendantKeys(map);
        Assert.assertEquals(16, map2.complexity());
        Assert.assertEquals(3, map2.size());
    }
}
