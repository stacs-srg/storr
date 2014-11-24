package uk.ac.standrews.cs.digitising_scotland.linkage.visualise;


import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.*;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.Person;
import uk.ac.standrews.cs.digitising_scotland.linkage.lxp_records.SameAs;
import uk.ac.standrews.cs.digitising_scotland.linkage.stream_operators.sharder.Pair;
import uk.ac.standrews.cs.nds.persistence.PersistentObjectException;

import java.io.IOException;
import java.util.Set;

/**
 * Created by al on 06/06/2014.
 */
public class IndexedBucketVisualiser {

    private final IIndexedBucket indexed;
    private final IBucket people;

    public IndexedBucketVisualiser(IIndexedBucket bucket, IBucket<Person> people) {
        this.indexed = bucket;
        this.people = people;
    }

    public void show() throws IOException, PersistentObjectException {

        IBucketIndex index = indexed.getIndex(SameAs.FIRST);
        Set<String> keys = index.keySet();

        for (String key : keys) {
            System.out.println("key = " + key);
            IInputStream<Pair<Person>> stream = index.records(key);

            boolean first = true;

            for (Pair<Person> next : stream) { // indexed by SameAsLabels.first

                if (first) {
                    String first_id_string = next.get(SameAs.FIRST); // id of second person in person table
                    ILXP person1 = people.get(Integer.parseInt(first_id_string));
                    if (person1 != null) {
                        System.out.println("\toriginal: " + person1.toString());
                    }
                }

                String relation = next.get(SameAs.RELATIONSHIP);
                String second_id_string = next.get(SameAs.SECOND); // id of second person in person table
                ILXP person2 = people.get(Integer.parseInt(second_id_string));
                if (relation != null && person2 != null) {
                    System.out.println("\t" + " * " + relation + ": " + person2.toString());
                }
                first = false;
            }
        }
    }
}
