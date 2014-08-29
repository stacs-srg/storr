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

import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.RestrictedDistribution;

public abstract class DistributionLogger<Value> extends Logger<Value> {

    
    protected int minXValue;
    protected int maxXValue;
    
    protected final static String FWD_SLASH = "/";
    protected final static String BCK_SLASH = "\\";
    protected String filePath;
    
    protected RestrictedDistribution<Value> relatedDistribution;
    

    public abstract void outputToGnuPlotFormat(int year, String fileName);
    
    
    protected int[] counts;
    
    protected Value[] xLabels;    
    
    public abstract void incCountFor(Enum<?> xLabel) throws NotSetUpAtClassInitilisationException;
    public abstract void incCountFor(Integer xLabel);
}
