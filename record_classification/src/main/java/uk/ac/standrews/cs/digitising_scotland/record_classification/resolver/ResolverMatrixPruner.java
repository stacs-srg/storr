package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 10:38.
 */
public class ResolverMatrixPruner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResolverMatrixPruner.class);
    private static final int LOWER_BOUND = 1;
    private static final int COMPLEXITY_UPPERLIMIT = 2000;

    /**
     * Chop until complexity within bound.
     *
     */
    public MultiValueMap pruneUntilComplexityWithinBound(final MultiValueMap<Code,Classification> matrix) throws IOException, ClassNotFoundException {

        MultiValueMap<Code,Classification> clone = matrix.deepClone();
        int maxNoOfEachCode = (int) Math.pow(COMPLEXITY_UPPERLIMIT, 1.0 / (double) matrix.size());
        maxNoOfEachCode = Math.max(LOWER_BOUND, maxNoOfEachCode);
        for (Code code : matrix) {
            Collections.sort(clone.get(code), new CodeTripleComparator());
            clone.put(code, matrix.get(code).subList(0, Math.min(matrix.get(code).size(), maxNoOfEachCode)));
        }
        return clone;
    }

    /**
     * Removes from the matrix any Pair<Code, Double> where the confidence value is lower than the confidence specified.
     *
     * @param confidence the confidence threshold
     */
    public MultiValueMap<Code,Classification> chopBelowConfidence(final MultiValueMap<Code,Classification> matrix, final Double confidence) throws IOException, ClassNotFoundException {
        //TODO refactor to use comparator and then refactor similarities between methods
        MultiValueMap<Code,Classification> clone = matrix.deepClone();
        for (Code code : clone) {
            List<Classification> oldList = clone.get(code);
            List<Classification> newList = new ArrayList<>();
            for (Classification classification : oldList) {
                if (classification.getConfidence() >= confidence) {
                    newList.add(classification);
                }
            }
            clone.put(code, newList);
        }
        return clone;
    }

    private static class CodeTripleComparator implements Comparator<Classification>, Serializable {

        private static final long serialVersionUID = -2746182512036694544L;

        @Override
        public int compare(final Classification o1, final Classification o2) {

            double measure1 = o1.getTokenSet().size() * o1.getConfidence();
            double measure2 = o2.getTokenSet().size() * o2.getConfidence();
            if (measure1 < measure2) {
                return 1;
            }
            else if (measure1 > measure2) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }
}
