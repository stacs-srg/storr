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
import uk.ac.standrews.cs.digitising_scotland.record_classification.pipeline.PipelineUtils;
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
            PipelineUtils.runRscript("src/main/R/CodeStatsPlotter.R", strictCodeStatsPath, reportspath, strictCodePath);
            PipelineUtils.runRscript("src/main/R/CodeStatsPlotter.R", softCodeStatsPath, reportspath, softCodePath);
            PipelineUtils.runRscript("src/main/R/HeatMapPlotter.R", matrixDataPath, reportspath, matrixImagePath);
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
}
