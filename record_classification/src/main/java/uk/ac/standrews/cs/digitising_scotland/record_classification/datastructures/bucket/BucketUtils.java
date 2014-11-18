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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

/**
 * This class contains methods that perform manipulation of {@link Bucket}s. Standard manipulations are
 * calculating the union or intersection of two buckets. Each method will read a number of buckets and
 * return a new bucket containing the result of the calculation.
 * @author jkc25
 *
 */
public final class BucketUtils {

    private BucketUtils() {

    }

    /**
     * Calculates the union of two buckets.
     * This is, every record in bucket B is added to bucket A.
     * @param bucketA First bucket
     * @param bucketB Second bucket
     * @return the union of buckets A and B.
     */
    public static Bucket getUnion(final Bucket bucketA, final Bucket bucketB) {

        final Bucket union = new Bucket();
        union.addCollectionOfRecords(bucketA);
        union.addCollectionOfRecords(bucketB);
        return union;
    }

    /**
     * Calculates the intersection between two buckets. That is a new bucket is constructed containing all
     * records that are members of both bucket A and bucket B.
     *
     * @param bucketA the first bucket
     * @param bucketB the second bucket
     * @return the intersection between bucket A and B
     */
    public static Bucket getIntersection(final Bucket bucketA, final Bucket bucketB) {

        Bucket interesection = new Bucket();
        for (Record record : bucketA) {
            if (bucketB.contains(record)) {
                interesection.addRecordToBucket(record);
            }
        }
        return interesection;
    }

    /**
     * Calculates the complement. The compliment of bucketB in BucketA is all the records of A that are not members of B.
     * This is equivalent of performing BucketA-BucketB on a set level.
     * @param bucketA the bucket to 'subtract' from
     * @param bucketB the bucket to 'subtract'
     * @return the complement
     */
    public static Bucket getComplement(final Bucket bucketA, final Bucket bucketB) {

        Bucket compliment = new Bucket();
        compliment.addCollectionOfRecords(bucketA);
        for (Record record : bucketA) {
            if (bucketB.contains(record)) {
                compliment.remove(record);
            }
        }
        return compliment;
    }

    /**
     * Checks if buckets A and B are disjoint.
     *
     * @param bucketA the bucket a
     * @param bucketB the bucket b
     * @return true, if is disjoint
     */
    public static boolean isDisjoint(final Bucket bucketA, final Bucket bucketB) {

        final Bucket intersection = getIntersection(bucketA, bucketB);
        return intersection.isEmpty();
    }

    /**
     * Checks if bucket A is a subset of bucket B.
     *
     * @param bucketA the bucket a
     * @param bucketB the bucket b
     * @return true, if is subset
     */
    public static boolean isSubset(final Bucket bucketA, final Bucket bucketB) {

        for (Record record : bucketA) {
            if (!bucketB.contains(record)) { return false; }
        }
        return true;
    }

}
