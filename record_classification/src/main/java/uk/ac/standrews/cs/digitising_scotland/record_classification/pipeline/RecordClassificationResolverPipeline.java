package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.lookup.NGramSubstrings;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.ClassificationComparator;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.ClassificationSetValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.generic.ResolverPipeline;

import java.util.Set;

/**
 *
 * Created by fraserdunlop on 09/10/2014 at 12:14.
 */
public class RecordClassificationResolverPipeline<P_LossFunction extends LossFunction<Set<Classification>,Double>>
               extends ResolverPipeline<Double,
                                        Code,
                                        Classification,
                                        ClassificationComparator,
                                        TokenSet,
                                        ClassificationSetValidityAssessor,
                                        Double,
                                        P_LossFunction>{

    public RecordClassificationResolverPipeline(final IClassifier<TokenSet, Classification> classifier,
                                                final P_LossFunction lengthWeightedLossFunction,
                                                final Double confidenceThreshold,
                                                final boolean multipleClassifications,
                                                final boolean resolveHierarchies) {
        super(classifier,
              multipleClassifications,
              new ClassificationComparator(),
              new ClassificationSetValidityAssessor(),
              lengthWeightedLossFunction,
              new NGramSubstrings(),
              confidenceThreshold,
              resolveHierarchies);
    }
}
