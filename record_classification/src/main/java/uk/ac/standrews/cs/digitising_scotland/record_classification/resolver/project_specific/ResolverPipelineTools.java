package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.ValidCombinationGetter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A suite of tools which can be used to construct classification/resolution pipelines
 * TODO - test! - fraser 8/Oct
 * TODO genericise!
 * Created by fraserdunlop on 06/10/2014 at 15:02.
 */
public class ResolverPipelineTools<LossFunction extends AbstractLossFunction<Set<Classification>,Double>> {

    private BelowThresholdRemover<Code, Classification, Double> bTR;
    private HierarchyResolver<Code, Classification> hR;
    private MultiValueMapPruner<Code, Classification, ClassificationComparator> mVMP;
    private ValidCombinationGetter<Code,Classification,TokenSet,ClassificationSetValidityAssessor> vCG;
    private Flattener<Code,Classification> flattener;
    private LossFunctionHolder<Set<Classification>,Double,LossFunction> lFH;

    public ResolverPipelineTools(final LossFunction lossFunction){

        bTR = new BelowThresholdRemover<>();
        hR = new HierarchyResolver<>();
        mVMP = new MultiValueMapPruner<>(new ClassificationComparator());
        vCG = new ValidCombinationGetter<>(new ClassificationSetValidityAssessor());
        flattener = new Flattener<>();
        lFH = new LossFunctionHolder<>(lossFunction);
    }

    public MultiValueMap<Code, Classification> removeBelowThreshold(MultiValueMap<Code, Classification> map, Double threshold) throws IOException, ClassNotFoundException {
        return bTR.removeBelowThreshold(map, threshold);
    }

    public MultiValueMap<Code, Classification> moveAncestorsToDescendantKeys(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return hR.moveAncestorsToDescendantKeys(map);
    }

    public MultiValueMap<Code, Classification> flattenForSingleClassifications(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return flattener.moveAllIntoKey(map, map.iterator().next());
    }

    public MultiValueMap<Code, Classification> pruneUntilComplexityWithinBound(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return mVMP.pruneUntilComplexityWithinBound(map);
    }

    public List<Set<Classification>> getValidSets(MultiValueMap<Code, Classification> map, TokenSet tokenSet) throws Exception {
        return vCG.getValidSets(map,tokenSet);
    }

    public Set<Classification> getBestSetAccordingToLossFunction(Collection<Set<Classification>> classifications){
        if(classifications.isEmpty())
            return new HashSet<>();
        return lFH.getBest(classifications);
    }
}
