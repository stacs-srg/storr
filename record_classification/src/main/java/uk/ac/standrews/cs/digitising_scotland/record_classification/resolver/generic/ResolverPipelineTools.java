package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import java.io.IOException;
import java.util.*;

/**
 * A suite of tools which can be used to construct classification/resolution pipelines
 * TODO - test! - fraser 8/Oct
 * TODO genericise!
 * Created by fraserdunlop on 06/10/2014 at 15:02.
 */
public class ResolverPipelineTools<K extends AncestorAble<K>,
                                   V extends Comparable<LossMetric>,
                                   LossMetric extends Comparable<LossMetric>,
                                   LossFunction extends AbstractLossFunction<Set<V>,LossMetric>,
                                   PrunerComparator extends Comparator<V>,
                                   ValidityCriterion,
                                   P_ValidityAssessor extends ValidityAssessor<Set<V>,ValidityCriterion>> {

    private BelowThresholdRemover<K, V, LossMetric> bTR;
    private HierarchyResolver<K, V> hR;
    private MultiValueMapPruner<K, V, PrunerComparator> mVMP;
    private ValidCombinationGetter<K, V,ValidityCriterion,P_ValidityAssessor> vCG;
    private Flattener<K, V> flattener;
    private LossFunctionHolder<Set<V>,LossMetric,LossFunction> lFH;

    public ResolverPipelineTools(final LossFunction lossFunction, final PrunerComparator comparator, final P_ValidityAssessor validityAssessor){

        bTR = new BelowThresholdRemover<>();
        hR = new HierarchyResolver<>();
        mVMP = new MultiValueMapPruner<>(comparator);
        vCG = new ValidCombinationGetter<>(validityAssessor);
        flattener = new Flattener<>();
        lFH = new LossFunctionHolder<>(lossFunction);
    }

    public MultiValueMap<K, V> removeBelowThreshold(MultiValueMap<K, V> map, LossMetric threshold) throws IOException, ClassNotFoundException {
        return bTR.removeBelowThreshold(map, threshold);
    }

    public MultiValueMap<K, V> moveAncestorsToDescendantKeys(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return hR.moveAncestorsToDescendantKeys(map);
    }

    public MultiValueMap<K, V> flattenMultiValueMapIntoFirstKey(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return flattener.moveAllIntoKey(map, map.iterator().next());
    }

    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return mVMP.pruneUntilComplexityWithinBound(map);
    }

    public List<Set<V>> getValidSets(MultiValueMap<K, V> map, ValidityCriterion tokenSet) throws Exception {
        return vCG.getValidSets(map,tokenSet);
    }

    public Set<V> getBestSetAccordingToLossFunction(Collection<Set<V>> setCollection){
        if(setCollection.isEmpty())
            return new HashSet<>();
        return lFH.getBest(setCollection);
    }
}
