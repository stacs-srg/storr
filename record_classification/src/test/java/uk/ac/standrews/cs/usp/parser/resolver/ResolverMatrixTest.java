package uk.ac.standrews.cs.usp.parser.resolver;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;

import com.google.common.collect.Multiset;

/**
 *
 * Created by fraserdunlop on 10/06/2014 at 15:14.
 */
public class ResolverMatrixTest {

    private ResolverMatrix matrix;

    @Before
    public void setup() {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeFactory.getInstance().loadDictionary(codeFile);
        matrix = new ResolverMatrix();

    }

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
        Multiset<TokenSet> powerSet = ResolverUtils.powerSet(new TokenSet("brown white"));
        List<Set<CodeTriple>> validTriples = matrix.getValidCodeTriples(powerSet);
        Assert.assertEquals(20, validTriples.size());
        addMockEntryToMatrix("blue", 2, 0.83);
        powerSet = ResolverUtils.powerSet(new TokenSet("brown white blue"));
        validTriples = matrix.getValidCodeTriples(powerSet);
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
        Assert.assertEquals(8, matrix.complexity());
    }

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

    private void addMockEntryToMatrix(String string, int id, double conf) {

        TokenSet tokenSet3 = new TokenSet(string);
        Code code3 = CodeFactory.getInstance().getCode(id);
        matrix.add(tokenSet3, new Pair<>(code3, conf));
    }

    private void addMockEntryToMatrix(String string, String code, double conf) {

        TokenSet tokenSet3 = new TokenSet(string);
        Code code3 = CodeFactory.getInstance().getCode(code);
        matrix.add(tokenSet3, new Pair<>(code3, conf));
    }

}
