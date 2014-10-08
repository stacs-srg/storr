package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

import java.util.*;

/**
 *
 * Created by fraserdunlop on 08/10/2014 at 16:25.
 */
public class TokenClassificationCachePopulator {

    public Map<TokenSet,Classification> prePopulate(final Bucket cachePopulationBucket) {
        Map<TokenSet,Classification> map = new HashMap<>();
        for (Record record : cachePopulationBucket) {
            List<Classification> singles = getClassifications(record);
            for (Classification classification : singles)
                map.put(classification.getTokenSet(),classification);
        }
        return map;
    }


    /**
     * Gets the singly coded triples, that is codeTriples that have only one coding.
     *
     * @param record the record to get single triples from
     * @return the singly coded triples
     */
    protected List<Classification> getClassifications(final Record record) {

        List<Classification> singles = new ArrayList<>();

        final Set<Classification> goldStandardClassificationSet = record.getGoldStandardClassificationSet();
        for (Classification codeTriple1 : goldStandardClassificationSet) {
            int count = 0;
            for (Classification codeTriple2 : goldStandardClassificationSet) {
                if (codeTriple1.getTokenSet().equals(codeTriple2.getTokenSet())) {
                    count++;
                }
            }
            if (count == 1) {
                singles.add(codeTriple1);
            }
        }

        return singles;
    }

}
