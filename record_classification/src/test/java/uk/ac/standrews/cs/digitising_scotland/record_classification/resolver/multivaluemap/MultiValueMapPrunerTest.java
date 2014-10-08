package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.IHierarchyResolver;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.TestInfoPrintTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.HierarchyResolver;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMapPruner;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.ResolverPipelineTools;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationComparator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.ClassificationSetValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific.LengthWeightedLossFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by fraserdunlop on 07/10/2014 at 12:40.
 */
@RunWith(Parameterized.class)
public class MultiValueMapPrunerTest {

    private IMultiValueMapPruner<Code, Classification,ClassificationComparator> pruner = new MultiValueMapPruner<>(new ClassificationComparator());
    private MultiValueMapTestHelper mvmHelper;


    public MultiValueMapPrunerTest(final IHierarchyResolver<Code,Classification> resolver){
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
        assertPrunedCorrectly(map,                 1000,                   1,               256,                4);
        assertPrunedCorrectly(map,                  256,                   1,               256,                4);
        assertPrunedCorrectly(map,                  255,                   1,               192,                4);
        assertPrunedCorrectly(map,                   16,                   1,                16,                4);
        assertPrunedCorrectly(map,                    1,                   2,                16,                4);
        assertPrunedCorrectly(map,                    1,                   4,               256,                4);
        assertPrunedCorrectly(map,                    1,                  10,               256,                4);
        assertPrunedCorrectly(map,                    1,                   3,                81,                4);
        assertPrunedCorrectly(map,                   81,                   1,                81,                4);
        assertPrunedCorrectly(map,                   70,                   1,                54,                4);
        assertPrunedCorrectly(map,                   17,                   1,                16,                4);
    }

    private void assertPrunedCorrectly(MultiValueMap<Code, Classification> map,
                                       int complexityUpperBound,
                                       int listLengthLowerBound,
                                       int expectedComplexity,
                                       int expectedKeySetSize) throws IOException, ClassNotFoundException {
        pruner.setComplexityUpperBound(complexityUpperBound);
        pruner.setListLengthLowerBound(listLengthLowerBound);
        MultiValueMap<Code, Classification> map4 = pruner.pruneUntilComplexityWithinBound(map);
        Assert.assertEquals(expectedComplexity, map4.complexity());
        Assert.assertEquals(expectedKeySetSize, map4.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetListLengthLowerBoundIncorrectly(){
        pruner.setListLengthLowerBound(-1);
    }
}
