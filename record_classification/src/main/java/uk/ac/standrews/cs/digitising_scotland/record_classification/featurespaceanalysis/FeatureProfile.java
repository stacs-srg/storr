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
package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import java.util.HashMap;

/**
 * Data structure for holding information on feature profiles. A
 * FeatureProfile belongs to a Code and is contained in a CodeProfile.
 * It contains information on the feature's count in the records which
 * predict the code as well as information on the code's distribution in
 * the data set.
 * Created by fraserdunlop on 24/10/2014 at 12:26.
 */
public class FeatureProfile {

    /**
     * The feature.
     */
    private final String feature;

    /**
     * The number of times the feature predicts the code in the data set.
     */
    private final int inCodeFeatureCount;

    /**
     * The number of times the feature occurs in total across all codes in the data set.
     */
    private final int countInTotal;

    /**
     * A map containing codes mapped to the number of times the feature predicts that code in the
     * data set.
     */
    private final HashMap<Code, Integer> profile;

    /**
     * Feature frequency -inverse code frequency.
     */
    private final double ffIcf;

    FeatureProfile(String feature,double ffIcf, int inCodeFeatureCount ,int countInTotal, HashMap<Code, Integer> profile) {
        this.feature = feature;
        this.ffIcf = ffIcf;
        this.inCodeFeatureCount = inCodeFeatureCount;
        this.countInTotal = countInTotal;
        this.profile = profile;
    }

    public int getInCodeFeatureCount() {
        return inCodeFeatureCount;
    }

    public int getCountInTotal() {
        return countInTotal;
    }


    public HashMap<Code, Integer> getProfile() {
        return profile;
    }

    public String getFeature() {
        return feature;
    }

    public double getFfIcf() {
        return ffIcf;
    }
}