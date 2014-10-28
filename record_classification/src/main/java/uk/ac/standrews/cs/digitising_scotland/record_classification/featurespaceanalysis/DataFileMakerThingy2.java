package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * Created by fraserdunlop on 28/10/2014 at 11:27.
 */
public class DataFileMakerThingy2 {
    private final FeatureSpaceAnalyser fsa1;
    private final FeatureSpaceAnalyser fsa2;
    private int outputLimit =15;

    public DataFileMakerThingy2(FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser, FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser) {
        this.fsa1 = dataSet1FeatureSpaceAnalyser;
        this.fsa2 = dataSet2FeatureSpaceAnalyser;
    }

    public String make(Code code) {
        Set<String> features = new HashSet<>();
        CodeProfile codeProfile1 = getCodeProfile(fsa1,code,features);
        CodeProfile codeProfile2 = getCodeProfile(fsa2, code, features);
        StringBuilder sb = new StringBuilder();
        List<String> featureList = new ArrayList<>();
        if(codeProfile1!=null) {
            featureList = sortFeatures(codeProfile1, features);
        }else if(codeProfile2!=null){
            featureList = sortFeatures(codeProfile2, features);
        }
        Iterator<String> iterator = featureList.iterator();
        int i = 0;
        while(iterator.hasNext() && i < outputLimit){
            i++;
            sb.append((new TokenSet(iterator.next())).toString().replaceAll("'","")); //Regex replace apostrophes as they mess up R's file reader.
            if(iterator.hasNext() && i < outputLimit)
                sb.append(",");
            else sb.append("\n");
        }
        appendScoreLine(codeProfile1, featureList, sb);
        appendScoreLine(codeProfile2, featureList, sb);

        return sb.toString();
    }

    private List<String> sortFeatures(CodeProfile codeProfile, Collection<String> features) {
        List<String> sorted = new ArrayList<>(features);
        sorted.sort(new featureComparator(codeProfile));
        return sorted;
    }

    private class featureComparator implements Comparator<String>{

        private CodeProfile codeProfile;

        public featureComparator(CodeProfile codeProfile){
            this.codeProfile = codeProfile;
        }

        @Override
        public int compare(String feature1, String feature2) {
            double fficf1 = getFfIcf(getFeatureProfile(feature1));
            double fficf2 = getFfIcf(getFeatureProfile(feature2));
            if(fficf1>fficf2){
                return -1;
            } else if(fficf1<fficf2){
                return 1;
            } else {
                return 0;
            }
        }

        private Double getFfIcf(FeatureProfile profile) {
            Double fficf1;
            if(profile!=null) {
                 fficf1 = profile.getFfIcf();
            } else {
                fficf1 = 0.0;
            }
            return fficf1;
        }

        private FeatureProfile getFeatureProfile(String feature1) {
            FeatureProfile profile =  null;
            if(codeProfile.contains(feature1)) {
                profile = codeProfile.getProfile(feature1);
            }
            return profile;
        }
    }

    private CodeProfile getCodeProfile(FeatureSpaceAnalyser fsa, Code code, Set<String> features) {
        CodeProfile codeProfile2 = null;
        if(fsa.contains(code)) {
            codeProfile2= fsa.getAnalysis(code);
            features.addAll(codeProfile2.getFeatures());
        }
        return codeProfile2;
    }

    private void appendScoreLine(CodeProfile codeProfile1, List<String> features, StringBuilder sb) {
        Iterator<String> iterator;
        iterator = features.iterator();
        DecimalFormat df = new DecimalFormat("#0.0000");
        int i = 0;
        while(iterator.hasNext() && i < outputLimit){
            i++;
            String feature = iterator.next();
            if(codeProfile1 != null && codeProfile1.getFeatures().contains(feature)){
                double ffIcf = codeProfile1.getProfile(feature).getFfIcf();
                sb.append(df.format(ffIcf));
            } else {
                sb.append(0.0);
            }
            if(iterator.hasNext() && i < outputLimit)
                sb.append(",");
            else
                sb.append("\n");
        }
    }
}
