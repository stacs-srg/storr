package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;


/**
 * builds a map of codes to counts of features which predict that code in the set
 * Created by fraserdunlop on 13/10/2014 at 15:59.
 */
public class FeatureSpaceAnalyser {

    HashMap<Code,HashMap<String,Integer>> featureProfiles;

    HashMap<String,Integer> featureDist;
    public FeatureSpaceAnalyser(Iterable<Record> records) {
        featureProfiles = new HashMap<>();
        featureDist = new HashMap<>();
       buildMap(records);
    }

    private void buildMap(Iterable<Record> records) {
        for (Record record : records){
            for(Classification classification : record.getGoldStandardClassificationSet()){
                addToFeatureProfiles(classification);
                addToFeatureDist(classification);
            }
        }
    }

    private void addToFeatureDist(Classification classification) {
        for(String token : classification.getTokenSet()){
            if(!featureDist.containsKey(token)){
                featureDist.put(token,1);
            } else {
                int currentValue = featureDist.get(token);
                featureDist.put(token,currentValue+1);
            }

        }
    }

    private void addToFeatureProfiles(Classification classification) {
        Code code = classification.getCode();
        if (!featureProfiles.containsKey(code)) {
            featureProfiles.put(code, new HashMap<String, Integer>());
        }
        for (String token : classification.getTokenSet()){
            if(!featureProfiles.get(code).containsKey(token)){
                featureProfiles.get(code).put(token,1);
            } else {
                int currentValue = featureProfiles.get(code).get(token);
                featureProfiles.get(code).put(token,currentValue+1);
            }
        }
    }

    public HashMap<String, Integer> featureProfile(Code code) {
        return featureProfiles.get(code);
    }

    public boolean contains(Code code) {
        return featureProfiles.containsKey(code);
    }

    public int featureDist(String feature) {
        return featureDist.get(feature);
    }
}
