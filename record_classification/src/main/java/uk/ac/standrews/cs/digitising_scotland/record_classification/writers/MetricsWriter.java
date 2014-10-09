package uk.ac.standrews.cs.digitising_scotland.record_classification.writers;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.AbstractConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.CodeMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.InvertedSoftConfusionMatrix;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.analysis_metrics.ListAccuracyMetrics;
import uk.ac.standrews.cs.digitising_scotland.record_classification.datastructures.code.CodeIndexer;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

import com.google.common.io.Files;

public class MetricsWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsWriter.class);

    private ListAccuracyMetrics accuracyMetrics;

    private String baseFolder;

    private CodeIndexer codeIndex;

    public MetricsWriter(ListAccuracyMetrics accuracyMetrics, String baseFolder, CodeIndexer codeIndex) {

        this.accuracyMetrics = accuracyMetrics;
        this.baseFolder = baseFolder;
        this.codeIndex = codeIndex;
    }

    public void write(String typeIdentifier, String bucketIdentifier) {

        final String codeStatsPath = baseFolder + "/Reports/" + typeIdentifier;

        final String reportspath = baseFolder + "/Reports/";
        final String matrixDataPath = baseFolder + "/Data/" + typeIdentifier + "/classificationCountMatrix.csv";
        final String matrixImagePath = "classificationMatrix";

        final String strictCodeStats = "strictCodeStats-";
        final String softCodeStats = "softCodeStats-";
        final String fileExtension = ".csv";

        final String strictCodeStatsPath = baseFolder + "/Data/" + typeIdentifier + "/" + strictCodeStats + bucketIdentifier + fileExtension;
        final String strictCodePath = strictCodeStats + bucketIdentifier;
        final String softCodeStatsPath = baseFolder + "/Data/" + typeIdentifier + "/" + softCodeStats + bucketIdentifier + fileExtension;
        final String softCodePath = softCodeStats + bucketIdentifier;

        CodeMetrics codeMetrics = accuracyMetrics.getMetrics();

        LOGGER.info(codeMetrics.getMicroStatsAsString());
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());

        codeMetrics.writeStats(strictCodeStatsPath);

        String mdSummary = accuracyMetrics.generateMarkDownSummary(baseFolder, codeStatsPath);

        final String markdownSummaryPath = baseFolder + "/Reports/" + typeIdentifier + "/summary-" + bucketIdentifier + ".md";

        createDirectories(markdownSummaryPath);

        Utils.writeToFile(mdSummary, markdownSummaryPath, true);
        final int[][] overUnderPredictionMatrix = accuracyMetrics.getOverUnderPredictionMatrix();
        Utils.writeToFile(accuracyMetrics.getMatrixAsString(overUnderPredictionMatrix, ",", false), baseFolder + "/Data/" + typeIdentifier + "/classificationCountMatrix-" + bucketIdentifier + ".csv");

        LOGGER.info(codeMetrics.getMicroStatsAsString());
        codeMetrics.writeStats(strictCodeStatsPath);
        LOGGER.info(strictCodeStatsPath + ": " + codeMetrics.getTotalCorrectlyPredicted());
        accuracyMetrics.generateMarkDownSummary(baseFolder, codeStatsPath);

        AbstractConfusionMatrix invertedConfusionMatrix = new InvertedSoftConfusionMatrix(accuracyMetrics.getBucket(), codeIndex);

        try {
            runRscript("src/main/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
            runRscript("src/main/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
            runRscript("src/main/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void createDirectories(final String markdownSummaryPath) {

        try {
            Files.createParentDirs(new File(markdownSummaryPath));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void runRscript(final String pathToRScript, final String dataPath, final String reportsPath, final String imageName) throws IOException {

        if (!isRinstalled()) { return; }

        String imageOutputPath = reportsPath + imageName + ".png";
        String command = "Rscript " + pathToRScript + " " + dataPath + " " + imageOutputPath;
        LOGGER.info(Utils.executeCommand(command));
    }

    public boolean isRinstalled() {

        final String pathToScript = Utils.class.getResource("/scripts/checkScript.sh").getFile();
        String checkSystemForR = "sh " + pathToScript + " RScript";
        final String executeCommand = Utils.executeCommand(checkSystemForR);
        LOGGER.info(executeCommand);

        if (executeCommand.equals("RScript required but it's not installed.  Aborting.\n")) {
            LOGGER.error("Stats not generated. R or RScript is not installed.");
            // System.exit(2);FIXME check if this exit is really required.
            return false;
        }
        return true;
    }
}
