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
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.closestmatchmap;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.IClassifier;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

public class StringSimilarityClassifier implements IClassifier<TokenSet, Classification> {

    ClosestMatchMap<String, Classification> map;

    public StringSimilarityClassifier(final ClosestMatchMap<String, Classification> closestMatchMap) {

        map = closestMatchMap;
    }

    @Override
    public Classification classify(final TokenSet tokenSet) {

        final Classification classification = map.get(tokenSet.toString());
        if (classification == null) {
            String s = map.getClosestKey(tokenSet.toString());
            double similarity = map.getSimilarity(tokenSet.toString(), s);
            Classification mostSimilar = map.getClosestMatch(s);
            return new Classification(mostSimilar.getCode(), mostSimilar.getTokenSet(), similarity);
        }
        return classification;
    }
}
