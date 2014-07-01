package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverUtils;

/**
 * Unit tests to test functionality of the {@link ResolverMatrix} class.
 * @author jkc25, frjd2
 * Created by fraserdunlop on 10/06/2014 at 15:14.
 */
public class ResolverMatrixTest {

    /** The ResolverMatrix. */
    private ResolverMatrix matrix;

    /**
     * Setup, run before each test. Creates a new {@link ResolverMatrix} and sets the {@link CodeFactory} to use
     * a compatible code map.
     */
    @Before
    public void setup() {
        matrix = new ResolverMatrix();
    }

    /**
     * Tests that s {@link CodeTriple} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning true.
     */
    @Test
    public void tripleSetIsValidTest() {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = CodeFactory.getInstance().getCode(0);
        CodeTriple codeTriple = new CodeTriple(code, new TokenSet("brown dog"), 1.0);
        Set<CodeTriple> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertTrue(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link CodeTriple} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning false.
     */
    @Test
    public void tripleSetIsValidTest2() {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = CodeFactory.getInstance().getCode(0);
        CodeTriple codeTriple = new CodeTriple(code, new TokenSet("the the the brown dog"), 1.0);
        Set<CodeTriple> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertFalse(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link CodeTriple} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning true.
     */
    @Test
    public void tripleSetIsValidTest3() {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = CodeFactory.getInstance().getCode(0);
        CodeTriple codeTriple = new CodeTriple(code, new TokenSet("the brown dog brown dog dog"), 1.0);
        Set<CodeTriple> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertTrue(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Tests that s {@link CodeTriple} is a valid subset of another set using ResolverUtils.tripleSetIsValid().
     * Should pass with ResolverUtils.tripleSetIsValid returning false.
     */
    @Test
    public void tripleSetIsValidTest4() {

        TokenSet originalSet = new TokenSet("the brown dog brown dog dog");
        Code code = CodeFactory.getInstance().getCode(0);
        CodeTriple codeTriple = new CodeTriple(code, new TokenSet("the brown dog brown dog dog bat"), 1.0);
        Set<CodeTriple> tripleSet = new HashSet<>();
        tripleSet.add(codeTriple);
        Assert.assertFalse(ResolverUtils.tripleSetIsValid(tripleSet, originalSet));
    }

    /**
     * Gets the valid code triples test.
     *
     */
    @Test
    public void getValidCodeTriplesTest() {

        addMockEntryToMatrix("brown", 0, 0.5);
        addMockEntryToMatrix("white", 0, 0.85);
        addMockEntryToMatrix("brown", 1, 0.81);
        addMockEntryToMatrix("white", 1, 0.87);
        addMockEntryToMatrix("brown", 3, 0.87);
        addMockEntryToMatrix("white", 3, 0.8);
        addMockEntryToMatrix("brown", 4, 0.85);
        addMockEntryToMatrix("white", 4, 0.83);
        TokenSet originalSet = new TokenSet("brown white");
        List<Set<CodeTriple>> validTriples = matrix.getValidCodeTriples(originalSet);
        Assert.assertEquals(20, validTriples.size());
        addMockEntryToMatrix("blue", 2, 0.83);
        TokenSet originalSet1 = new TokenSet("brown white blue");
        validTriples = matrix.getValidCodeTriples(originalSet1);
        Assert.assertEquals(41, validTriples.size());
        for (Set<CodeTriple> set : validTriples) {
            Assert.assertEquals(1.5, ResolverUtils.lossFunction(set), 1.5);
        }
        Set<CodeTriple> best = ResolverUtils.getBest(validTriples);
        Double averageConfidence = 0.;
        for (CodeTriple triple : best) {
            averageConfidence += triple.getConfidence();
        }
        Assert.assertEquals((2 * 0.87 + 0.83), averageConfidence, 0.001);
    }

    /**
     * Resolve hierarchies test.
     */
    @Test
    public void resolveHierarchiesTest() {

        addMockEntryToMatrix("brown dog", 0, 0.5);
        addMockEntryToMatrix("white dog", 0, 0.85);
        addMockEntryToMatrix("brown dog", 1, 0.81);
        addMockEntryToMatrix("white dog", 1, 0.87);
        addMockEntryToMatrix("brown dog", "952", 0.87);
        addMockEntryToMatrix("white dog", "952", 0.8);
        addMockEntryToMatrix("brown dog", "95240", 0.85);
        addMockEntryToMatrix("white dog", "95240", 0.83);
        Assert.assertEquals(16, matrix.complexity());
        matrix.resolveHierarchies();
        Assert.assertEquals(16, matrix.complexity());
        System.out.println(matrix.toString());
    }

    /**
     * Chop below confidence test.
     */
    @Test
    public void chopBelowConfidenceTest() {

        addMockEntryToMatrix("brown dog", 0, 0.5);
        addMockEntryToMatrix("white dog", 0, 0.85);
        addMockEntryToMatrix("brown dog", 1, 0.81);
        addMockEntryToMatrix("white dog", 1, 0.87);
        Assert.assertEquals(4, matrix.complexity());
        matrix.chopBelowConfidence(0.7);
        Assert.assertEquals(2, matrix.complexity());
    }

    /**
     * Complexity test.
     */
    @Test
    public void complexityTest() {

        addMockEntryToMatrix("brown dog", 0, 0.8);
        Assert.assertEquals(1, matrix.complexity());
        addMockEntryToMatrix("white dog", 0, 0.85);
        Assert.assertEquals(2, matrix.complexity());
        addMockEntryToMatrix("brown dog", 1, 0.81);
        Assert.assertEquals(2, matrix.complexity());
        addMockEntryToMatrix("white dog", 1, 0.87);
        Assert.assertEquals(4, matrix.complexity());
    }

    /**
     * Adds the mock entry to matrix.
     *
     * @param string the string
     * @param id the id
     * @param conf the conf
     */
    private void addMockEntryToMatrix(final String string, final int id, final double conf) {

        TokenSet tokenSet3 = new TokenSet(string);
        Code code3 = CodeFactory.getInstance().getCode(id);
        matrix.add(tokenSet3, new Pair<>(code3, conf));
    }

    /**
     * Adds the mock entry to matrix.
     *
     * @param string the string
     * @param code the code
     * @param conf the conf
     */
    private void addMockEntryToMatrix(final String string, final String code, final double conf) {

        TokenSet tokenSet3 = new TokenSet(string);
        Code code3 = CodeFactory.getInstance().getCode(code);
        matrix.add(tokenSet3, new Pair<>(code3, conf));
    }

}
