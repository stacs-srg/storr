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
package uk.ac.standrews.cs.digitising_scotland.record_classification.preprocessor;

import java.util.List;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;

public class RelatedFieldChanger {

    public Bucket updateRelatedFields(final Bucket bucket) {

        Bucket updatedBucket = new Bucket();
        for (Record record : bucket) {
            record = processRecord(record);
            updatedBucket.addRecordToBucket(record);;
        }
        return updatedBucket;
    }

    private Record processRecord(final Record record) {

        String phrase = "";
        final List<String> dList = record.getDescription();
        for (String description : dList) {
            if (description.contains("DUE TO (A)")) {
                phrase = "(A)<" + dList.get(0) + ">";

            }
            if (description.contains("DUE TO (B)")) {
                phrase = "(B)<" + dList.get(1) + ">";

            }
            if (description.contains("DUE TO (C)")) {
                phrase = "(C)<" + dList.get(2) + ">";

            }
            String newDescription = description.replaceAll("(DUE TO \\([ABC]\\))", "DUE TO " + phrase);
            record.updateDescription(description, newDescription);
        }

        return record;
    }
}
