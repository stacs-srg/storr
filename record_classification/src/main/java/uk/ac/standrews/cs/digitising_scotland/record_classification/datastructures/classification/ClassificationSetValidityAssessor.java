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
package uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification;

import java.util.ArrayList;
import java.util.Set;

import uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces.ValidityAssessor;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Validity assessor for sets of classifications.
 * A Set<Classification> is said to be valid if the union of the token sets  of the set of
 * Classifications is a member of the power set of the TokenSet.
 * Created by fraserdunlop on 06/10/2014 at 16:37.
 */
public class ClassificationSetValidityAssessor implements ValidityAssessor<Multiset<Classification>, TokenSet> {

    /**
     * Assesses if the union of the token sets  of the set of
     * Classifications is a member of the power set of the TokenSet.
     * @param classifications the set of classifications to assess.
     * @param tokenSet the TokenSet to test against.
     * @return boolean.
     */
    @Override
    public boolean assess(Multiset<Classification> classifications, TokenSet tokenSet) {

        Multiset<TokenSet> tokenSetsFromClassifications = getTokenSetsFromClassifications(classifications);
        TokenSet unionOfTokenSets = getUnionOfTokenSets(tokenSetsFromClassifications);
        return tokenSet.containsAll(unionOfTokenSets) && noTokenAppearsInUnionMoreOftenThanInOriginalSet(tokenSet, unionOfTokenSets);
    }

    /**
     * Gets the union of a multiset of tokenSets.
     * @param tokenSets the tokenSet Multiset to create a union from.
     * @return the union of all sets in the Multiset - a TokenSet.
     */
    private TokenSet getUnionOfTokenSets(final Multiset<TokenSet> tokenSets) {

        Multiset<String> union = HashMultiset.create();
        for (TokenSet tokenSet : tokenSets) {
            for (String token : tokenSet) {
                union.add(token);
            }
        }
        return new TokenSet(union);
    }

    /**
     * Creates a Multiset of the token sets belonging to a set of Classifications.
     * @param classifications the classifications
     * @return the token sets from triple
     */
    private Multiset<TokenSet> getTokenSetsFromClassifications(final Multiset<Classification> classifications) {

        Multiset<TokenSet> tokenSets = HashMultiset.create();
        for (Classification classification : classifications) {
            tokenSets.add(classification.getTokenSet());
        }
        return tokenSets;
    }

    /**
     * Returns true if no token appears in union more often than in original set.
     * False otherwise.
     * @param originalTokenSet a tokenSet.
     * @param union a tokenSet.
     * @return boolean
     */
    private boolean noTokenAppearsInUnionMoreOftenThanInOriginalSet(final TokenSet originalTokenSet, final TokenSet union) {

        for (String token : union) {
            TokenSet originalCopy = new TokenSet(originalTokenSet);
            TokenSet unionCopy = new TokenSet(union);
            originalCopy.retainAll(new TokenSet(token));
            unionCopy.retainAll(new TokenSet(token));
            if (unionCopy.size() > originalCopy.size()) { return false; }
        }
        return true;
    }
}
