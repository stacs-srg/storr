package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import static uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.ResolverUtils.powerSet;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenClassificationCache;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 *
 * Created by fraserdunlop on 10/06/2014 at 14:09.
 */
public class ResolverMkIITest {

    @Before
    public void setUp() {

        File codeFile = new File(getClass().getResource("/CodeFactoryTestFile.txt").getFile());
        CodeIndexer.getInstance().loadDictionary(codeFile);
    }

    /**
     * Checks if is valid test.
     */
    @Test
    public void isValidTest() {

        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        tokenSetSet.add(new TokenSet("brown dog"));
        tokenSetSet.add(new TokenSet("white cat"));
        tokenSetSet.add(new TokenSet("blue parrot"));
        Multiset<TokenSet> mockPowerSet = HashMultiset.create();
        Assert.assertFalse(ResolverUtils.isValid(tokenSetSet, mockPowerSet));
        mockPowerSet.add(new TokenSet("brown dog white cat parrot blue"));
        Assert.assertTrue(ResolverUtils.isValid(tokenSetSet, mockPowerSet));
    }

    /**
     * Gets the union test.
     *

     */
    @Test
    public void getUnionTest() {

        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        tokenSetSet.add(new TokenSet("brown dog"));
        tokenSetSet.add(new TokenSet("white cat"));
        tokenSetSet.add(new TokenSet("blue parrot"));
        TokenSet union = ResolverUtils.getUnion(tokenSetSet);
        Assert.assertEquals(6, union.size());
        tokenSetSet.add(new TokenSet("brown dog white cat blue parrot"));
        union = ResolverUtils.getUnion(tokenSetSet);
        Assert.assertEquals(12, union.size());
    }

    /**
     * Removes the ancestors test.
     */
    @Test
    public void removeAncestorsTest() {

        Set<Code> codes = new HashSet<>();
        codes.add(CodeIndexer.getInstance().getCode("95120"));
        codes.add(CodeIndexer.getInstance().getCode("9500"));
        codes.add(CodeIndexer.getInstance().getCode("95240"));
        codes.add(CodeIndexer.getInstance().getCode("952"));

        Set<Code> ancestorsRemoved = ResolverUtils.removeAncestors(codes);

        Assert.assertEquals(3, ancestorsRemoved.size());

        Assert.assertTrue(codes.contains(CodeIndexer.getInstance().getCode("952")));
        Assert.assertFalse(ancestorsRemoved.contains(CodeIndexer.getInstance().getCode("952")));
    }

    /**
     * Power set test.
     */
    @Test
    public void powerSetTest() {

        TokenSet tokenSet = new TokenSet("the brown dog jumped");
        Multiset<TokenSet> powerSet = powerSet(tokenSet);

        Assert.assertEquals(16, powerSet.size());

        Set<TokenSet> unique = new HashSet<>();
        unique.addAll(powerSet);

        Assert.assertEquals(16, unique.size());
    }

    /**
     * Power set multi test.
     */
    @Test
    public void powerSetMultiTest() {

        TokenSet tokenSet = new TokenSet("the brown dog dog");
        Multiset<TokenSet> powerSet = powerSet(tokenSet);

        Assert.assertEquals(16, powerSet.size());

        Set<TokenSet> unique = new HashSet<>();
        unique.addAll(powerSet);

        Assert.assertEquals(12, unique.size());
    }

    /**
     * Classification cache.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Test
    public void classificationCache() throws IOException {

        TokenClassificationCache cache = new TokenClassificationCache(new mockClassifier());
        TokenSet tokenSet0 = new TokenSet("brown dog");
        TokenSet tokenSet1 = new TokenSet("jumped");

        Code code = CodeIndexer.getInstance().getCode(0);
        Double d = 0.5;
        Pair<Code, Double> codeDoublePair = new Pair<>(code, d);

        Assert.assertEquals(codeDoublePair, cache.getClassification(tokenSet0));
        Assert.assertEquals(codeDoublePair, cache.getClassification(tokenSet1));

    }

    /**
     * The Class mockClassifier.
     */
    static class mockClassifier extends AbstractClassifier {

        /* (non-Javadoc)
         * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#train(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Bucket)
         */
        @Override
        public void train(final Bucket bucket) throws Exception {

        }

        /* (non-Javadoc)
         * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Record)
         */
        @Override
        public Record classify(final Record record) throws IOException {

            return null;
        }

        /* (non-Javadoc)
         * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#classify(uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.TokenSet)
         */
        @Override
        public Pair<Code, Double> classify(final TokenSet string) throws IOException {

            Code code = CodeIndexer.getInstance().getCode(0);
            Double d = 0.5;
            return new Pair<>(code, d);
        }

        /* (non-Javadoc)
         * @see uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.AbstractClassifier#getModelFromDefaultLocation()
         */
        @Override
        public AbstractClassifier getModelFromDefaultLocation() {

            return this;
        }
    }
}
