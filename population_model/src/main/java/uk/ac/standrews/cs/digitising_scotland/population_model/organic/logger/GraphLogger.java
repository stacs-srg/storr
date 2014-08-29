/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module population_model.
 *
 * population_model is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * population_model is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with population_model. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.population_model.organic.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class GraphLogger extends Logger<Integer>{

    protected int[] values;

    protected String fileName;
    protected String graphTitle;
    protected String yLabel;
    
    public GraphLogger(int minXValue, int maxXValue, String fileName, String graphTitle, String yLabel) {
        this.fileName = fileName;
        this.graphTitle = graphTitle;
        this.yLabel = yLabel;
        this.minXValue = minXValue;
        this.maxXValue = maxXValue;
        values = new int[maxXValue - minXValue];
    }
    
    public void log(int xValue, int yValue) {
        values[xValue - minXValue] = yValue;
    }
    
    public void outputToGnuPlotFormat() {
        PrintWriter writer;
        try {
            filePath = "src/main/resources/output/gnu/" + fileName + ".dat";
            writer = new PrintWriter(filePath, "UTF-8");
            filePath = new File("").getAbsolutePath() + "/" + filePath;
            writer.println("# This file is called " + fileName + ".dat");
            writer.println("# X    Y");
            for (int i = 0; i < values.length; i++) {
                writer.println((i + minXValue) + "    " + values[i]);
            }
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.out.println("_________CAUGHT__________");
            System.out.println(e.getMessage());
        }
        
    }


    public void generateGnuPlotScriptLines(PrintWriter writer) {
        writer.println("set title \"" + graphTitle + "\"");
        writer.println("set xlabel \"Year\"");
        writer.println("set ylabel \"" + yLabel + "\"");
        writer.println(generateGnuPlotPlottingScript());
    }

    @Override
    public String generateGnuPlotPlottingScript() {
        return "plot \"" + filePath.replace(BCK_SLASH, FWD_SLASH) + "\" using 1:2 with line";
    }

}
