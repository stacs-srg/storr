package uk.ac.standrews.cs.usp.parser.pipeline;

import java.io.IOException;
import java.util.Set;

import uk.ac.standrews.cs.usp.parser.datastructures.Bucket;
import uk.ac.standrews.cs.usp.parser.datastructures.Record;
import uk.ac.standrews.cs.usp.parser.resolver.CodeTriple;

public class BucketClassifier {

    RecordClassificationPipeline recordClassifier;

    public BucketClassifier(RecordClassificationPipeline recordClassifier) {

        this.recordClassifier = recordClassifier;
    }

    public Bucket classify(Bucket bucket) throws IOException {

        for (Record record : bucket) {
            Set<CodeTriple> result = recordClassifier.classify(record);
            record.addAllCodeTriples(result);
        }

        return bucket;

    }
}
