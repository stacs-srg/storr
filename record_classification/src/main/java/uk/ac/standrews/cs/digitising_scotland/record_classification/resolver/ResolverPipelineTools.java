package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.multivaluemap.*;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 *
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

    public MultiValueMap<Code, Classification> removeBelowThreshold(MultiValueMap<Code, Classification> map, Double threshold) throws IOException, ClassNotFoundException {
        return bTR.removeBelowThreshold(map, threshold);
    }

    public MultiValueMap<Code, Classification> moveAncestorsToDescendantKeys(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return hR.moveAncestorsToDescendantKeys(map);
    }

    public MultiValueMap<Code, Classification> flattenForSingleClassifications(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return flattener.flatten(map);
    }

    public MultiValueMap<Code, Classification> pruneUntilComplexityWithinBound(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return mVMP.pruneUntilComplexityWithinBound(map);
    }

    public List<Set<Classification>> getValidSets(MultiValueMap<Code, Classification> map, TokenSet tokenSet) throws Exception {
        return vCG.getValidSets(map,tokenSet);
    }
}
