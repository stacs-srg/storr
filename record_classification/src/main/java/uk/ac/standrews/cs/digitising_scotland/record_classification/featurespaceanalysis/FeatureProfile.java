package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import java.util.HashMap;

/**
 * Holder class to simplify handling of data.
 * Created by fraserdunlop on 24/10/2014 at 12:26.
 */
public class FeatureProfile {

    private final String feature;
    private final int inCodeFeatureCount;
    private final int countInTotal;
    private final double proportionInCode;
    private final HashMap<Code, Integer> profile;

    FeatureProfile(String feature, int inCodeFeatureCount, int countInTotal, double proportionInCode, HashMap<Code, Integer> profile) {
        this.feature = feature;
        this.inCodeFeatureCount = inCodeFeatureCount;
        this.countInTotal = countInTotal;
        this.proportionInCode = proportionInCode;
        this.profile = profile;
    }


    public int getInCodeFeatureCount() {
        return inCodeFeatureCount;
    }

    public int getCountInTotal() {
        return countInTotal;
    }

    public double getProportionInCode() {
        return proportionInCode;
    }

    public HashMap<Code, Integer> getProfile() {
        return profile;
    }

    public String getFeature() {
        return feature;
    }
}