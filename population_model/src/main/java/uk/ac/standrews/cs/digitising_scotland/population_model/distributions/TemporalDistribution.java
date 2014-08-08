/**
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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.model.RandomFactory;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.DateManipulation;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 * Creates a weighted distribution with weights that change over time.
 *
 * @author Victor Andrei (va9@st-andrews.ac.uk)
 */
public class TemporalDistribution implements Distribution<Integer> {

    private HashMap<Integer, WeightedIntegerDistribution> map = new HashMap<Integer, WeightedIntegerDistribution>();
    private static String line;
    private static boolean firstLine = true;
    private static int minimum, maximum, range;
    private Random random = RandomFactory.getRandom();

    private static String CONTEXT_PATH; //absolute path prefix
    private final static String RELATIVE_PATH = "/digitising_scotland/population_model/src/main/resources/distributions/"; //where files should be in the project folders
    private final static String TAB = "\t";
    private final static String COMMENT_INDICATOR = "%";

    private Integer[] keyArray;
    private OrganicPopulation population;
    
    /**
     * Constructor that takes in a file name with the weights information.
     *
     * @param filename
     */
    public TemporalDistribution(OrganicPopulation population, String distributionKey) {
    	this.population = population;
    	
        CONTEXT_PATH = new File("").getAbsolutePath();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(PopulationProperties.getProperties().getProperty(distributionKey)), FileManipulation.FILE_CHARSET))) {

            try {
                do {
                    line = reader.readLine();

                    if (line == null)
                        break;

                    if (line.startsWith(COMMENT_INDICATOR)) {
                        continue;
                    }

                    if (firstLine) {
                        range = Integer.parseInt(line.split(TAB)[0]);
                        minimum = Integer.parseInt(line.split(TAB)[1]);
                        maximum = Integer.parseInt(line.split(TAB)[2]);
                        firstLine = false;
                    } else {

                        try {
                            String[] lineComponents = line.split(TAB);

                            int[] weights = new int[lineComponents.length - 1];
                            int year = Integer.parseInt(lineComponents[0]);

                            for (int i = 1; i < lineComponents.length; i++) {
                                weights[i - 1] = Integer.parseInt(lineComponents[i]);
                            }
//                            System.out.println(year);
//                        	for (int i : weights) {
//                        		System.out.print(weights[i] +  " ");
//                        	}
                            WeightedIntegerDistribution currentDistribution = new WeightedIntegerDistribution(minimum, maximum, weights, random);
                            map.put((int) ((year - OrganicPopulation.getEpochYear()) * OrganicPopulation.getDaysPerYear()) , currentDistribution);
                        } catch (NumberFormatException e) {
                            // do nothing
                        }
                    }
                } while (line != null);

            } catch (Exception e) {
                e.printStackTrace();
                ErrorHandling.exceptionError(e, "Could not process line:" + line);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        Set<Integer> keys = map.keySet();
        ArrayList<Integer> keyList = new ArrayList<>(keys);
        keyArray = keyList.toArray(new Integer[keyList.size()]);
        Arrays.sort(keyArray);
//        for (Integer i : keyArray) {
//        	System.out.println(i);
//        	for (Integer j : map.get(keyArray[i])) {
//        		
//        	}
//        }
    }

    @Override
    public Integer getSample() {
    	int key = keyArray[keyArray.length - 1];
    	int day = population.getCurrentDay();
//    	System.out.println("day: " + day);    	
    	if (keyArray[0] > day) {
    		key = keyArray[0];
    		return map.get(key).getSample();
    	}
    	for (int i = 0; i < keyArray.length - 1; i++) {
    		if (keyArray[i] < day && day < keyArray[i + 1]) {
    			key = keyArray[i];
    			return map.get(key).getSample();
    		}
    	}
//    	System.out.println(key);
    	key = keyArray.length - 1;
        return map.get(key).getSample();
    }
}
