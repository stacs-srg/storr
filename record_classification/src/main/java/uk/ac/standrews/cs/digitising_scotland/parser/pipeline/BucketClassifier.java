package uk.ac.standrews.cs.digitising_scotland.parser.pipeline;

import java.io.IOException;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Bucket;
import uk.ac.standrews.cs.digitising_scotland.parser.datastructures.Record;
import uk.ac.standrews.cs.digitising_scotland.parser.resolver.CodeTriple;

public class BucketClassifier {

    RecordClassificationPipeline recordClassifier;

    public BucketClassifier(final RecordClassificationPipeline recordClassifier) {

        this.recordClassifier = recordClassifier;
    }

    public Bucket classify(final Bucket bucket) throws IOException {

        for (Record record : bucket) {
            Set<CodeTriple> result = recordClassifier.classify(record);
            record.addAllCodeTriples(result);
        }

        return bucket;

    }
}
