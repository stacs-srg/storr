package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.tools.DeepCloner;

import java.io.IOException;
import java.util.*;

/**
 *
 * Created by fraserdunlop on 06/10/2014 at 10:00.
 */
public class HierarchyResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyResolver.class);
    DeepCloner deepCloner = new DeepCloner();

    /**
     * Resolves hierarchies in the matrix by removing the ancestors of {@link uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code}s in the matrix.
     * This utilises the ResolverUtils.removeAncestors() method to achieve this.
     */
    protected MultiValueMap<Code,Classification> resolveHierarchies(final MultiValueMap<Code,Classification> matrix, final boolean multipleClassifications) throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> clone = deepCloner.deepClone(matrix);
        if (multipleClassifications)
            return resolveForMultipleClassifications(clone);
        else
            return flattenForSingleClassifications(clone);
    }

    private MultiValueMap<Code,Classification> flattenForSingleClassifications(final MultiValueMap<Code,Classification> matrix) {

        Map<Code, List<Classification>> singleColumnMatrix = new HashMap<>();
        // pick any code here as it's not used in single classifications.
        Code c = matrix.iterator().next();
        singleColumnMatrix.put(c, new ArrayList<Classification>());
        for (Code code : matrix) {
            singleColumnMatrix.get(c).addAll(matrix.get(code));
        }
        return new MultiValueMap<>(singleColumnMatrix);
    }

    private MultiValueMap<Code,Classification> resolveForMultipleClassifications(final MultiValueMap<Code,Classification> matrix) throws IOException, ClassNotFoundException {
        MultiValueMap<Code,Classification> clone = deepCloner.deepClone(matrix);
        for (Code code : matrix) {
            Code ancestor = ResolverUtils.whichCodeIsAncestorOfCodeInCollection(code, clone.keySet());
            if (ancestor != null) {
                clone.get(code).addAll(matrix.get(ancestor));
                clone.remove(ancestor);
            }
        }
        return clone;
    }
}
