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
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NoPermissableValueException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.NotSetUpAtClassInitilisationException;
import uk.ac.standrews.cs.digitising_scotland.population_model.distributions.general.RestrictedDistribution;
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
 *
 * @param <Value> The Value type to be used by the distribution.
 */
public abstract class TemporalDistribution<Value> implements ITemporalDistribution<Value> {

    private HashMap<Integer, RestrictedDistribution<Value>> map = new HashMap<Integer, RestrictedDistribution<Value>>();
    private String line;
    private boolean firstLine = true;
    private int minimum, maximum;
    private boolean normal;

    private static final String TAB = "\t";
    private static final String COMMENT_INDICATOR = "%";

    private Integer[] keyArray;

    /**
     * Constructor for TemporalDistribution. Reads in distributions data for all years from the location specified and creates a mapping of years to distributions.
     * 
     * @param population The instance of the population which the distribution pertains to.
     * @param distributionKey The key specified in the config file as the location of the relevant file.
     * @param random The random to be used.
     * @param handleNoPermissibleValueAsZero Indicates if the distribution is to treat the returning of NoPermissibleValueExceptions as returning a zero value.
     */
    public TemporalDistribution(final OrganicPopulation population, final String distributionKey, final Random random, final boolean handleNoPermissibleValueAsZero) {

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
                    RestrictedDistribution<?> currentDistribution;
                    if (normal) {
                        currentDistribution = new NormalDistribution(Double.valueOf(lineComponents[1]), Double.valueOf(lineComponents[2]), random, minimum, maximum);
                    } else {
                        int[] weights = new int[lineComponents.length - 1];

                        for (int i = 1; i < lineComponents.length; i++) {
                            weights[i - 1] = Integer.parseInt(lineComponents[i]);
                        }
                        currentDistribution = new WeightedIntegerDistribution(minimum, maximum, weights, random, handleNoPermissibleValueAsZero);
                    }
                    map.put((int) ((year - OrganicPopulation.getEpochYear()) * OrganicPopulation.getDaysPerYear()), (RestrictedDistribution<Value>) currentDistribution);
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

    protected Integer getIntSample(final int date) {
        int key = keyArray[keyArray.length - 1];
        Integer returnValue;
        if (keyArray[0] > date) {
            key = keyArray[0];
        }
        for (int i = 0; i < keyArray.length - 1; i++) {
            if (keyArray[i] < date && date < keyArray[i + 1]) {
                key = keyArray[i];
                break;
            }
        }

        do {
            returnValue = Double.valueOf(map.get(key).getSample().toString()).intValue();
        } while (returnValue > maximum || returnValue < minimum);

        return returnValue;
    }

    protected Integer getIntSample(final int date, final int earliestValue, final int latestValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException {
        int key = keyArray[keyArray.length - 1];
        Integer returnValue;
        if (keyArray[0] > date) {
            key = keyArray[0];
        }
        for (int i = 0; i < keyArray.length - 1; i++) {
            if (keyArray[i] < date && date < keyArray[i + 1]) {
                key = keyArray[i];
                break;
            }
        }

        String s = map.get(key).getSample(earliestValue, latestValue).toString();
        returnValue = Double.valueOf(s).intValue();

        return returnValue;
    }

    @Override
    public abstract Value getSample();

    /**
     * Method returns a sample value from the distribution for the specified year which falls within the permissible values range.
     * 
     * @param date The date in days since 1/1/1600 which will be used to select the correct distribution to be used for the given year.
     * @param smallestPermissibleReturnValue The smallest value that the user wishes to be returned from the distribution.
     * @param largestPermissibleReturnValue The largest value that the user wishes to be returned from the distribution.
     * @return The Value that has been sampled from the distribution.
     * @throws NoPermissableValueException Thrown if the distribution does not contain a value within the permissible range.
     * @throws NotSetUpAtClassInitilisationException Thrown if the distribution has not been initlised in the correct way to allow restricted sampling.
     */
    public abstract Value getSample(int date, int smallestPermissibleReturnValue, int largestPermissibleReturnValue) throws NoPermissableValueException, NotSetUpAtClassInitilisationException;

    public Integer[] getMapKeys() {
        return map.keySet().toArray(new Integer[map.keySet().size()]);
    }
    
    public RestrictedDistribution<Value> getDistributionForYear(int year) {
        return (RestrictedDistribution<Value>) map.get(year);
    }
    

}
