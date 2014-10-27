package uk.ac.standrews.cs.digitising_scotland.tools.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrecisionAndRecallGenerator {

    public static void main(final String[] args) throws IOException {

        File folder = new File("/Users/jkc25/Documents/TempStaging/HISCOCodingExperiments/");
        PrecisionAndRecallGenerator instance = new PrecisionAndRecallGenerator();
        instance.run(Paths.get(folder.getAbsolutePath()));
    }

    private void run(final Path folder) throws IOException {

        processFiles(folder);

    }

    private File[] processFiles(final Path folder) throws IOException {

        FileProcessor processor = new PrecisionAndRecallProcesser();
        Finder finder = new Finder("strictCodeStats-allClassified.csv", processor);
        Files.walkFileTree(folder, finder);
        finder.done();

        return null;
    }

}
