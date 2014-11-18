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
package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Multiset;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.cachedclassifier.CachedClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.RecordClassificationResolverPipeline;
import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.LossFunction;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenToClassificationMapGenerator;

/**
 * This class is produces a set of {@link Classification}s that represent the
 * classification for a {@link Record}.
 * 
 * @author jkc25, frjd2
 * 
 */
public class ClassifierPipeline implements IPipeline {

    /** The Constant CONFIDENCE_CHOP_LEVEL. */
    private static final double CONFIDENCE_CHOP_LEVEL = 0.3;
    private final RecordClassificationResolverPipeline<? extends LossFunction<Multiset<Classification>, Double>> resolverPipeline;

    /** The record cache. */
    private Map<String, Set<Classification>> recordCache;

    private Bucket successfullyClassified;
    private Bucket forFurtherProcessing;

    /**
     * Constructs a new {@link ClassifierPipeline} with the specified
     * {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used to perform the classification duties.
     *
     * @param classifier    {@link uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier} used for machine learning classification
     * @param cachePopulationBucket the training bucket
     */
    public ClassifierPipeline(final IClassifier<TokenSet, Classification> classifier, final Bucket cachePopulationBucket, final LossFunction<Multiset<Classification>, Double> lossFunction, final boolean multipleClassifications, final boolean resolveHierarchies) {

        /* The cache. */
        TokenToClassificationMapGenerator populator = new TokenToClassificationMapGenerator(cachePopulationBucket);
        recordCache = new HashMap<>();
        CachedClassifier<TokenSet, Classification> cache = new CachedClassifier<>(classifier, populator.getMap());

        this.resolverPipeline = new RecordClassificationResolverPipeline<>(cache, lossFunction, CONFIDENCE_CHOP_LEVEL, multipleClassifications, resolveHierarchies);
        this.successfullyClassified = new Bucket();
        this.forFurtherProcessing = new Bucket();
    }

    @Override
    public Bucket getSuccessfullyClassified() {

        return successfullyClassified;
    }

    /**
     * Classify all records in a bucket.
     *
     * @param bucket the bucket to classifiy
     * @return bucket this is the bucket of records for further processing
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public Bucket classify(final Bucket bucket) throws Exception {

        for (Record record : bucket) {
            putRecordIntoAppropriateBucket(classifyRecord(record));
        }
        return forFurtherProcessing;
    }

    private void putRecordIntoAppropriateBucket(final Record record) {

        if (record.isFullyClassified()) {
            successfullyClassified.addRecordToBucket(record);
        }
        else {
            forFurtherProcessing.addRecordToBucket(record);
        }

    }

    private Record classifyRecord(final Record record) throws Exception {

        for (String description : record.getDescription()) {
            if (!record.descriptionIsClassified(description)) {
                Set<Classification> result = classifyDescription(description);
                record.addClassificationsToDescription(description, result);
            }
        }
        return record;
    }

    private Set<Classification> classifyDescription(final String description) throws Exception {

        Set<Classification> result;
        if (recordCache.containsKey(description)) {
            result = recordCache.get(description);
        }
        else {
            TokenSet tokenSet = new TokenSet(description);
            result = resolverPipeline.classify(tokenSet);
            recordCache.put(description, result);
        }
        return result;
    }
}
