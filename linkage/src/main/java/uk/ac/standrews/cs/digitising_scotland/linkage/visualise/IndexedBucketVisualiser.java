package uk.ac.standrews.cs.digitising_scotland.linkage.visualise;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsTypeLabel;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Set;

/**
 * Created by al on 06/06/2014.
 */
public class IndexedBucketVisualiser {

    private final IIndexedBucketTypedOLD indexed;
    private final IBucketTypedOLD people;

    public IndexedBucketVisualiser(IIndexedBucketTypedOLD bucket, IBucketTypedOLD people) {
        this.indexed = bucket;
        this.people = people;
    }

    public void show() throws IOException, PersistentObjectException {

        IBucketIndexOLD index = indexed.getIndex(SameAsTypeLabel.first);
        Set<String> keys = index.keySet();

        for (String key : keys) {
            System.out.println("key = " + key);
            ILXPInputStreamTypedOld<ILXP> stream = index.records(key);

            boolean first = true;

            for (ILXP next : stream) { // indexed by SameAsLabels.first

                if (first) {
                    String first_id_string = next.get(SameAsTypeLabel.first); // id of second person in person table
                    ILXP person1 = people.get(Integer.parseInt(first_id_string));
                    if (person1 != null) {
                        System.out.println("\toriginal: " + person1.toString());
                    }
                }

                String relation = next.get(SameAsTypeLabel.relationship);
                String second_id_string = next.get(SameAsTypeLabel.second); // id of second person in person table
                ILXP person2 = people.get(Integer.parseInt(second_id_string));
                if (relation != null && person2 != null) {
                    System.out.println("\t" + " * " + relation + ": " + person2.toString());
                }
                first = false;
            }
        }
    }
}
