/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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

    Map<TokenSet, Classification> map = new HashMap<>();
    private List<TokenSet> blackList;

    public TokenToClassificationMapGenerator(Bucket bucket) {

        blackList = new ArrayList<>();
        for (Record record : bucket) {
            putClassificationsInMap(map, record);
        }
    }

    /**
     * Generates a map from TokenSets to Classifications.
     * TokenSets which are classified to a single Classification within a record
     * are put into the map with their corresponding classification.
     * @return a mapping from TokenSets to Classifications
     */
    public Map<TokenSet, Classification> getMap() {

        return map;
    }

    private void putClassificationsInMap(final Map<TokenSet, Classification> map, final Record record) {

        List<Classification> singles = getClassifications(record);
        putClassificationsInMap(map, singles);
    }

    private void putClassificationsInMap(final Map<TokenSet, Classification> map, final List<Classification> singles) {

        for (Classification classification : singles) {
            // map.put(classification.getTokenSet(), classification);
            addToLookup(map, classification, classification.getTokenSet(), blackList);
        }
    }

    protected void addToLookup(final Map<TokenSet, Classification> lookup, final Classification goldStandardCode, final TokenSet description, final List<TokenSet> blacklist) {

        if (!blacklist.contains(description)) {
            if (!lookup.containsKey(description)) {
                // Make new code witj -1 as confidence so we can tell where classifications came from later.
                // -2 means exact match, -1 means cache classifier
                Classification editClassification = new Classification(goldStandardCode.getCode(), goldStandardCode.getTokenSet(), -1.0);
                lookup.put(description, editClassification);
            }
            else if (!goldStandardCode.equals(lookup.get(description))) {
                blacklist.add(description);
                lookup.remove(description);
            }
        }
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
            if (count == 1) {
                singles.add(classification);
            }
        }
        return singles;
    }

    private int countNumClassificationsWithSameTokenSet(final Set<Classification> set, final TokenSet tokenSet) {

        int count = 0;
        for (Classification classification : set) {
            if (tokenSet.equals(classification.getTokenSet())) {
                count++;
            }
        }
        return count;
    }

}
