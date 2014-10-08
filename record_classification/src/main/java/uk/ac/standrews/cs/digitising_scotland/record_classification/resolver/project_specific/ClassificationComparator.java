package uk.ac.standrews.cs.digitising_scotland.record_classification.resolver.project_specific;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Classification comparator. Compares classifications
 * Created by fraserdunlop on 06/10/2014 at 14:15.
 */
public class ClassificationComparator implements Comparator<Classification>, Serializable {

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