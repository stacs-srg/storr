package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

// TODO: Auto-generated Javadoc
/**
 * Unit tests to test functionality of the {@link MultiValueMap} class.
 * @author jkc25, frjd2
 * Created by fraserdunlop on 10/06/2014 at 15:14.
 */
public class ResolverMatrixTest {

    /** The ResolverMatrix. */
    private MultiValueMap<Code,Classification> matrix;
    private MultiValueMapPruner<Code,Classification,ClassificationComparator> pruner
            = new MultiValueMapPruner<>(new ClassificationComparator());
    private HierarchyResolver resolver = new HierarchyResolver();
    private CodeDictionary codeDictionary;
    private ValidCodeTripleGetter validCodeTripleGetter = new ValidCodeTripleGetter();
    private BelowThresholdRemover belowThresholdRemover = new BelowThresholdRemover();

    /**
     * Setup, run before each test. Creates a new {@link MultiValueMap} and sets the {@link CodeIndexer} to use
     * a compatible code map.
     */
    @Before
    public void setup() {

        File codeDictionaryFile = new File("src/test/resources/CodeFactoryTestFile.txt");
        try {
            codeDictionary = new CodeDictionary(codeDictionaryFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        matrix = new MultiValueMap<>(new HashMap<Code,List<Classification>>());

    }

    /**
     * Tests that s {@link Classification} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning true.
     * @throws CodeNotValidException 
     */
    @Test
    public void tripleSetIsValidTest() throws CodeNotValidException {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = codeDictionary.getCode("2100");
        Classification codeTriple = new Classification(code, new TokenSet("brown dog"), 1.0);
        Set<Classification> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertTrue(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link Classification} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning false.
     * @throws CodeNotValidException 
     */
    @Test
    public void tripleSetIsValidTest2() throws CodeNotValidException {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = codeDictionary.getCode("2100");
        Classification codeTriple = new Classification(code, new TokenSet("the the the brown dog"), 1.0);
        Set<Classification> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertFalse(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link Classification} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning true.
     * @throws CodeNotValidException 
     */
    @Test
    public void tripleSetIsValidTest3() throws CodeNotValidException {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = codeDictionary.getCode("2100");
        Classification codeTriple = new Classification(code, new TokenSet("the brown dog brown dog dog"), 1.0);
        Set<Classification> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertTrue(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link Classification} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning false.
     * @throws CodeNotValidException 
     */
    @Test
    public void tripleSetIsValidTest4() throws CodeNotValidException {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = codeDictionary.getCode("2100");
        Classification codeTriple = new Classification(code, new TokenSet("the brown dog brown dog dog bat"), 1.0);
        Set<Classification> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertFalse(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Gets the valid code triples test.
     *

     */
    @Test
    public void getValidCodeTriplesTest() {

        addMockEntryToMatrix("brown", "2100", 0.5);
        addMockEntryToMatrix("white", "2100", 0.85);
        addMockEntryToMatrix("brown", "2200", 0.81);
        addMockEntryToMatrix("white", "2200", 0.87);
        addMockEntryToMatrix("brown", "4215", 0.87);
        addMockEntryToMatrix("white", "4215", 0.8);
        addMockEntryToMatrix("brown", "6700", 0.85);
        addMockEntryToMatrix("white", "6700", 0.83);
        TokenSet originalSet = new TokenSet("brown white");
        List<Set<Classification>> validTriples = validCodeTripleGetter.getValidCodeTriples(matrix,originalSet);
        Assert.assertEquals(20, validTriples.size());
        addMockEntryToMatrix("blue", "3000", 0.83);
        TokenSet originalSet1 = new TokenSet("brown white blue");
        validTriples = validCodeTripleGetter.getValidCodeTriples(matrix,originalSet1);
        Assert.assertEquals(41, validTriples.size());
        for (Set<Classification> set : validTriples) {
            Assert.assertEquals(1.5, ResolverUtils.lossFunction(set), 1.5);
        }
        Set<Classification> best = ResolverUtils.getBest(validTriples);
        Double averageConfidence = 0.;
        for (Classification triple : best) {
            averageConfidence += triple.getConfidence();
        }
        Assert.assertEquals((2 * 0.87 + 0.83), averageConfidence, 0.001);
    }

    /**
     * Resolve hierarchies test.
     */
    @Test
    public void resolveHierarchiesTest() throws IOException, ClassNotFoundException {

        addMockEntryToMatrix("brown dog", "2100", 0.5);
        addMockEntryToMatrix("white dog", "2100", 0.85);
        addMockEntryToMatrix("brown dog", "2200", 0.81);
        addMockEntryToMatrix("white dog", "2200", 0.87);
        addMockEntryToMatrix("brown dog", "952", 0.87);
        addMockEntryToMatrix("white dog", "952", 0.8);
        addMockEntryToMatrix("brown dog", "95240", 0.85);
        addMockEntryToMatrix("white dog", "95240", 0.83);
        Assert.assertEquals(16, matrix.complexity());
        matrix = resolver.moveAncestorsToDescendantKeys(matrix);
        Assert.assertEquals(16, matrix.complexity());
    }

    /**
     * Resolve hierarchies test.
     */
    @Test
    public void resolveHierarchiesSingleClassificationTest() throws IOException, ClassNotFoundException {

        matrix = new MultiValueMap<>(new HashMap<Code,List<Classification>>());
        addMockEntryToMatrix("brown dog", "2100", 0.5);
        addMockEntryToMatrix("white dog", "2100", 0.85);
        addMockEntryToMatrix("brown dog", "2200", 0.81);
        addMockEntryToMatrix("white dog", "2200", 0.87);
        addMockEntryToMatrix("brown dog", "952", 0.87);
        addMockEntryToMatrix("white dog", "952", 0.8);
        addMockEntryToMatrix("brown dog", "95240", 0.85);
        addMockEntryToMatrix("white dog", "95240", 0.83);
        Assert.assertEquals(4, matrix.size());
        MultiValueMap<Code,Classification> matrix2 = resolver.moveAncestorsToDescendantKeys(matrix);
        Assert.assertEquals(3,matrix2.size());
        Assert.assertEquals(matrix.complexity(), matrix2.complexity());
    }

    /**
     * Chop below confidence test.
     */
    @Test
    public void chopBelowConfidenceTest() throws IOException, ClassNotFoundException {

        addMockEntryToMatrix("brown dog", "2100", 0.5);
        addMockEntryToMatrix("white dog", "2100", 0.85);
        addMockEntryToMatrix("brown dog", "2200", 0.81);
        addMockEntryToMatrix("white dog", "2200", 0.87);
        Assert.assertEquals(4, matrix.complexity());
        MultiValueMap matrix2 = belowThresholdRemover.removeBelowThreshold(matrix,0.7);
        Assert.assertEquals(2, matrix2.complexity());
    }

    /**
     * Complexity test.
     */
    @Test
    public void complexityTest() {

        addMockEntryToMatrix("brown dog", "2100", 0.8);
        Assert.assertEquals(1, matrix.complexity());
        addMockEntryToMatrix("white dog", "2100", 0.85);
        Assert.assertEquals(2, matrix.complexity());
        addMockEntryToMatrix("brown dog", "2200", 0.81);
        Assert.assertEquals(2, matrix.complexity());
        addMockEntryToMatrix("white dog", "2200", 0.87);
        Assert.assertEquals(4, matrix.complexity());
    }

    /**
     * Adds the mock entry to matrix.
     *
     * @param string the string
     * @param codeAsString the code
     * @param conf the conf
     */
    private void addMockEntryToMatrix(final String string, final String codeAsString, final double conf) {

        TokenSet tokenSet = new TokenSet(string);
        Code code = null;
        try {
            code = codeDictionary.getCode(codeAsString);
        }
        catch (CodeNotValidException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        matrix.add(code,new Classification(code,tokenSet,conf));
    }
}
