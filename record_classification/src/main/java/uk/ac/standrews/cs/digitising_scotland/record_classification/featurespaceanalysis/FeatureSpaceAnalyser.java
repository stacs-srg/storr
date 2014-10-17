package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.classification.Classification;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import java.util.HashMap;


/**
 * Created by fraserdunlop on 13/10/2014 at 15:59.
 */
public class FeatureSpaceAnalyser {


    HashMap<Code,HashMap<String,Integer>> map;

    public FeatureSpaceAnalyser(Iterable<Record> records) {
        map = buildMap(records);
    }

    private HashMap<Code, HashMap<String, Integer>> buildMap(Iterable<Record> records) {
        HashMap<Code, HashMap<String, Integer>> map1 = new HashMap<>();
        for (Record record : records){
            addToMap(map1, record);
        }
        return map1;
    }

    private void addToMap(HashMap<Code, HashMap<String, Integer>> map1, Record record) {
        for(Classification classification : record.getGoldStandardClassificationSet()){
            addToMap(map1, classification);
        }
    }

    private void addToMap(HashMap<Code, HashMap<String, Integer>> map1, Classification classification) {
        Code code = classification.getCode();
        if (!map1.containsKey(code)) map1.put(code, new HashMap<String, Integer>());
        for (String token : classification.getTokenSet()){
            if(!map1.get(code).containsKey(token)){
                map1.get(code).put(token,1);
            } else {
                int currentValue = map1.get(code).get(token);
                map1.get(code).put(token,currentValue+1);
            }
        }
    }

    public HashMap<String, Integer> featureProfile(Code code) {
        return map.get(code);
    }
}
