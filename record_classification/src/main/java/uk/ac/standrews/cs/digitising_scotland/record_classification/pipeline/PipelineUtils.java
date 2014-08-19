package uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.AbstractConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.InvertedSoftConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.StrictConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.bucket.Bucket;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeFactory;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeTriple;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.records.Record;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.DataClerkingWriter;
import uk.ac.standrews.cs.digitising_scotland.record_classification.writers.FileComparisonWriter;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

public class PipelineUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineUtils.class);

    protected static void generateActualCodeMappings(final Bucket bucket) {

        HashMap<String, Integer> codeMapping = new HashMap<>();
        for (Record record : bucket) {
            for (CodeTriple currentCodeTriple : record.getGoldStandardClassificationSet()) {
                codeMapping.put(currentCodeTriple.getCode().getCodeAsString(), 1);
            }
        }

        StringBuilder sb = new StringBuilder();
        Set<String> keySet = codeMapping.keySet();

        for (String key : keySet) {
            sb.append(key + "\t" + key + "\n");
        }
        //    sb.append("\n");
        File codeFile = new File("target/customCodeMap.txt");
        Utils.writeToFile(sb.toString(), codeFile.getAbsolutePath());
        CodeFactory.getInstance().loadDictionary(codeFile);
    }

    protected static void generateAndPrintStats(final Bucket classifiedBucket, final String header, final String experimentalFolderName) throws IOException {

        LOGGER.info(header);
        ListAccuracyMetrics accuracyMetrics = new ListAccuracyMetrics(classifiedBucket);
        accuracyMetrics.prettyPrint(header);
        generateStats(classifiedBucket, accuracyMetrics, experimentalFolderName);
    }

    protected static void generateStats(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String experimentalFolderName) throws IOException {

        final String matrixDataPath = experimentalFolderName + "/Data/classificationCountMatrix.csv";
        final String matrixImagePath = "classificationMatrix";
        final String reportspath = experimentalFolderName + "/Reports/";

        final String strictCodeStatsPath = experimentalFolderName + "/Data/strictCodeStats.csv";
        final String strictCodePath = "strictCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, strictCodeStatsPath, strictCodePath, experimentalFolderName);

        final String softCodeStatsPath = experimentalFolderName + "/Data/softCodeStats.csv";
        final String softCodePath = "softCodeStats";
        printCodeMetrics(bucket, accuracyMetrics, softCodeStatsPath, softCodePath, experimentalFolderName);

        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(bucket);
        double totalCorrectlyPredicted = invertedConfusionMatrix.getTotalCorrectlyPredicted();
        LOGGER.info("Number of predictions too specific: " + totalCorrectlyPredicted);
        LOGGER.info("Proportion of predictions too specific: " + totalCorrectlyPredicted / invertedConfusionMatrix.getTotalPredicted());

        runRscript("src/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
        runRscript("src/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
        runRscript("src/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);

    }

    protected static String printCodeMetrics(final Bucket bucket, final ListAccuracyMetrics accuracyMetrics, final String strictCodeStatsPath, final String codeStatsPath, final String experimentalFolderName) {

        CodeMetrics codeMetrics = new CodeMetrics(new StrictConfusionMatrix(bucket));
        LOGGER.info(codeMetrics.getMicroStatsAsString());
        codeMetrics.writeStats(strictCodeStatsPath);
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(experimentalFolderName, codeStatsPath);
        return strictCodeStatsPath;
    }

    protected static void runRscript(final String pathToRScript, final String dataPath, final String reportsPath, final String imageName) throws IOException {

        // TODO this doesn't look too portable!

        if (!isRinstalled()) { return; }

        String imageOutputPath = reportsPath + imageName + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath + " " + imageOutputPath;
        LOGGER.info(Utils.executeCommand(command));
    }

    protected static boolean isRinstalled() {

        final String pathToScript = Utils.class.getResource("/scripts/checkScript.sh").getFile();
        String checkSystemForR = "sh " + pathToScript + " RScript";
        final String executeCommand = Utils.executeCommand(checkSystemForR);
        LOGGER.info(executeCommand);

        if (executeCommand.equals("RScript required but it's not installed.  Aborting.\n")) {
            LOGGER.error("Stats not generated. R or RScript is not installed.");
            System.exit(2);
            return false;
        }
        return true;
    }

    protected static boolean checkFileType(final File inputFile) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
        String line = br.readLine();
        br.close();
        final int expectedLineLength = 38;
        if (line != null && line.split(Utils.getCSVComma()).length == expectedLineLength) { return true; }
        return false;
    }

    protected static void writeRecords(final Bucket classifiedBucket, final String experimentalFolderName) throws IOException {

        final String nrsReportPath = "/Data/NRSData.txt";
        final DataClerkingWriter writer = new DataClerkingWriter(new File(experimentalFolderName + nrsReportPath));
        for (final Record record : classifiedBucket) {
            writer.write(record);
        }
        writer.close();

        final String comparisonReportPath = "/Data/comaprison.txt";
        final FileComparisonWriter comparisonWriter = new FileComparisonWriter(new File(experimentalFolderName + comparisonReportPath), "\t");
        for (final Record record : classifiedBucket) {
            comparisonWriter.write(record);
        }
        comparisonWriter.close();
    }

}
