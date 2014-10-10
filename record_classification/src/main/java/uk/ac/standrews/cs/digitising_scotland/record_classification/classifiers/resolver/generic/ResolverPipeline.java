package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.generic;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.AbstractClassification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.AncestorAble;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.SubsetEnumerator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.ValidityAssessor;

import com.google.common.collect.Multiset;

/**
 * Resolver Pipeline Classifier. Splits FeatureSet into subsets, classifies the subsets,
 * puts the classifications in a MultiValueMap with Codes as keys, MultiValueMap then
 * gets handed through the pipeline which removes Classifications below a specified
 * Threshold, resolves ancestral hierarchies (as they are assumed to be mutually exclusive),
 * if the multipleClassifications flag is set to false then the MultiValueMap is flattened
 * rather than hierarchies being resolved, prunes the map until its complexity is within a specified bound,
 * finds the valid combinations of Classifications from the map (this is expensive for high
 * complexity MultiValueMaps hence the bound) and finally returns the combination of codes which
 * maximises the loss function.
 * Created by fraserdunlop on 08/10/2014 at 09:50.
 */
public class ResolverPipeline<Threshold, Code extends AncestorAble<Code>, Classification extends AbstractClassification<Code, Threshold>, P_Comparator extends Comparator<Classification>, FeatureSet, P_ValidityAssessor extends ValidityAssessor<Set<Classification>, FeatureSet>, LossMetric extends Comparable<LossMetric>, P_LossFunction extends LossFunction<Set<Classification>, LossMetric>>
                implements IClassifier<FeatureSet, Set<Classification>> {

    private final boolean multipleClassifications;
    private final boolean resolveHierarchies;
    private IClassifier<FeatureSet, Classification> classifier;
    private BelowThresholdRemover<Code, Classification, Threshold> bTR;
    private HierarchyResolver<Code, Classification> hR;
    private Flattener<Code, Classification> flattener;
    private MultiValueMapPruner<Code, Classification, P_Comparator> pruner;
    private ValidCombinationGetter<Code, Classification, FeatureSet, P_ValidityAssessor> vCG;
    private LossFunctionApplier<Classification, LossMetric, P_LossFunction> lFA;
    private SubsetEnumerator<FeatureSet> subsetEnumerator;

    public ResolverPipeline(final IClassifier<FeatureSet, Classification> classifier, final boolean multipleClassifications, final P_Comparator classificationComparator, final P_ValidityAssessor classificationSetValidityAssessor, final P_LossFunction lengthWeightedLossFunction,
                    final SubsetEnumerator<FeatureSet> subsetEnumerator, final Threshold threshold, final boolean resolveHierarchies) {

        this.classifier = classifier;
        this.multipleClassifications = multipleClassifications;
        this.resolveHierarchies = resolveHierarchies;
        bTR = new BelowThresholdRemover<>(threshold);
        hR = new HierarchyResolver<>();
        flattener = new Flattener<>();
        pruner = new MultiValueMapPruner<>(classificationComparator);
        vCG = new ValidCombinationGetter<>(classificationSetValidityAssessor);
        lFA = new LossFunctionApplier<>(lengthWeightedLossFunction);
        this.subsetEnumerator = subsetEnumerator;
    }

    /**
     * The IClassifier interface, classifies FeatureSets to sets of Classifications.
     * @param featureSet set of features to classify
     * @return set of classifications
     * @throws Exception
     */
    @Override
    public Set<Classification> classify(final FeatureSet featureSet) throws Exception {

        MultiValueMap<Code, Classification> multiValueMap = classifySubsets(featureSet);
        return resolverPipeline(multiValueMap, featureSet);
    }

    private Set<Classification> resolverPipeline(MultiValueMap<Code, Classification> multiValueMap, final FeatureSet featureSet) throws Exception {

        multiValueMap = bTR.removeBelowThreshold(multiValueMap);
        if (multipleClassifications && resolveHierarchies) {
            multiValueMap = hR.moveAncestorsToDescendantKeys(multiValueMap);
        }
        else {
            multiValueMap = flattener.moveAllIntoKey(multiValueMap, multiValueMap.iterator().next());
        }
        multiValueMap = pruner.pruneUntilComplexityWithinBound(multiValueMap);
        List<Set<Classification>> validSets = vCG.getValidSets(multiValueMap, featureSet);
        return lFA.getBest(validSets);
    }

    private MultiValueMap<Code, Classification> classifySubsets(final FeatureSet tokenSet) throws Exception {

        MultiValueMap<Code, Classification> multiValueMap = new MultiValueMap<>(new HashMap<Code, List<Classification>>());
        Multiset<FeatureSet> subsets = subsetEnumerator.enumerate(tokenSet);
        for (FeatureSet set : subsets) {
            Classification classification = classifier.classify(set);
            multiValueMap.add(classification.getProperty(), classification);
        }
        return multiValueMap;
    }
}
