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

/**
 * Similarity metric interface.
 * Created by fraserdunlop on 01/10/2014 at 17:04.
 */
public interface SimilarityMetric<K> {

    /**
     * Returns a measure of similarity between objects o1 and o2
     * Similarity should be commutative. i.e. the
     * result of getSimilarity(a,b) should always equal the result of
     * getSimilarity(b,a). It is recommended that getSimilarity returns
     * a double between 0 and 1 where 1 indicates absolute similarity and 0 absolute
     * dissimilarity (it could be that for some argument domains 0 is
     * never returned and is just the theoretical minimum bound of the
     * operator). For example if the similarity metric were based on a measure of
     * distance between two doubles then -inf and +inf may return 0 but in
     * other argument spaces such as string-space it may not be so easy to
     * have a well defined notion of absolute dissimilarity.
     * @param o1 object 1.
     * @param o2 object 2.
     * @return some measure of similarity between objects o1 and o2.
     */
    public abstract double getSimilarity(K o1, K o2);
}
