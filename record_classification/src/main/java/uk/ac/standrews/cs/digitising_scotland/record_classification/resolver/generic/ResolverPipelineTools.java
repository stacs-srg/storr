package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.*;

import java.io.IOException;
import java.util.*;

/**
 * A suite of tools which can be used to construct classification/resolution pipelines
 * TODO - test! - fraser 8/Oct
 * Created by fraserdunlop on 06/10/2014 at 15:02.
 */
public class ResolverPipelineTools<K extends AncestorAble<K>,
                                   Threshold,
                                   V extends Comparable<Threshold>,
                                   LossMetric extends Comparable<LossMetric>,
                                   LossFunction extends uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction<Set<V>,LossMetric>,
                                   PrunerComparator extends Comparator<V>,
                                   ValidityCriterion,
                                   P_ValidityAssessor extends ValidityAssessor<Set<V>,ValidityCriterion>>
                        implements IBelowThresholdRemover<K, V, Threshold>,
                                   IFlattener<K,V>,
                                   IHierarchyResolver<K,V>{

    private BelowThresholdRemover<K, V, Threshold> bTR;
    private HierarchyResolver<K, V> hR;
    private MultiValueMapPruner<K, V, PrunerComparator> mVMP;
    private ValidCombinationGetter<K, V,ValidityCriterion,P_ValidityAssessor> vCG;
    private Flattener<K, V> flattener;
    private LossFunctionApplier<Set<V>,LossMetric,LossFunction> lFH;

    public ResolverPipelineTools(final LossFunction lossFunction,
                                 final PrunerComparator comparator,
                                 final P_ValidityAssessor validityAssessor,
                                 final Threshold threshold){

        bTR = new BelowThresholdRemover<>(threshold);
        hR = new HierarchyResolver<>();
        mVMP = new MultiValueMapPruner<>(comparator);
        vCG = new ValidCombinationGetter<>(validityAssessor);
        flattener = new Flattener<>();
        lFH = new LossFunctionApplier<>(lossFunction);
    }

    public MultiValueMap<K, V> flattenMultiValueMapIntoFirstKey(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return moveAllIntoKey(map, map.iterator().next());
    }

    public MultiValueMap<K, V> pruneUntilComplexityWithinBound(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return mVMP.pruneUntilComplexityWithinBound(map);
    }

    public List<Set<V>> getValidSets(MultiValueMap<K, V> map, ValidityCriterion tokenSet) throws Exception {
        return vCG.getValidSets(map, tokenSet);
    }

    public Set<V> getBestSetAccordingToLossFunction(Collection<Set<V>> setCollection){
        if(setCollection.isEmpty())
            return new HashSet<>();
        return lFH.getBest(setCollection);
    }

    @Override
    public MultiValueMap<K, V> moveAncestorsToDescendantKeys(final MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return hR.moveAncestorsToDescendantKeys(map);
    }

    @Override
    public MultiValueMap<K, V> removeBelowThreshold(MultiValueMap<K, V> map) throws IOException, ClassNotFoundException {
        return bTR.removeBelowThreshold(map);
    }

    @Override
    public MultiValueMap<K, V> moveAllIntoKey(MultiValueMap<K, V> map, K key) throws IOException, ClassNotFoundException {
        return flattener.moveAllIntoKey(map, key);
    }
}
