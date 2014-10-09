package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.AncestorAble;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.ValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.AbstractClassification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.SubsetEnumerator;

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
                              Code extends AncestorAble<Code>,
                              Classification extends AbstractClassification<Code,Threshold>,
                              P_Comparator extends Comparator<Classification>,
                              FeatureSet,
                              P_ValidityAssessor extends ValidityAssessor<Set<Classification>, FeatureSet>,
                              LossMetric extends Comparable<LossMetric>,
                              P_LossFunction extends LossFunction<Set<Classification>,LossMetric> >{

    private final boolean multipleClassifications;
    private IClassifier<FeatureSet, Classification> classifier;
    private BelowThresholdRemover<Code, Classification,Threshold> bTR;
    private HierarchyResolver<Code, Classification> hR;
    private Flattener<Code, Classification> flattener;
    private MultiValueMapPruner<Code, Classification, P_Comparator> pruner;
    private ValidCombinationGetter<Code, Classification, FeatureSet,P_ValidityAssessor> vCG;
    private LossFunctionApplier<Set<Classification>, LossMetric, P_LossFunction> lFA;
    private SubsetEnumerator<FeatureSet> subsetEnumerator;

    public ResolverPipeline(final IClassifier<FeatureSet, Classification> classifier, final boolean multipleClassifications,final P_Comparator classificationComparator, final  P_ValidityAssessor classificationSetValidityAssessor, final P_LossFunction lengthWeightedLossFunction,final SubsetEnumerator<FeatureSet> subsetEnumerator,final Threshold CONFIDENCE_CHOP_LEVEL){
        this.classifier = classifier;
        this.multipleClassifications = multipleClassifications;
        bTR = new BelowThresholdRemover<>(CONFIDENCE_CHOP_LEVEL);
        hR = new HierarchyResolver<>();
        flattener = new Flattener<>();
        pruner = new MultiValueMapPruner<>(classificationComparator);
        vCG = new ValidCombinationGetter<>(classificationSetValidityAssessor);
        lFA = new LossFunctionApplier<>(lengthWeightedLossFunction);
        this.subsetEnumerator = subsetEnumerator;
    }


    public Set<Classification> classify(final FeatureSet tokenSet) throws Exception {
        MultiValueMap<Code, Classification> multiValueMap = classifySubsets(tokenSet);
        return resolverPipeline(multiValueMap, tokenSet);
    }

    private Set<Classification> resolverPipeline(MultiValueMap<Code, Classification> multiValueMap, final FeatureSet featureSet) throws Exception {
        multiValueMap = bTR.removeBelowThreshold(multiValueMap);
        if(multipleClassifications) {
            multiValueMap = hR.moveAncestorsToDescendantKeys(multiValueMap);
        } else {
            multiValueMap = flattener.moveAllIntoKey(multiValueMap,multiValueMap.iterator().next());
        }
        multiValueMap = pruner.pruneUntilComplexityWithinBound(multiValueMap);
        List<Set<Classification>> validSets = vCG.getValidSets(multiValueMap, featureSet);
        return lFA.getBest(validSets);
    }

    private MultiValueMap<Code, Classification> classifySubsets(final FeatureSet tokenSet) throws IOException {
        MultiValueMap<Code, Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code,List<Classification>>());
        Multiset<FeatureSet> subsets = subsetEnumerator.enumerate(tokenSet);
        for (FeatureSet set : subsets) {
            Classification classification = classifier.classify(set);
            multiValueMap.add(classification.getProperty(), classification);
        }
        return multiValueMap;
    }
}
