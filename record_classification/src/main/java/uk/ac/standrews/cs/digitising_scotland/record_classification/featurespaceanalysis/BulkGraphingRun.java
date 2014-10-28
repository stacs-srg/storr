package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

/**
 *
 * Created by fraserdunlop on 27/10/2014 at 15:57.
 */
public class BulkGraphingRun {

    private static File dataSet1;
    private static File dataSet2;
    private final FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser;
    private final File topDir;
    private Path pathToRScript = new File(this.getClass().getResource("/FeatureSpaceAnalysisPlotter.R").getPath()).toPath();
    private static final String statsFileName = "stats.csv";
    private final FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser;
    private Path pathToCopyOfRScript;

    /**
     * @param args 0,1,2,3
     * @throws Exception
     * @throws CodeNotValidException
     */
    public static void main(String args[]) throws Exception, CodeNotValidException {

        File topDir = new File(args[2]);
        if(topDir.exists()){
            throw new Exception("Output path supplied would overwrite a directory which already exists. Please choose something different.");
        }
        CodeDictionary codeDictionary = new CodeDictionary(new File(args[1]));
        BucketGenerator bucketGen = new BucketGenerator(codeDictionary);
        dataSet1 = new File(args[0]);
        Bucket dataSet1 = bucketGen.generateTrainingBucket(BulkGraphingRun.dataSet1);
//        dataSet2 = new File(args[1]);
//        Bucket dataSet2 = bucketGen.generateTrainingBucket(BulkGraphingRun.dataSet2);
        Bucket[] buckets = randomlyAssignToTrainingAndPrediction(dataSet1,0.8);
        FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser = new FeatureSpaceAnalyser(buckets[0]);
        FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser = new FeatureSpaceAnalyser(buckets[1]);

        new BulkGraphingRun(topDir, dataSet1FeatureSpaceAnalyser,dataSet2FeatureSpaceAnalyser);
    }

    private static Bucket[] randomlyAssignToTrainingAndPrediction(final Bucket bucket, final double trainingRatio) {

        Bucket[] buckets = initBuckets();

        for (Record record : bucket) {
            if (Math.random() < trainingRatio) {
                buckets[0].addRecordToBucket(record);
            }
            else {
                buckets[1].addRecordToBucket(record);
            }
        }
        return buckets;
    }

    private static Bucket[] initBuckets() {

        Bucket[] buckets = new Bucket[2];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new Bucket();
        }
        return buckets;
    }

    public BulkGraphingRun(File topDir,  FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser, FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser) throws Exception {

        this.topDir = topDir;
        pathToCopyOfRScript = new File(topDir+"/RScript.R").toPath();
        this.dataSet1FeatureSpaceAnalyser = dataSet1FeatureSpaceAnalyser;
        this.dataSet2FeatureSpaceAnalyser = dataSet2FeatureSpaceAnalyser;
        run();

        //iterate over codes, make csv for each, put csv in new dir titled as code


    }

    private void copyRScript() throws IOException {
        Files.copy(pathToRScript,pathToCopyOfRScript);
    }

    private void run() throws Exception {
        makeAndCheckDir(topDir);
        copyRScript();
        populateWithCodeAnalysis();


    }

    private void populateWithCodeAnalysis() throws Exception {
        HashSet<Code> codes = new HashSet<>(dataSet1FeatureSpaceAnalyser.codes());
        codes.addAll(dataSet2FeatureSpaceAnalyser.codes());
        for(Code code : codes){
            makeCodeFolderContainingAnalysisCSV(code);
        }
    }

    private void makeCodeFolderContainingAnalysisCSV(Code code) throws Exception {
        File newDir = new File(topDir.getAbsolutePath() + "/" + code.getCodeAsString());
        makeAndCheckDir(newDir);
        putCSVInDir(newDir, code);
        String codeName = code.getCodeAsString() + "_-_\"" + code.getDescription().replaceAll(" ","_") + "\""; //tODO get description into graph
        runRPlotScriptOnDir(newDir.getAbsolutePath(), codeName);
    }

    private void putCSVInDir(final File newDir, Code code) throws FileNotFoundException, UnsupportedEncodingException {

        DataFileMakerThingy2 dataFileMakerThingy = new DataFileMakerThingy2(dataSet1FeatureSpaceAnalyser,dataSet2FeatureSpaceAnalyser);
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(newDir.getAbsolutePath()+"/"+ statsFileName))))){

                writer.write(dataFileMakerThingy.make(code));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runRPlotScriptOnDir(String dataPath1, String codeName) {
        String imageOutputPath = dataPath1 + "/plot" + ".png";
        String command = "Rscript " + pathToCopyOfRScript.toString() + " " + dataPath1 + "/"+ statsFileName + " " + imageOutputPath + " " + codeName;
        System.out.println(Utils.executeCommand(command));
    }

    private void makeAndCheckDir(final File file) throws Exception {
        boolean isDirectoryCreated = file.mkdir();
        if (!isDirectoryCreated) {
            throw new Exception("Cannot create top level directory.");
    }
    }
}
