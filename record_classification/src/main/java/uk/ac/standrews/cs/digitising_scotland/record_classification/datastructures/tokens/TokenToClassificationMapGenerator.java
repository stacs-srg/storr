package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * Generates a mapping between token sets and classification by
 * Created by fraserdunlop on 08/10/2014 at 16:25.
 */
public class TokenToClassificationMapGenerator {

    /**
     * Generates a map from TokenSets to Classifications.
     * TokenSets which are classified to a single Classification within a record
     * are put into the map with their corresponding classification.
     * @param bucket used to populate map
     * @return a mapping from TokenSets to Classifications
     */
    public Map<TokenSet, Classification> generateMap(final Bucket bucket) {

        Map<TokenSet, Classification> map = new HashMap<>();
        for (Record record : bucket)
            putClassificationsInMap(map, record);
        return map;
    }

    private void putClassificationsInMap(Map<TokenSet, Classification> map, Record record) {

        List<Classification> singles = getClassifications(record);
        putClassificationsInMap(map, singles);
    }

    private void putClassificationsInMap(Map<TokenSet, Classification> map, List<Classification> singles) {

        for (Classification classification : singles)
            map.put(classification.getTokenSet(), classification);
    }

    /**
     * Gets all the classifications in record which correspond to a single description.
     * @param record the record to get unique description classifications from
     * @return list of classifications deriving from unique (in the list) descriptions
     */
    protected List<Classification> getClassifications(final Record record) {

        List<Classification> singles = new ArrayList<>();
        final Set<Classification> set = record.getGoldStandardClassificationSet();
        for (Classification classification : set) {
            int count = countNumClassificationsWithSameTokenSet(set, classification.getTokenSet());
            if (count == 1) singles.add(classification);
        }
        return singles;
    }

    private int countNumClassificationsWithSameTokenSet(Set<Classification> set, TokenSet tokenSet) {

        int count = 0;
        for (Classification classification : set) {
            if (tokenSet.equals(classification.getTokenSet())) count++;
        }
        return count;
    }

}
