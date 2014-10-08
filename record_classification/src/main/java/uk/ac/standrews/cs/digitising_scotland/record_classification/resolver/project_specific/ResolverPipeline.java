package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.Pair;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenClassificationCache;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.MultiValueMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * TODO - refactored here from ClassifierPipeline. There are no Tests!?!? Test! - fraser 8/Oct :P
 * Created by fraserdunlop on 08/10/2014 at 09:50.
 */
public class ResolverPipeline {

    private final boolean multipleClassifications;
    private TokenClassificationCache cache;
    private double CONFIDENCE_CHOP_LEVEL = 0.3;
    private ResolverPipelineTools rPT = new ResolverPipelineTools();

    public ResolverPipeline(final TokenClassificationCache cache, final boolean multipleClassifications, final double CONFIDENCE_CHOP_LEVEL){
        this.cache = cache;
        this.CONFIDENCE_CHOP_LEVEL = CONFIDENCE_CHOP_LEVEL;
        this.multipleClassifications = multipleClassifications;
    }


    public Set<Classification> classify(final TokenSet tokenSet) throws Exception {
        MultiValueMap<Code,Classification> multiValueMap = classifyNGramSubstrings(tokenSet);
        return resolverPipeline(multiValueMap, tokenSet);
    }

    private Set<Classification> resolverPipeline(MultiValueMap<Code, Classification> multiValueMap, final TokenSet tokenSet) throws Exception {
        multiValueMap = rPT.removeBelowThreshold(multiValueMap, CONFIDENCE_CHOP_LEVEL);
        if(multipleClassifications) {
            multiValueMap = rPT.moveAncestorsToDescendantKeys(multiValueMap);
        } else {
            multiValueMap = rPT.flattenForSingleClassifications(multiValueMap);
        }
        multiValueMap = rPT.pruneUntilComplexityWithinBound(multiValueMap);
        List<Set<Classification>> validSets = rPT.getValidSets(multiValueMap, tokenSet);
        return rPT.getBestSetAccordingToLossFunction(validSets);
    }

    private MultiValueMap<Code,Classification> classifyNGramSubstrings(final TokenSet tokenSet) throws IOException {
        MultiValueMap<Code,Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code,List<Classification>>());
        NGramSubstrings ngs = new NGramSubstrings(tokenSet);
        Multiset<TokenSet> nGramSet = ngs.getGramMultiset();
        for (TokenSet nGram : nGramSet) {
            Pair<Code, Double> codeDoublePair = cache.getClassification(nGram);
            multiValueMap.add(codeDoublePair.getLeft(), new Classification(codeDoublePair.getLeft(),nGram,codeDoublePair.getRight()));
        }
        return multiValueMap;
    }
}
