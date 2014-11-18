/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
        Finder finder = new Finder("strictCodeStats-unique records.csv", processor);
        Files.walkFileTree(folder, finder);
        finder.done();

        return null;
    }

}
