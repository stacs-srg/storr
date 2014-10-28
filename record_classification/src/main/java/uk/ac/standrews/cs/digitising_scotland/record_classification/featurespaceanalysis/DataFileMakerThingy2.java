package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.tokens.TokenSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by fraserdunlop on 28/10/2014 at 11:27.
 */
public class DataFileMakerThingy2 {
    private final FeatureSpaceAnalyser fsa1;
    private final FeatureSpaceAnalyser fsa2;

    public DataFileMakerThingy2(FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser, FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser) {
        this.fsa1 = dataSet1FeatureSpaceAnalyser;
        this.fsa2 = dataSet2FeatureSpaceAnalyser;
    }

    public String make(Code code) {
        Set<String> features = new HashSet<>();
        CodeProfile codeProfile1 = null;
        CodeProfile codeProfile2 = null;
        if(fsa1.contains(code)) {
             codeProfile1= fsa1.getAnalysis(code);
            features.addAll(codeProfile1.getFeatures());
        }
        if(fsa2.contains(code)) {
            codeProfile2= fsa2.getAnalysis(code);
            features.addAll(codeProfile2.getFeatures());
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = features.iterator();
        while(iterator.hasNext()){
            sb.append((new TokenSet(iterator.next())).toString());
            if(iterator.hasNext())
                sb.append(",");
            else sb.append("\n");
        }
        appendScoreLine(codeProfile1, features, sb);
        appendScoreLine(codeProfile2, features, sb);

        return sb.toString();
    }

    private void appendScoreLine(CodeProfile codeProfile1, Set<String> features, StringBuilder sb) {
        Iterator<String> iterator;
        iterator = features.iterator();
        while(iterator.hasNext()){
            String feature = iterator.next();
            if(codeProfile1 != null && codeProfile1.getFeatures().contains(feature)){
                sb.append(codeProfile1.getProfile(feature).getFfIcf());
            } else {
                sb.append(0.0);
            }
            if(iterator.hasNext())
                sb.append(",");
            else
                sb.append("\n");
        }
    }
}
