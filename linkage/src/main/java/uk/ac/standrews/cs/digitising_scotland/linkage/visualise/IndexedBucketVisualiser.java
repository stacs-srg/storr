package uk.ac.standrews.cs.digitising_scotland.linkage.visualise;

import uk.ac.standrews.cs.digitising_scotland.generic_linkage.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.labels.SameAsLabels;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Set;

/**
 * Created by al on 06/06/2014.
 */
public class IndexedBucketVisualiser {

    private final IIndexedBucket indexed;
    private final IBucket people;

    public IndexedBucketVisualiser(IIndexedBucket bucket, IBucket people) {
        this.indexed = bucket;
        this.people = people;
    }

    public void show() throws IOException, PersistentObjectException {

        IBucketIndex index = indexed.getIndex(SameAsLabels.first);
        Set<String> keys = index.keySet();

        for (String key : keys) {
            System.out.println("key = " + key);
            ILXPInputStream stream = index.records(key);

            boolean first = true;

            for (ILXP next : stream) { // indexed by SameAsLabels.first

                if (first) {
                    String first_id_string = next.get(SameAsLabels.first); // id of second person in person table
                    ILXP person1 = people.get(Integer.parseInt(first_id_string));
                    if (person1 != null) {
                        System.out.println("\toriginal: " + person1.toString());
                    }
                }

                String relation = next.get(SameAsLabels.relationship);
                String second_id_string = next.get(SameAsLabels.second); // id of second person in person table
                ILXP person2 = people.get(Integer.parseInt(second_id_string));
                if (relation != null && person2 != null) {
                    System.out.println("\t" + " * " + relation + ": " + person2.toString());
                }
                first = false;
            }
        }
    }
}
