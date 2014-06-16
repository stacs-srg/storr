package uk.ac.standrews.cs.usp.parser.resolver;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.standrews.cs.usp.parser.classifiers.AbstractClassifier;
import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.datastructures.TokenSet;
import uk.ac.standrews.cs.usp.parser.datastructures.code.Code;
import uk.ac.standrews.cs.usp.parser.datastructures.code.CodeFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static uk.ac.standrews.cs.usp.parser.resolver.ResolverUtils.*;

/**
 *
 * Created by fraserdunlop on 10/06/2014 at 14:09.
 */
public class ResolverMkIITest {


    @Test
    public void isValidTest(){
        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        tokenSetSet.add(new TokenSet("brown dog"));
        tokenSetSet.add(new TokenSet("white cat"));
        tokenSetSet.add(new TokenSet("blue parrot"));
        Multiset<TokenSet> mockPowerSet = HashMultiset.create();
        Assert.assertFalse(ResolverUtils.isValid(tokenSetSet, mockPowerSet));
        mockPowerSet.add(new TokenSet("brown dog white cat parrot blue"));
        Assert.assertTrue(ResolverUtils.isValid(tokenSetSet, mockPowerSet));
    }

    @Test
    public void getUnionTest(){
        Multiset<TokenSet> tokenSetSet = HashMultiset.create();
        tokenSetSet.add(new TokenSet("brown dog"));
        tokenSetSet.add(new TokenSet("white cat"));
        tokenSetSet.add(new TokenSet("blue parrot"));
        TokenSet union = ResolverUtils.getUnion(tokenSetSet);
        Assert.assertEquals(6,union.size());
        tokenSetSet.add(new TokenSet("brown dog white cat blue parrot"));
        union = ResolverUtils.getUnion(tokenSetSet);
        Assert.assertEquals(12,union.size());
    }

    @Test
    public void removeAncestorsTest(){

        Set<Code> codes = new HashSet<>();
        codes.add(CodeFactory.getInstance().getCode("95120"));
        codes.add(CodeFactory.getInstance().getCode("9500"));
        codes.add(CodeFactory.getInstance().getCode("95240"));
        codes.add(CodeFactory.getInstance().getCode("952"));

        Set<Code> ancestorsRemoved = ResolverUtils.removeAncestors(codes);

        Assert.assertEquals(3, ancestorsRemoved.size());

        Assert.assertTrue(codes.contains(CodeFactory.getInstance().getCode("952")));
        Assert.assertFalse(ancestorsRemoved.contains(CodeFactory.getInstance().getCode("952")));
    }

    @Test
    public void powerSetTest(){
        TokenSet tokenSet = new TokenSet("the brown dog jumped");
        Multiset<TokenSet> powerSet = powerSet(tokenSet);

        Assert.assertEquals(16,powerSet.size());

        Set<TokenSet> unique = new HashSet<>();
        unique.addAll(powerSet);

        Assert.assertEquals(16,unique.size());
    }

    @Test
    public void powerSetMultiTest(){
        TokenSet tokenSet = new TokenSet("the brown dog dog");
        Multiset<TokenSet> powerSet = powerSet(tokenSet);

        Assert.assertEquals(16,powerSet.size());

        Set<TokenSet> unique = new HashSet<>();
        unique.addAll(powerSet);

        Assert.assertEquals(12,unique.size());
    }

    @Test
    public void classificationCache() throws IOException {
        TokenClassificationCache cache = new TokenClassificationCache(new mockClassifier());
        TokenSet tokenSet0 = new TokenSet("brown dog");
        TokenSet tokenSet1 = new TokenSet("jumped");

        Code code = CodeFactory.getInstance().getCode(0);
        Double d = 0.5;
        Pair<Code,Double> codeDoublePair =  new Pair<>(code,d);

        Assert.assertEquals(codeDoublePair,cache.getClassification(tokenSet0));
        Assert.assertEquals(codeDoublePair,cache.getClassification(tokenSet1));


    }

    class mockClassifier extends AbstractClassifier{

        @Override
        public void train(Bucket bucket) throws Exception {

        }

        @Override
        public Record classify(Record record) throws IOException {
            return null;
        }

        @Override
        public Pair<Code, Double> classify(TokenSet string) throws IOException {
            Code code = CodeFactory.getInstance().getCode(0);
            Double d = 0.5;
            return new Pair<>(code,d);
        }

        @Override
        public void getModelFromDefaultLocation() {

        }
    }
}
