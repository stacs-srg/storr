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
package uk.ac.standrews.cs.digitising_scotland.population_model.distributions.temporal;

import uk.ac.standrews.cs.digitising_scotland.population_model.config.PopulationProperties;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.Distribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeDeviationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NegativeWeightException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NormalDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.WeightedIntegerDistribution;
import uk.ac.standrews.cs.digitising_scotland.population_model.organic.OrganicPopulation;
import uk.ac.standrews.cs.digitising_scotland.util.FileManipulation;
import uk.ac.standrews.cs.nds.util.ErrorHandling;

import java.io.BufferedReader;
import java.io.IOException;
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
 * @author Tom Dalton (tsd4@st-andrews.ac.uk)
 */
public abstract class TemporalDistribution<Value> implements ITemporalDistribution<Value> {

	private HashMap<Integer, Distribution<?>> map = new HashMap<Integer, Distribution<?>>();
	private String line;
	private boolean firstLine = true;
	private int minimum, maximum;
	private boolean normal;

	private final static String TAB = "\t";
	private final static String COMMENT_INDICATOR = "%";

	private Integer[] keyArray;

	/**
	 * Constructor that takes in a file name with the weights information.
	 *
	 * @param filename
	 */
	public TemporalDistribution(OrganicPopulation population, String distributionKey, Random random) {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(PopulationProperties.getProperties().getProperty(distributionKey)), FileManipulation.FILE_CHARSET))) {

			while ((line = reader.readLine()) != null) {

				if (line.startsWith(COMMENT_INDICATOR)) {
					continue;
				}

				if (firstLine) {
					minimum = Integer.parseInt(line.split(TAB)[0]);
					maximum = Integer.parseInt(line.split(TAB)[1]);
					if (distributionKey.equals("children_number_of_in_marriage_or_cohab_distributions_filename")) {
						population.setMaximumNumberOfChildrenInFamily(maximum);
					}
					if (line.split(TAB).length > 2 && line.split(TAB)[2].equalsIgnoreCase("NORMAL")) {
						normal = true;
					}
					firstLine = false;
				} else {


						String[] lineComponents = line.split(TAB);
						int year = Integer.parseInt(lineComponents[0]);
						Distribution<?> currentDistribution;
						if (normal) {
							currentDistribution = new NormalDistribution(Double.valueOf(lineComponents[1]), Double.valueOf(lineComponents[2]), random);
						} else {
							int[] weights = new int[lineComponents.length - 1];

							for (int i = 1; i < lineComponents.length; i++) {
								weights[i - 1] = Integer.parseInt(lineComponents[i]);
							}
							currentDistribution = new WeightedIntegerDistribution(minimum, maximum, weights, random);
						}
						map.put((int) ((year - OrganicPopulation.getEpochYear()) * OrganicPopulation.getDaysPerYear()) , currentDistribution);
					
				}
			}


		} catch (NumberFormatException e) {
			ErrorHandling.exceptionError(e, "Could not process line:" + line);
			e.printStackTrace();
		} catch (IOException e) {
			ErrorHandling.exceptionError(e, "IO Exception");
			e.printStackTrace();
		} catch (NegativeWeightException e) {
			ErrorHandling.exceptionError(e, "NegativeWeightException");
			e.printStackTrace();
		} catch (NegativeDeviationException e) {
			ErrorHandling.exceptionError(e, "NegativeDeviationException");
			e.printStackTrace();
		}
		Set<Integer> keys = map.keySet();
		ArrayList<Integer> keyList = new ArrayList<>(keys);
		keyArray = keyList.toArray(new Integer[keyList.size()]);
		Arrays.sort(keyArray);
	}

	protected Integer getIntSample(int date) {
		int key = keyArray[keyArray.length - 1];
		Integer returnValue;
		if (keyArray[0] > date) {
			key = keyArray[0];
		}
		for (int i = 0; i < keyArray.length - 1; i++) {
			if (keyArray[i] < date && date < keyArray[i + 1]) {
				key = keyArray[i];
			}
		}

		do {
			returnValue = Double.valueOf(map.get(key).getSample().toString()).intValue();
		} while (returnValue > maximum || returnValue < minimum);

		return returnValue;
	}

	@Override
	public abstract Value getSample();

	public abstract Value getSample(int date);
}
