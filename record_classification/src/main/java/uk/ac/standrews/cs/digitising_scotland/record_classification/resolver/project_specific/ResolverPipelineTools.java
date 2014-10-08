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
 * Created by fraserdunlop on 06/10/2014 at 15:02.
 */
public class ResolverPipelineTools {

    private BelowThresholdRemover<Code, Classification, Double> bTR = new BelowThresholdRemover<>();
    private HierarchyResolver<Code, Classification> hR = new HierarchyResolver<>();
    private MultiValueMapPruner<Code, Classification, ClassificationComparator> mVMP =
            new MultiValueMapPruner<>(new ClassificationComparator());
    private ValidCombinationGetter<Code,Classification,TokenSet,ClassificationSetValidityAssessor> vCG =
            new ValidCombinationGetter<>(new ClassificationSetValidityAssessor());
    private Flattener<Code,Classification> flattener = new Flattener<>();
    private LossFunctionHolder<Set<Classification>,Double,LengthWeightedLossFunction> lFH =
            new LossFunctionHolder<>(new LengthWeightedLossFunction());

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
