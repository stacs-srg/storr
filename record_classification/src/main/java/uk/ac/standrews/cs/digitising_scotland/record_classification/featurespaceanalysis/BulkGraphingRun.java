package uk.ac.standrews.cs.digitising_scotland.record_classification.featurespaceanalysis;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.Code;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeDictionary;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeNotValidException;
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.BucketGenerator;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import java.io.*;
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
    private static final String pathToRScript = "/Users/fraserdunlop/IdeaProjects/DSCleanPull/record_classification/src/main/R/FeatureSpaceAnalysisPlotter.R";
    private static final String statsFileName = "stats.csv";
    private final FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser;

    public static void main(String args[]) throws Exception, CodeNotValidException {

        CodeDictionary codeDictionary = new CodeDictionary(new File(args[2]));
        BucketGenerator bucketGen = new BucketGenerator(codeDictionary);
        dataSet1 = new File(args[0]);
        Bucket dataSet1 = bucketGen.generateTrainingBucket(BulkGraphingRun.dataSet1);
        dataSet2 = new File(args[1]);
        Bucket dataSet2 = bucketGen.generateTrainingBucket(BulkGraphingRun.dataSet2);
        FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser = new FeatureSpaceAnalyser(dataSet1);
        FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser = new FeatureSpaceAnalyser(dataSet2);

        new BulkGraphingRun(new File(args[3]), dataSet1FeatureSpaceAnalyser,dataSet2FeatureSpaceAnalyser);
    }

    public BulkGraphingRun(File topDir,  FeatureSpaceAnalyser dataSet1FeatureSpaceAnalyser, FeatureSpaceAnalyser dataSet2FeatureSpaceAnalyser) throws Exception {
        if(topDir.exists()){
            throw new Exception("Output path supplied would overwrite a directory which already exists. Please choose something different.");
        }
        this.topDir = topDir;
        this.dataSet1FeatureSpaceAnalyser = dataSet1FeatureSpaceAnalyser;
        this.dataSet2FeatureSpaceAnalyser = dataSet2FeatureSpaceAnalyser;
        run();

        //iterate over codes, make csv for each, put csv in new dir titled as code


    }

    private void run() throws Exception {
        makeAndCheckDir(topDir);
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
        runRPlotScriptOnDir(newDir.getAbsolutePath());
    }

    private void putCSVInDir(final File newDir, Code code) throws FileNotFoundException, UnsupportedEncodingException {

        DataFileMakerThingy dataFileMakerThingy1 = new DataFileMakerThingy(dataSet1FeatureSpaceAnalyser);
        DataFileMakerThingy dataFileMakerThingy2 = new DataFileMakerThingy(dataSet1FeatureSpaceAnalyser);
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(newDir.getAbsolutePath()+"/"+ statsFileName))))){
            writer.write(dataFileMakerThingy1.make(code,true,dataSet1.getName()));
            writer.write(dataFileMakerThingy2.make(code,false,dataSet2.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runRPlotScriptOnDir(String dataPath1) {
        String imageOutputPath = dataPath1 + "/plot" + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath1 + "/"+ statsFileName + " " + imageOutputPath;
        System.out.println(Utils.executeCommand(command));
    }

    private void makeAndCheckDir(final File file) throws Exception {
        boolean isDirectoryCreated = file.mkdir();
        if (!isDirectoryCreated) {
            throw new Exception("Cannot create top level directory.");
    }
    }
}
