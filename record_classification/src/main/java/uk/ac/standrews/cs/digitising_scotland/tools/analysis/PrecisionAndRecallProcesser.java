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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

import uk.ac.standrews.cs.digitising_scotland.tools.ReaderWriterFactory;
import uk.ac.standrews.cs.digitising_scotland.tools.Utils;

/**
 * Class for processing output files from the machine learning pipeline and calculating micro/macro precision/recall stats.
 * @author jkc25
 *
 */
public class PrecisionAndRecallProcesser extends FileProcessor {

    @Override
    void process(final Path file) throws NumberFormatException, IOException {

        boolean isFirst = true;
        double total = 0;
        double sumtp = 0;
        double sumfp = 0;
        double sumfn = 0;
        double sump = 0;
        double sumr = 0;

        BufferedReader br = ReaderWriterFactory.createBufferedReader(file.toFile());
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] lineSplit = line.split(Utils.getCSVComma());
            if (!isFirst && lineSplit.length == 13) {
                total++;
                double tp = Double.parseDouble(lineSplit[1]);
                double fp = Double.parseDouble(lineSplit[3]);
                double fn = Double.parseDouble(lineSplit[4]);
                double precision = Double.parseDouble(lineSplit[5]);
                double recall = Double.parseDouble(lineSplit[6]);

                sumtp += tp;
                sumfp += fp;
                sumfn += fn;
                sump += precision;
                sumr += recall;
            }
            isFirst = false;

        }

        double microPrecision = sumtp / (sumtp + sumfp);
        double microRecall = sumtp / (sumtp + sumfn);
        double macroPrecision = sump / total;
        double macroRecall = sumr / total;

        printResults(microPrecision, microRecall, macroPrecision, macroRecall);

    }

    private void printResults(final double microPrecision, final double microRecall, final double macroPrecision, final double macroRecall) {

        System.out.println();
        System.out.println("microPrecision: " + microPrecision);
        System.out.println("microRecall: " + microRecall);
        System.out.println("macroPrecision: " + macroPrecision);
        System.out.println("macroRecall: " + macroRecall);
        System.out.println();
    }
}
