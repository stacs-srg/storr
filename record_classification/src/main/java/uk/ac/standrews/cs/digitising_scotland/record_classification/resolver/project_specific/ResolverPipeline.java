package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.CachedClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.AncestorAble;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.ValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.Value;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.*;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * TODO refactoring in process - fraser
 *
 * Created by fraserdunlop on 08/10/2014 at 09:50.
 */
public class ResolverPipeline<Threshold,
                              K extends AncestorAble<K>,
                              V extends Value<K,Threshold>,
                              P_Comparator extends Comparator<V>,
                              ValidityCriterion,
                              P_ValidityAssessor extends ValidityAssessor<Set<V>,ValidityCriterion>,
                              LossMetric extends Comparable<LossMetric>,
                              P_LossFunction extends LossFunction<Set<V>,LossMetric>> {

    private final boolean multipleClassifications;
    private CachedClassifier<TokenSet, V> cache;
    private BelowThresholdRemover<K, V,Threshold> bTR;
    private HierarchyResolver<K, V> hR;
    private Flattener<K, V> flattener;
    private MultiValueMapPruner<K, V, P_Comparator> pruner;
    private ValidCombinationGetter<K, V, ValidityCriterion,P_ValidityAssessor> vCG;
    private LossFunctionApplier<Set<V>, LossMetric, P_LossFunction> lFA;

    public ResolverPipeline(final CachedClassifier<TokenSet, V> cache, final boolean multipleClassifications,final P_Comparator classificationComparator, final  P_ValidityAssessor classificationSetValidityAssessor, final P_LossFunction lengthWeightedLossFunction,final Threshold CONFIDENCE_CHOP_LEVEL){
        this.cache = cache;
        this.multipleClassifications = multipleClassifications;
        bTR = new BelowThresholdRemover<>(CONFIDENCE_CHOP_LEVEL);
        hR = new HierarchyResolver<>();
        flattener = new Flattener<>();
        pruner = new MultiValueMapPruner<>(classificationComparator);
        vCG = new ValidCombinationGetter<>(classificationSetValidityAssessor);
        lFA = new LossFunctionApplier<>(lengthWeightedLossFunction);
    }


    //first and 2nd argument are the same thing please ignore - i'm half way through refactoring this class - fraser
    //TODO clean up this mess :P
    public Set<V> classify(final TokenSet tokenSet, ValidityCriterion validityCriterion ) throws Exception {
        MultiValueMap<K, V> multiValueMap = classifyNGramSubstrings(tokenSet);
        return resolverPipeline(multiValueMap, validityCriterion);
    }

    private Set<V> resolverPipeline(MultiValueMap<K, V> multiValueMap, final ValidityCriterion validityCriterion) throws Exception {
        multiValueMap = bTR.removeBelowThreshold(multiValueMap);
        if(multipleClassifications) {
            multiValueMap = hR.moveAncestorsToDescendantKeys(multiValueMap);
        } else {
            multiValueMap = flattener.moveAllIntoKey(multiValueMap,multiValueMap.iterator().next());
        }
        multiValueMap = pruner.pruneUntilComplexityWithinBound(multiValueMap);
        List<Set<V>> validSets = vCG.getValidSets(multiValueMap, validityCriterion);
        return lFA.getBest(validSets);
    }

    private MultiValueMap<K, V> classifyNGramSubstrings(final TokenSet validityCriterion) throws IOException {
        MultiValueMap<K, V> multiValueMap = new MultiValueMap<>(new HashMap<K,List<V>>());
        NGramSubstrings ngs = new NGramSubstrings(validityCriterion);
        Multiset<TokenSet> nGramSet = ngs.getGramMultiset();
        for (TokenSet nGram : nGramSet) {
            V v = cache.classify(nGram);
            multiValueMap.add(v.getProperty(), v);
        }
        return multiValueMap;
    }
}
