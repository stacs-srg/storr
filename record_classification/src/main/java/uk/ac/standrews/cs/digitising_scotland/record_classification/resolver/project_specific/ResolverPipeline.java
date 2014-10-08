package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.CachedClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.*;
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
    private CachedClassifier<TokenSet,Classification> cache;
    private BelowThresholdRemover<Code,Classification,Double> bTR;
    private HierarchyResolver<Code,Classification> hR;
    private Flattener<Code,Classification> flattener;
    private MultiValueMapPruner<Code,Classification,ClassificationComparator> pruner;
    private ValidCombinationGetter<Code,Classification,TokenSet,ClassificationSetValidityAssessor> vCG;
    private LossFunctionApplier<Set<Classification>, Double, ? extends LossFunction<Set<Classification>, Double>> lFA;

    public ResolverPipeline(final CachedClassifier<TokenSet,Classification> cache, final boolean multipleClassifications, final double CONFIDENCE_CHOP_LEVEL){
        this.cache = cache;
        this.multipleClassifications = multipleClassifications;
        bTR = new BelowThresholdRemover<>(CONFIDENCE_CHOP_LEVEL);
        hR = new HierarchyResolver<>();
        flattener = new Flattener<>();
        pruner = new MultiValueMapPruner<>(new ClassificationComparator());
        vCG = new ValidCombinationGetter<>(new ClassificationSetValidityAssessor());
        lFA = new LossFunctionApplier<>(new LengthWeightedLossFunction());
    }


    public Set<Classification> classify(final TokenSet tokenSet) throws Exception {
        MultiValueMap<Code,Classification> multiValueMap = classifyNGramSubstrings(tokenSet);
        return resolverPipeline(multiValueMap, tokenSet);
    }

    private Set<Classification> resolverPipeline(MultiValueMap<Code, Classification> multiValueMap, final TokenSet tokenSet) throws Exception {
        multiValueMap = bTR.removeBelowThreshold(multiValueMap);
        if(multipleClassifications) {
            multiValueMap = hR.moveAncestorsToDescendantKeys(multiValueMap);
        } else {
            multiValueMap = flattener.moveAllIntoKey(multiValueMap,multiValueMap.iterator().next());
        }
        multiValueMap = pruner.pruneUntilComplexityWithinBound(multiValueMap);
        List<Set<Classification>> validSets = vCG.getValidSets(multiValueMap, tokenSet);
        return lFA.getBest(validSets);
    }

    private MultiValueMap<Code,Classification> classifyNGramSubstrings(final TokenSet tokenSet) throws IOException {
        MultiValueMap<Code,Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code,List<Classification>>());
        NGramSubstrings ngs = new NGramSubstrings(tokenSet);
        Multiset<TokenSet> nGramSet = ngs.getGramMultiset();
        for (TokenSet nGram : nGramSet) {
            Classification classification = cache.classify(nGram);
            multiValueMap.add(classification.getCode(), classification);
        }
        return multiValueMap;
    }
}
