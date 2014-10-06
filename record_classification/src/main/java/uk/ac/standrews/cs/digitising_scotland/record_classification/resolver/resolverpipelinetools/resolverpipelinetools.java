package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.resolverpipelinetools;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.MultiValueMap;

import java.io.IOException;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 15:02.
 */
public class ResolverPipelineTools {

    private BelowThresholdRemover<Code, Classification, Double> bTR = new BelowThresholdRemover<>();
    private HierarchyResolver<Code, Classification> hR = new HierarchyResolver<>();
    private MultiValueMapPruner<Code, Classification, ClassificationComparator> mVMP =
            new MultiValueMapPruner<>(new ClassificationComparator());

    public MultiValueMap<Code, Classification> removeBelowThreshold(MultiValueMap<Code, Classification> map, Double threshold) throws IOException, ClassNotFoundException {
        return bTR.removeBelowThreshold(map, threshold);
    }

    public MultiValueMap<Code, Classification> moveAncestorsToDescendantKeys(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return hR.moveAncestorsToDescendantKeys(map);
    }

    public MultiValueMap<Code, Classification> flattenForSingleClassifications(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return hR.flattenForSingleClassifications(map);
    }

    public MultiValueMap<Code, Classification> pruneUntilComplexityWithinBound(final MultiValueMap<Code, Classification> map) throws IOException, ClassNotFoundException {
        return mVMP.pruneUntilComplexityWithinBound(map);
    }
}
