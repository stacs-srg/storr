package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.tools.analysis.UniqueWordCounter;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Reads a {@link Bucket} and performs data cleaning such as spelling correction and feature selection on the descriptions in each {@link Record}.
 * OriginalData.description is not changed, instead the cleanedDescripion field is populated.
 * @author jkc25, frjd2
 *
 */
public abstract class DataCleaning {

    private Multiset<String> wordMultiset;

    /**
     * Currently a dummy method.
     * Copies original string to cleaned string.
     * @param bucketToClean bucket to perform cleaning on.
     * @return the bucket with cleaned records attached.
     */
    public static Bucket cleanData(final Bucket bucketToClean) {

        Bucket cleanedBucket = bucketToClean;
        //TODO dummy method, copies original data to cleaned data.
        for (Record record : cleanedBucket) {
            record.setCleanedDescription(record.getOriginalData().getDescription());
        }

        return cleanedBucket;
    }

    private void buildTokenOccurenceMap(Bucket bucket) {

        wordMultiset = HashMultiset.create();
        String line;

        for (Record r : bucket) {
            Set<CodeTriple> set = r.getGoldStandardClassificationSet();
            for (CodeTriple codeTriple : set) {
                line = codeTriple.getTokenSet().toString();
                UniqueWordCounter.countWordsInLine(wordMultiset, line);
            }
        }

    }
}
